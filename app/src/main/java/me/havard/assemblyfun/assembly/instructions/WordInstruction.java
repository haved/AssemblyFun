package me.havard.assemblyfun.assembly.instructions;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;

/** An instruction that can't be run but that holds a word, 32 bits
 * Created by Havard on 06.11.2015.
 */
public class WordInstruction extends Instruction {
    public static final String MNEMONIC = ".word ";

    private int mValue;
    public WordInstruction() {

    }

    public WordInstruction(int value) {
        mValue = value;
    }

    @Override
    public void loadFromString(String s, HashMap<String, Integer> registerNames) {

    }

    @Override
    public void run(AssemblyRunner runner) {
        throw new AssemblyException("WordInstruction.run() Not supposed to be called!", AssemblyException.NOT_RUNNABLE_INSTRUCTION_RUN, getClass().getSimpleName());
    }

    @Override
    public boolean runnable(){
        return false;
    }

    public int getValue(){
        return mValue;
    }
}
