package me.havard.assemblyfun.assembly.instructions;

/** An instruction for subtracting two numbers from another
 * Created by Havard on 12.11.2015.
 */
public class RsbInstruction extends ArithmeticInstruction {
    public static final String MNEMONIC = "rsb";

    @Override
    public int getResult(int Rn, int FSO) {
        return FSO-Rn;
    }
}
