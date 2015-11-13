package me.havard.assemblyfun.assembly.instructions;

/** An instruction for bitwise clear between two numbers
 * Created by Havard on 12.11.2015.
 */
public class BicInstruction extends ArithmeticInstruction {
    public static final String MNEMONIC = "bic ";

    @Override
    public int getResult(int Rn, int FSO) {
        return Rn&~FSO;
    }
}
