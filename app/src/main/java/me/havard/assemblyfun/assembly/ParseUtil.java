package me.havard.assemblyfun.assembly;

import android.util.Log;

import me.havard.assemblyfun.AssemblyException;

/** A helper class for parsing immediate values.
 * Created by Havard on 08.11.2015.
 */
public class ParseUtil {
    public static long parseImmediateValue(String imm, long min, long max) {
        if(imm.startsWith("#")) {
            boolean negative = false;
            if(imm.startsWith("-")) {
                negative = true;
            }
            imm = imm.substring(negative?2:1);
            int base = 10;
            if(imm.startsWith("0b"))
                base = 2;
            else if(imm.startsWith("0x"))
                base=16;
            if(base!=10)
                imm = imm.substring(2);

            long output = Long.parseLong(imm, base);
            if(negative)
                output = -output;

            if(output<min | output>max)
                throw new AssemblyException("ImmediateValue.parseImmediateValue() value out of bounds", AssemblyException.IMMEDIATE_VALUE_NOT_IN_BOUNDS, imm, Long.toString(min), Long.toString(max));

            return output;
        } else {
            throw new AssemblyException("ImmediateValue.parseImmediateValue() imm doesn't start with a '#'", AssemblyException.IMMEDIATE_VALUE_WITHOUT_HASH, imm);
        }
    }

    public static int getLongAsInt(long l) {
        int i = (int)l;
        if(l<0)
            i=-i;
        Log.d("Assembly Fun", "The long "+l+" (" + Long.toString(l, 16) + ") turned into the integer " + i + " (" + Long.toString(i&0xFFFFFFFFL,16)+")");
        return i;
    }

    public static int skipChar(String s, char c, int i) {
        for(;i<s.length();i++) {
            if(s.charAt(i)!=c)
                break;
        }
        return i;
    }
}
