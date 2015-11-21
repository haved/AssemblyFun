package me.havard.assemblyfun.assembly.instructions;

import android.util.Log;

import java.util.ArrayList;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.BuildConfig;
import me.havard.assemblyfun.assembly.ParseUtil;

/** A List of Instruction classes for mnemonics
 * Created by Havard on 06.11.2015.
 */
public class MnemonicList {
    private static String[] mMnemonics;
    private static Class<? extends Instruction>[] mInstructions;

    private static void makeList() {
        ArrayList<String> mnemonics = new ArrayList<>();
        ArrayList<Class<? extends Instruction>> instructions = new ArrayList<>();

        addInstruction(mnemonics, instructions, DebugInstruction.MNEMONIC, DebugInstruction.class);
        addInstruction(mnemonics, instructions, WordInstruction.MNEMONIC, WordInstruction.class);
        addInstruction(mnemonics, instructions, MoveInstruction.MNEMONIC, MoveInstruction.class);
        addInstruction(mnemonics, instructions, MoveInstruction.MvnInstruction.MNEMONIC, MoveInstruction.MvnInstruction.class);
        addInstruction(mnemonics, instructions, AddInstruction.MNEMONIC, AddInstruction.class);
        addInstruction(mnemonics, instructions, SubInstruction.MNEMONIC, SubInstruction.class);
        addInstruction(mnemonics, instructions, RsbInstruction.MNEMONIC, RsbInstruction.class);
        addInstruction(mnemonics, instructions, AndInstruction.MNEMONIC, AndInstruction.class);
        addInstruction(mnemonics, instructions, OrrInstruction.MNEMONIC, OrrInstruction.class);
        addInstruction(mnemonics, instructions, EorInstruction.MNEMONIC, EorInstruction.class);
        addInstruction(mnemonics, instructions, BicInstruction.MNEMONIC, BicInstruction.class);
        addInstruction(mnemonics, instructions, CmpInstruction.MNEMONIC, CmpInstruction.class);
        addInstruction(mnemonics, instructions, ShiftInstruction.LslShiftInstruction.MNEMONIC, ShiftInstruction.LslShiftInstruction.class);
        addInstruction(mnemonics, instructions, ShiftInstruction.LsrShiftInstruction.MNEMONIC, ShiftInstruction.LsrShiftInstruction.class);
        addInstruction(mnemonics, instructions, ShiftInstruction.AsrShiftInstruction.MNEMONIC, ShiftInstruction.AsrShiftInstruction.class);
        addInstruction(mnemonics, instructions, ShiftInstruction.RorShiftInstruction.MNEMONIC, ShiftInstruction.RorShiftInstruction.class);
        addInstruction(mnemonics, instructions, BranchInstruction.MNEMONIC, BranchInstruction.class);

        if(BuildConfig.DEBUG && mnemonics.size()!=instructions.size())
            throw new AssertionError();

        mMnemonics = new String[mnemonics.size()];
        mnemonics.toArray(mMnemonics);
        //noinspection unchecked
        mInstructions = new Class[instructions.size()];
        instructions.toArray(mInstructions);
    }

    private static void addInstruction(ArrayList<String> mnemonics, ArrayList<Class<? extends Instruction>> instructions, String mnemonic, Class<? extends Instruction> instruction) {
        mnemonics.add(mnemonic);
        instructions.add(instruction);
    }

    public static Instruction newCondInstance(String line) {
        if(mMnemonics==null)
            makeList();

        for(int i = 0; i < mMnemonics.length; i++) {
            String mnemonic = mMnemonics[i];
            if(line.length()>=mnemonic.length()) {
                if(mnemonic.endsWith(" "))
                    mnemonic = mnemonic.substring(0, mnemonic.length()-1);
                boolean correct = true;
                for(int charIndex = 0; charIndex < mnemonic.length(); charIndex++)
                    if(mnemonic.charAt(charIndex)!=Character.toLowerCase(line.charAt(charIndex))) {
                        correct = false;
                        break;
                    }
                if(!correct)
                    continue;

                String condCode = line.substring(mnemonic.length(), ParseUtil.indexOfOrEnd(line, ' ', mnemonic.length())).toLowerCase();
                Instruction.ConditionCodes usedCode = null;
                for(Instruction.ConditionCodes code : Instruction.ConditionCodes.values())
                    if(condCode.equals(code.getCode())) {
                        usedCode = code;
                        break;
                    }

                if(usedCode==null)
                    continue;

                try {
                    return mInstructions[i].newInstance().setConditionCode(usedCode);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    throw new AssemblyException("MnemonicList.newCondInstance() instance failed to be created. InstantiationException", AssemblyException.INSTRUCTION_INSTANTIATION_FAILED, mMnemonics[i]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new AssemblyException("MnemonicList.newCondInstance() instance failed to be created. IllegalAccessException", AssemblyException.INSTRUCTION_INSTANTIATION_FAILED, mMnemonics[i]);
                }
            }
        }

        throw new AssemblyException("MnemonicList.newCondInstance() no mnemonic with that name+condition code found", AssemblyException.MNEMONIC_NOT_PARSED, line);
    }
}
