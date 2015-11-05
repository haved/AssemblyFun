package me.havard.assemblyfun.assembly;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import me.havard.assemblyfun.assembly.instructions.Instruction;

/** A simple implementation of an AssemblyROMProvider
 * Created by Havard on 15.10.2015.
 */
public class SimpleAssemblyROMProvider extends AssemblyROMProvider {

    List<Instruction> instructions;

    public SimpleAssemblyROMProvider(String text) {
        parseText(text);
    }

    public void parseText(String text) {

    }

    @Override
    public Instruction getInstruction(int address) {
        return null;
    }

    @Override
    public byte getROMByte(int address) {
        return 0;
    }

    @Override
    public int getROMSize() {
        return 0;
    }

    @Override
    public int getROMSizeInBytes() {
        return getROMSize()*4;
    }
}
