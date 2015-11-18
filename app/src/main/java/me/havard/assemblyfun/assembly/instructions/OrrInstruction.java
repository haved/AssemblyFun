package me.havard.assemblyfun.assembly.instructions;

/** An instruction for bitwise or between two numbers
 * Created by Havard on 12.11.2015.
 */
public class OrrInstruction extends ArithmeticInstruction {
    public static final String MNEMONIC = "orr";

    @Override
    public int getResult(int Rn, int FSO) {
        return Rn|FSO;
    }
}
