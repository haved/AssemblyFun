package me.havard.assemblyfun.assembly;

/** An interface for providing instructions and rom data to the AssemblyRunner
 * Created by Havard on 15.10.2015.
 */
public interface AssemblyROMProvider {
    String getInstruction(int address);
    int getROMSize();
}
