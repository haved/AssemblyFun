package me.havard.assemblyfun.assembly.instructions;

/** An instruction for subtracting to numbers from another
 * Created by Havard on 12.11.2015.
 */
public class SubInstruction extends ArithmeticInstruction {
    public static final String MNEMONIC = "sub";
    @Override
    public int getResult(int Rn, int FSO) {
        return Rn-FSO;
    }
}
