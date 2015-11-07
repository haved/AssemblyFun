package me.havard.assemblyfun.assembly.instructions;

import android.util.Log;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;

/** The Move instruction.
 *
 *  Typing "MOV r0, r1" will
 *  store the value of r1 in r0.
 * Created by Havard on 07.11.2015.
 */
public class MoveInstruction extends Instruction {
    public static final String MNEMONIC = "mov ";

    private int mRd;

    @Override
    public void loadFromString(String line, HashMap<String, Integer> registerNames) {
        int sStart = MNEMONIC.length();
        int sLength = line.length();
        for(int charIndex = sStart; charIndex < sLength; charIndex++) {
            if(line.charAt(charIndex)==' ' & sStart == charIndex-1)
                sStart = charIndex+1;
            else if(line.charAt(charIndex)==',') {
                String regName = line.substring(sStart,charIndex);
                if(!registerNames.containsKey(regName))
                    throw new AssemblyException("MoveInstruction.loadFromString() unknownRegisterName", AssemblyException.UNKNOWN_REGISTER_NAME, regName);
                mRd = registerNames.get(regName);

            }
        }
    }

    @Override
    public void run(AssemblyRunner runner) {
        Log.d("Assembly Fun", "r"+mRd + " used to be " + runner.getRegister(mRd));
        runner.setRegister(mRd, runner.getRegister(mRd) + 1);
        Log.d("Assembly Fun", "r" + mRd + " is now " + runner.getRegister(mRd));
    }
}
