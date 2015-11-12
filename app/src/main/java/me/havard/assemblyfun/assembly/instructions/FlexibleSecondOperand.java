package me.havard.assemblyfun.assembly.instructions;

import android.util.Log;

import java.util.HashMap;

import me.havard.assemblyfun.AssemblyException;
import me.havard.assemblyfun.assembly.AssemblyRunner;
import me.havard.assemblyfun.assembly.ParseUtil;

/** A class for a Flexible Second Operand
 * Created by Havard on 08.11.2015.
 */
public class FlexibleSecondOperand {
    private static final int IMMEDIATE_VALUE = 0;
    private static final int REGISTER = 1;

    private int mType;

    private int mImmediateValue;
    private int mRn;

    public FlexibleSecondOperand(String text, HashMap<String, Integer> registerNames) {
        text = text.substring(ParseUtil.skipChar(text, ' ', 0));
        if(text.startsWith("#")) {
            try {
                mImmediateValue = ParseUtil.getLongAsInt(ParseUtil.parseImmediateValue(text, 0, 0xFFFFFFFFL));
                boolean valid = false;
                for (int i = 0; i < 32; i += 2) {
                    int rori = Integer.rotateRight(mImmediateValue, i);
                    if(0<=rori && rori<=0xFF) {
                        valid=true;
                        break;
                    }
                }
                if(!valid) {
                    throw new AssemblyException("FlexibleSecondOperand() imm not byte ror by even number", AssemblyException.FSO_IMMEDIATE_VALUE_NOT_FROM_SHIFT, Integer.toString(mImmediateValue));
                }
                mType = IMMEDIATE_VALUE;
            } catch(Exception e) {
                if(e instanceof AssemblyException)
                    throw e;
                throw new AssemblyException("FlexibleSecondOperand() immediateValue not parsed as an int", AssemblyException.FAILED_TO_PARSE_IMMEDIATE_VALUE, text, e.getMessage());
            }
        } else {
            if(!registerNames.containsKey(text))
                throw new AssemblyException("FlexibleSecondOperand() register name not recognized", AssemblyException.UNKNOWN_REGISTER_NAME, text);
            mRn = registerNames.get(text);
            mType = REGISTER;
        }
    }

    public int getValue(AssemblyRunner runner) {
        if(mType==IMMEDIATE_VALUE)
            return mImmediateValue;
        else
            return runner.getRegister(mRn);
    }
}
