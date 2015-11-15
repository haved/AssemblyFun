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
public abstract class GeneralCompareInstruction extends Instruction {
    private int mRn=-1;
    private int mRm=-1;

    @Override
    public void loadFromString(String line, HashMap<String, Integer> registerNames) {
        int sStart = ParseUtil.findThenSkipChar(line, ' ', line.indexOf(' '));
        int sLength = line.length();
        for(int charIndex = sStart; charIndex < sLength; charIndex++) {
            if(line.charAt(charIndex)==',') {
                String regName = line.substring(sStart,charIndex);
                if(!registerNames.containsKey(regName))
                    throw new AssemblyException("GeneralCompareInstruction.loadFromString() unknownRegisterName", AssemblyException.UNKNOWN_REGISTER_NAME, regName);
                mRn = registerNames.get(regName);
                int rmStart = ParseUtil.skipChar(line, ' ', charIndex + 1);
                regName = line.substring(rmStart, line.length());
                mRm = registerNames.get(regName);
            }
        }
        if(mRn==-1 | mRm==-1) {
            throw new AssemblyException("ArithmeticInstruction.loadFromString()", AssemblyException.LACK_OF_ARGUMENTS, line, "Rn,Rm");
        }
    }

    @Override
    public void run(AssemblyRunner runner) {
        long value = getCompare(runner.getRegister(mRn), runner.getRegister(mRm));
        runner.setFlags(value==0, value<0, (value&(1<<31))!=0);
    }

    protected abstract long getCompare(int rN, int rM);
}