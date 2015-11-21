package me.havard.assemblyfun.assembly.instructions;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;
import me.havard.assemblyfun.assembly.ParseUtil;

/** An instruction for branching to a label
 * Created by Havard on 21.11.2015.
 */
public class BranchInstruction extends Instruction {
    public static final String MNEMONIC = "b";

    protected int mROMAddress;

    @Override
    public void loadFromString(String line, HashMap<String, Integer> registerNames, HashMap<String, Integer> labels) {
        int start = ParseUtil.findThenSkipChar(line, ' ', MNEMONIC.length());
        String label = line.substring(start);
        if(labels.containsKey(label)) {
            mROMAddress = labels.get(label);
        } else
            throw new AssemblyException("BranchInstruction.loadFromString() label not recognized", AssemblyException.LABEL_NOT_RECOGNIZED, line, label);
    }

    @Override
    public void run(AssemblyRunner runner) {
        runner.setRegister(AssemblyRunner.PROGRAM_COUNTER, runner.getROMStart()+mROMAddress);
    }
}
