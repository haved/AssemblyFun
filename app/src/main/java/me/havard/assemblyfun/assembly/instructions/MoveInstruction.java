package me.havard.assemblyfun.assembly.instructions;

import android.util.Log;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;
import me.havard.assemblyfun.assembly.ParseUtil;

/** The Move instruction.
 *
 *  Typing "MOV r0, r1" will
 *  store the value of r1 in r0.
 * Created by Havard on 07.11.2015.
 */
public class MoveInstruction extends Instruction {
    public static final String MNEMONIC = "mov";

    protected int mRd;
    protected FlexibleSecondOperand mFSO;

    @Override
    public void loadFromString(String line, HashMap<String, Integer> registerNames, HashMap<String, Integer> labels) {
        int sStart = ParseUtil.findThenSkipChar(line, ' ', MNEMONIC.length());
        int sLength = line.length();
        for(int charIndex = sStart; charIndex < sLength; charIndex++) {
            if(line.charAt(charIndex)==',') {
                String regName = line.substring(sStart,charIndex);
                if(!registerNames.containsKey(regName))
                    throw new AssemblyException("MoveInstruction.loadFromString() unknownRegisterName", AssemblyException.UNKNOWN_REGISTER_NAME, regName);
                mRd = registerNames.get(regName);
                mFSO = new FlexibleSecondOperand(line.substring(charIndex+1), registerNames);
            }
        }
    }

    @Override
    public void run(AssemblyRunner runner) {
        runner.setRegister(mRd, mFSO.getValue(runner));
    }

    public static class MvnInstruction extends MoveInstruction {
        public static final String MNEMONIC = "mvn";

        @Override
        public void run(AssemblyRunner runner) {
            runner.setRegister(mRd, ~mFSO.getValue(runner));
        }
    }
}
