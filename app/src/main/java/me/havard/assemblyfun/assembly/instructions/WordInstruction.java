package me.havard.assemblyfun.assembly.instructions;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;

/** An instruction that can't be run but that holds a word, 32 bits
 * Created by Havard on 06.11.2015.
 */
public class WordInstruction extends Instruction {
    public static final String MNEMONIC = ".word ";

    public WordInstruction() {

    }

    public WordInstruction(int value) {
        mValue = value;
    }

    @Override
    public void loadFromString(String s) {

    }

    @Override
    public void run(AssemblyRunner runner) {
        throw new AssemblyException("WordInstruction is never supposed to be run!");
    }

    @Override
    public boolean runnable(){
        return false;
    }

    int mValue;
    public int getValue(){
        return mValue;
    }
}
