package me.havard.assemblyfun.assembly;

import me.havard.assemblyfun.assembly.instructions.Instruction;

/** An interface for providing instructions and rom data to the AssemblyRunner
 * Created by Havard on 15.10.2015.
 */
public abstract class AssemblyROMProvider {
    public abstract Instruction getInstruction(int address);
    public abstract byte getROMByte(int address);
    public abstract int getROMWordCount();
    public abstract int getROMSizeInBytes();
}
