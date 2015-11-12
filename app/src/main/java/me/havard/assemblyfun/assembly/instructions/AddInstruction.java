package me.havard.assemblyfun.assembly.instructions;

/** An instruction for adding to numbers together
 * Created by Havard on 12.11.2015.
 */
public class AddInstruction extends ArithmeticInstruction {
    public static final String MNEMONIC = "add ";

    @Override
    public int getResult(int Rn, int FSO) {
        return Rn+FSO;
    }
}
