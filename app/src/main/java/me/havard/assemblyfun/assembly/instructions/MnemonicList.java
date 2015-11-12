package me.havard.assemblyfun.assembly.instructions;

import java.util.ArrayList;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.BuildConfig;

/** A List of Instruction classes for mnemonics
 * Created by Havard on 06.11.2015.
 */
public class MnemonicList {
    private static String[] mMnemonics;
    private static Class<? extends Instruction>[] mInstructions;

    private static void makeList() {
        ArrayList<String> mnemonics = new ArrayList<>();
        ArrayList<Class<? extends Instruction>> instructions = new ArrayList<>();

        mnemonics.add(DebugInstruction.MNEMONIC);
        instructions.add(DebugInstruction.class);
        mnemonics.add(WordInstruction.MNEMONIC);
        instructions.add(WordInstruction.class);
        mnemonics.add(MoveInstruction.MNEMONIC);
        instructions.add(MoveInstruction.class);
        mnemonics.add(AddInstruction.MNEMONIC);
        instructions.add(AddInstruction.class);
        mnemonics.add(SubInstruction.MNEMONIC);
        instructions.add(SubInstruction.class);
        mnemonics.add(RsbInstruction.MNEMONIC);
        instructions.add(RsbInstruction.class);

        if(BuildConfig.DEBUG && mnemonics.size()!=instructions.size())
            throw new AssertionError();

        mMnemonics = new String[mnemonics.size()];
        mnemonics.toArray(mMnemonics);
        //noinspection unchecked
        mInstructions = new Class[instructions.size()];
        instructions.toArray(mInstructions);
    }

    public static Instruction newInstance(String line) {
        if(mMnemonics==null)
            makeList();

        for(int i = 0; i < mMnemonics.length; i++) {
            if(line.startsWith(mMnemonics[i])) {
                try {
                    return mInstructions[i].newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    throw new AssemblyException("MnemonicList.newInstance() instance failed to be created. InstantiationException", AssemblyException.INSTRUCTION_INSTANTIATION_FAILED, mMnemonics[i]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new AssemblyException("MnemonicList.newInstance() instance failed to be created. IllegalAccessException", AssemblyException.INSTRUCTION_INSTANTIATION_FAILED, mMnemonics[i]);
                }
            }
        }

        return null;
    }
}
