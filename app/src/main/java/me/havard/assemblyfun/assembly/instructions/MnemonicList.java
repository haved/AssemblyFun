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
        mnemonics.add(AndInstruction.MNEMONIC);
        instructions.add(AndInstruction.class);
        mnemonics.add(OrrInstruction.MNEMONIC);
        instructions.add(OrrInstruction.class);
        mnemonics.add(EorInstruction.MNEMONIC);
        instructions.add(EorInstruction.class);
        mnemonics.add(BicInstruction.MNEMONIC);
        instructions.add(BicInstruction.class);

        if(BuildConfig.DEBUG && mnemonics.size()!=instructions.size())
            throw new AssertionError();

        mMnemonics = new String[mnemonics.size()];
        mnemonics.toArray(mMnemonics);
        //noinspection unchecked
        mInstructions = new Class[instructions.size()];
        instructions.toArray(mInstructions);
    }

    public static Instruction newCondInstance(String line) {
        if(mMnemonics==null)
            makeList();

        for(int i = 0; i < mMnemonics.length; i++) {
            String mnemonic = mMnemonics[i];
            if(line.length()> mnemonic.length()) {
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

                String condCode = line.substring(mnemonic.length(), line.indexOf(' ', mnemonic.length())).toLowerCase();
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
