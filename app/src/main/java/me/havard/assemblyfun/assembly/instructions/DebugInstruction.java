package me.havard.assemblyfun.assembly.instructions;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;

/** An instruction that logs a message
 * Created by Havard on 06.11.2015.
 */
public class DebugInstruction extends Instruction {
    public static final String MNEMONIC = ".debug ";

    private String mText;

    public DebugInstruction() {}

    public DebugInstruction(String text) { mText = text; }

    @Override
    public void loadFromString(String s, HashMap<String, Integer> registerNames) {
        if(s.length()<=MNEMONIC.length())
            mText = "Debug message";
        else
            mText = s.substring(MNEMONIC.length());
    }

    @Override
    public void run(AssemblyRunner runner) {
        StringBuilder b = new StringBuilder();
        b.append(".debug ").append(mText).append("\n");
        for(int i = 0; i < AssemblyRunner.TOTAL_REGISTERS; i++) {
            b.append("r").append(i).append(":").append(runner.getRegister(i)).append("   ");
        }
        throw new AssemblyException(b.toString(), AssemblyException.DEBUG_ERROR_MESSAGE);
    }
}
