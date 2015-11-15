package me.havard.assemblyfun.assembly.instructions;

/** A class for the cmp instruction. It compares numbers by subtracting.
 * Created by Havard on 15.11.2015.
 */
public class CmpInstruction extends GeneralCompareInstruction {
    public static final String MNEMONIC = "cmp";

    @Override
    protected long getCompare(int rN, int rM) {
        return (rN&0xFFFFFFL)-(rM&0xFFFFFFFFL);
    }
}
