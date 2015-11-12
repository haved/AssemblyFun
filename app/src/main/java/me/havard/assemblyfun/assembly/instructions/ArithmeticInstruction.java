package me.havard.assemblyfun.assembly.instructions;

import android.util.Log;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;
import me.havard.assemblyfun.assembly.ParseUtil;

/** A class for all instructions that take Rd,Rn and an FSO
 * Created by Havard on 12.11.2015.
 */
public abstract class ArithmeticInstruction extends Instruction {
    private int mRd;
    private int mRn;
    private FlexibleSecondOperand mFSO;

    @Override
    public void loadFromString(String line, HashMap<String, Integer> registerNames) {
        int sStart = ParseUtil.skipChar(line, ' ', line.indexOf(' ') + 1);
        int sLength = line.length();
        for(int charIndex = sStart;charIndex < sLength; charIndex++) {
            if(line.charAt(charIndex)==',') {
                String regName = line.substring(sStart,charIndex);
                if(!registerNames.containsKey(regName))
                    throw new AssemblyException("MoveInstruction.loadFromString() unknownRegisterName", AssemblyException.UNKNOWN_REGISTER_NAME, regName);
                mRd = registerNames.get(regName);
                sStart=ParseUtil.skipChar(line, ' ', charIndex+1);
                break;
            }
        }
        for(int charIndex = sStart; charIndex < sLength; charIndex++) {
            if(line.charAt(charIndex)==',') {
                String regName = line.substring(sStart,charIndex);
                if(!registerNames.containsKey(regName))
                    throw new AssemblyException("MoveInstruction.loadFromString() unknownRegisterName", AssemblyException.UNKNOWN_REGISTER_NAME, regName);
                mRn = registerNames.get(regName);
                mFSO = new FlexibleSecondOperand(line.substring(charIndex+1), registerNames);
            }
        }
    }

    @Override
    public void run(AssemblyRunner runner) {
        runner.setRegister(mRd, getResult(runner.getRegister(mRn), mFSO.getValue(runner)));
    }

    public abstract int getResult(int Rn, int FSO);
}