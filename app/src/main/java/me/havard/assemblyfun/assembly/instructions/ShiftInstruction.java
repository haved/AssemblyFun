package me.havard.assemblyfun.assembly.instructions;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;
import me.havard.assemblyfun.assembly.ParseUtil;

/** A class for all instructions that take Rd,Rn and an FSO
 * Created by Havard on 12.11.2015.
 */
public abstract class ShiftInstruction extends Instruction {
    private int mRd=-1;
    private int mRn=-1;
    private int shift=-1;

    @Override
    public void loadFromString(String line, HashMap<String, Integer> registerNames) {
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
                shift = ParseUtil.getLongAsInt(ParseUtil.parseImmediateValue(line.substring(ParseUtil.skipChar(line, ' ', charIndex+1)), 0, 31));
            }
        }
        if(mRd==-1 | mRn==-1 | shift ==-1) {
            throw new AssemblyException("ShiftInstruction.loadFromString()", AssemblyException.LACK_OF_ARGUMENTS, line, "Rd,Rn,FSO");
        }
    }

    @Override
    public void run(AssemblyRunner runner) {
        runner.setRegister(mRd, getResult(runner.getRegister(mRn), shift));
    }

    public abstract int getResult(int Rn, int shift);

    public static class LslShiftInstruction extends ShiftInstruction {
        public static final String MNEMONIC = "lsl";

        @Override
        public int getResult(int Rn, int shift) {
            return Rn<<shift;
        }
    }

    public static class LsrShiftInstruction extends ShiftInstruction {
        public static final String MNEMONIC = "lsr";

        @Override
        public int getResult(int Rn, int shift) {
            return Rn>>>shift;
        }
    }

    public static class AsrShiftInstruction extends ShiftInstruction {
        public static final String MNEMONIC = "asr";

        @Override
        public int getResult(int Rn, int shift) {
            return Rn>>shift;
        }
    }

    public static class RorShiftInstruction extends ShiftInstruction {
        public static final String MNEMONIC = "ror";

        @Override
        public int getResult(int Rn, int shift) {
            return Integer.rotateRight(Rn, shift);
        }
    }
}