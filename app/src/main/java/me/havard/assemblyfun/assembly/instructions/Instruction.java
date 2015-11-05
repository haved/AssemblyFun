package me.havard.assemblyfun.assembly.instructions;

import me.havard.assemblyfun.assembly.AssemblyRunner;

/** An abstract superclass for Instructions
 * Created by Havard on 05.11.2015.
 */
public abstract class Instruction {
    public abstract void run(AssemblyRunner runner);
    public boolean runnable() {
        return true;
    }
}
