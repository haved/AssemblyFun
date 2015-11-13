package me.havard.assemblyfun.assembly.instructions;

/** An instruction for bitwise and between two numbers
 * Created by Havard on 12.11.2015.
 */
public class AndInstruction extends ArithmeticInstruction {
    public static final String MNEMONIC = "and ";

    @Override
    public int getResult(int Rn, int FSO) {
        return Rn&FSO;
    }
}
