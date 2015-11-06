package me.havard.assemblyfun.assembly;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.R;
import me.havard.assemblyfun.assembly.instructions.Instruction;
import me.havard.assemblyfun.assembly.instructions.MnemonicList;
import me.havard.assemblyfun.assembly.instructions.WordInstruction;

/** A simple implementation of an AssemblyROMProvider
 * Created by Havard on 15.10.2015.
 */
public class SimpleAssemblyROMProvider extends AssemblyROMProvider {

    List<Instruction> mInstructions;

    public SimpleAssemblyROMProvider(String text) {
        try {
            parseText(text);
        }catch(AssemblyException ae) {
            Log.e("Assembly Fun", "AssemblyException thrown in SimpleAssemblyROMProvider.parseText()", ae);
        }
    }

    public void parseText(String text) {
        mInstructions = new ArrayList<>();
        int textLength = text.length();
        int lineStart=0;
        for(int charIndex = 0; charIndex < textLength; charIndex++) {
            if(charIndex==lineStart && text.charAt(charIndex)==' ')
                lineStart = charIndex+1;
            if(text.charAt(charIndex)=='\n') { //Parse a line!
                String line = text.substring(lineStart, charIndex);
                lineStart = charIndex+1;
                parseLine(line);
            }
        }
        if(lineStart<textLength)
            parseLine(text.substring(lineStart));
    }

    private void parseLine(String line) {
        Instruction instruction = MnemonicList.newInstance(line);
        if(instruction!=null) {
            instruction.loadFromString(line);
            mInstructions.add(instruction);
        }
    }

    @Override
    public Instruction getInstruction(int address) {
        if(address%4!=0)
            throw new AssemblyException("SimpleAssemblyROMProvider.getInstruction instruction_alignment address was " + address, R.string.message_error_instruction_alignment);
        int instIndex = address/4;
        if(instIndex<0|instIndex>= mInstructions.size())
            return null;
        return mInstructions.get(instIndex);
    }

    @Override
    public byte getROMByte(int address) {
        int instIndex = address/4;
        if(instIndex<0|instIndex>= mInstructions.size())
            throw new AssemblyException("SimpleAssemblyROMProvider.getROMByte rom_out_of_bounds address was " + address, R.string.message_error_rom_out_of_bounds);
        Instruction instruction = mInstructions.get(instIndex);
        if(instruction instanceof WordInstruction) {
            return (byte)(((WordInstruction)instruction).getValue()>>((address%4)*8)&0xFF);
        }
        throw new AssemblyException("SimpleAssemblyROMProvider.getROMByte rom_address_not_byte address was " + address, R.string.message_error_rom_address_not_byte);
    }

    @Override
    public int getROMWordCount() {
        return mInstructions.size();
    }

    @Override
    public int getROMSizeInBytes() {
        return getROMWordCount()*4;
    }
}
