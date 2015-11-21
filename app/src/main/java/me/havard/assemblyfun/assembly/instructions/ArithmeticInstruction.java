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
    private int mRd=-1;
    private int mRn=-1;
    private FlexibleSecondOperand mFSO=null;

    @Override
    public void loadFromString(String line, HashMap<String, Integer> registerNames, HashMap<String, Integer> labels) {
        int sStart = ParseUtil.findThenSkipChar(line, ' ', line.indexOf(' '));
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
        if(mRd==-1 | mRn==-1 | mFSO == null) {
            throw new AssemblyException("ArithmeticInstruction.loadFromString()", AssemblyException.LACK_OF_ARGUMENTS, line, "Rd,Rn,FSO");
        }
    }

    @Override
    public void run(AssemblyRunner runner) {
        runner.setRegister(mRd, getResult(runner.getRegister(mRn), mFSO.getValue(runner)));
    }

    public abstract int getResult(int Rn, int FSO);
}