package me.havard.assemblyfun.assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.instructions.Instruction;
import me.havard.assemblyfun.assembly.instructions.MnemonicList;
import me.havard.assemblyfun.assembly.instructions.WordInstruction;

/** A simple implementation of an AssemblyROMProvider
 * Created by Havard on 15.10.2015.
 */
public class SimpleAssemblyROMProvider extends AssemblyROMProvider {

    List<Instruction> mInstructions;
    HashMap<String, Integer> mRegisterNames;
    HashMap<String, Integer> mLabels;

    public SimpleAssemblyROMProvider(String text) {
        mRegisterNames = getRegisterNameChart();
        mLabels = new HashMap<>();
        parseText(text);
    }

    public void parseText(String text) {
        mInstructions = new ArrayList<>();
        int textLength = text.length();
        int lineStart=0;
        int lineNumber = 1;
        for(int charIndex = 0; charIndex < textLength; charIndex++) {
            if(charIndex==lineStart && text.charAt(charIndex)==' ')
                lineStart = charIndex+1;
            if(text.charAt(charIndex)=='\n') { //Parse a line!
                String line = text.substring(lineStart, charIndex);
                lineStart = charIndex+1;
                parseLine(line, lineNumber);
                lineNumber++;
            }
            if(text.charAt(charIndex)==';') { //Parse a line before a comment!
                if(lineStart<charIndex) {
                    String line = text.substring(lineStart, charIndex);
                    parseLine(line, lineNumber);
                }
                for(int i = charIndex; i <= textLength; i++) {
                    if(i==textLength || text.charAt(i)=='\n') {
                        lineStart = i+1;
                        charIndex = i;
                        lineNumber++;
                        break;
                    }
                }
            }
        }
        if(lineStart<textLength) //Parse a final line if there is one without any line break after it.
            parseLine(text.substring(lineStart), lineNumber);
    }

    private void parseLine(String line, int lineNumber) {
        line = line.trim();
        if(line.length()==0)
            return;
        if(line.contains(":")) {
            int colon = line.indexOf(':');
            if(colon<line.length()-1)
                throw new AssemblyException("SimpleAssemblyROMProvider.parseLine() chars after colon in label definition", AssemblyException.JUNK_ON_END_OF_LABEL_DEFINE_LINE, line);
            else if(colon<=0)
                throw new AssemblyException("SimpleAssemblyROMProvider.parseLine() no chars before colon in label definition", AssemblyException.EMPTY_LABEL, line);

            mLabels.put(line.substring(0, colon), mInstructions.size() * 4); //This puts the address of the next instruction added to the label.
            return;
        }
        try {
            Instruction instruction = MnemonicList.newCondInstance(line);
            if (instruction != null) {
                instruction.loadFromString(line, mRegisterNames, mLabels);
                mInstructions.add(instruction);
            }
        } catch(AssemblyException ae) {
            ae.setLineNumber(lineNumber);
            throw ae;
        }
    }

    @Override
    public Instruction getInstruction(int address) {
        if(address%4!=0)
            throw new AssemblyException("SimpleAssemblyROMProvider.getInstruction() address not aligned", AssemblyException.INSTRUCTION_ADDRESS_NOT_ALIGNED, Integer.toString(address));
        int instIndex = address/4;
        if(instIndex<0|instIndex>= mInstructions.size())
            return null;
        return mInstructions.get(instIndex);
    }

    @Override
    public byte getROMByte(int address) {
        int instIndex = address/4;
        if(instIndex<0|instIndex>= mInstructions.size())
            throw new AssemblyException("SimpleAssemblyROMProvider.getROMByte() address out of bounds", AssemblyException.ROM_BYTE_OUT_OF_BOUNDS, Integer.toString(address));
        Instruction instruction = mInstructions.get(instIndex);
        if(instruction instanceof WordInstruction) {
            return (byte)(((WordInstruction)instruction).getValue()>>((address%4)*8)&0xFF);
        }
        throw new AssemblyException("SimpleAssemblyROMProvider.getROMByte() not a WordInstruction at address", AssemblyException.ROM_BYTE_NOT_A_WORD_INSTRUCTION, Integer.toString(address));
    }

    @Override
    public int getROMWordCount() {
        return mInstructions.size();
    }

    @Override
    public int getROMSizeInBytes() {
        return getROMWordCount()*4;
    }

    private static HashMap<String, Integer> getRegisterNameChart() {
        HashMap<String, Integer> map = new HashMap<>();

        for(int i = 0; i < AssemblyRunner.TOTAL_REGISTERS; i++) {
            map.put("r"+i, i);
        }
        map.put("lr", AssemblyRunner.LINK_REGISTER);
        map.put("sp", AssemblyRunner.STACK_POINTER);
        map.put("pc", AssemblyRunner.PROGRAM_COUNTER);

        return map;
    }
}
