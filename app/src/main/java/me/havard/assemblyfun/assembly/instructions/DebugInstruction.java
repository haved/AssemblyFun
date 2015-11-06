package me.havard.assemblyfun.assembly.instructions;

import android.util.Log;

import me.havard.assemblyfun.assembly.AssemblyRunner;

/** An instruction that logs a message
 * Created by Havard on 06.11.2015.
 */
public class DebugInstruction extends Instruction {
    public static final String MNEMONIC = ".debug ";

    private String mText;

    public DebugInstruction() {}

    public DebugInstruction(String text) { mText = text; }

    @Override
    public void loadFromString(String s) {
        if(s.length()<=MNEMONIC.length())
            mText = "Debug message";
        else
            mText = s.substring(MNEMONIC.length());
    }

    @Override
    public void run(AssemblyRunner runner) {
        Log.d("Assembly Fun", "DebugInstruction was run: " + mText);
    }
}
