package me.havard.assemblyfun.assembly.instructions;

/** An instruction for bitwise exclusive or between two numbers
 * Created by Havard on 12.11.2015.
 */
public class EorInstruction extends ArithmeticInstruction {
    public static final String MNEMONIC = "eor ";

    @Override
    public int getResult(int Rn, int FSO) {
        return Rn^FSO;
    }
}
