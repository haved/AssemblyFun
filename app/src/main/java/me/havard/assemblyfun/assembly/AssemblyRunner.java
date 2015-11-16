package me.havard.assemblyfun.assembly;

import android.util.Log;

import me.havard.assemblyfun.assembly.instructions.Instruction;

/** A class that can run assembly!
 * Created by Havard on 15.10.2015.
 */
public class AssemblyRunner {

    public static final int STACK_POINTER = 13;
    public static final int LINK_REGISTER = 14;
    public static final int PROGRAM_COUNTER = 15;
    public static final int TOTAL_REGISTERS = 16;

    public static final int FLAG_ZERO = 0b1;
    public static final int FLAG_NEGATIVE = 0b10;
    public static final int FLAG_SIGNED = 0b100;

    private int mInstructionCounter;
    private int mMemoryCounter;

    private byte[] mRam;
    private AssemblyROMProvider mRom;
    private int mRomPosition;
    private int[] mRegisters;
    private int mFlags;
    public AssemblyRunner() {

    }

    public void setRAM(AssemblyROMProvider rom, int romPosition) {
        mRom = rom;
        mRomPosition = romPosition;
        mRam = new byte[romPosition+rom.getROMSizeInBytes()];

        mRegisters = new int[TOTAL_REGISTERS];
        setRegister(STACK_POINTER, romPosition);
        setRegister(PROGRAM_COUNTER, romPosition);
        setRegister(LINK_REGISTER, mRam.length);
        mFlags = FLAG_ZERO;
        mInstructionCounter = 0;
        mMemoryCounter = 0;
    }

    /** A method that runs the instruction at pc.
     *
     * @return True if the instruction ran. False if there was no runnable instruction at pc.
     */
    public boolean runCurrentInstruction() {
        int currentInstruction = getRegister(PROGRAM_COUNTER);
        setRegister(PROGRAM_COUNTER, getRegister(PROGRAM_COUNTER) + 4);

        Instruction instruction = mRom.getInstruction(currentInstruction-mRomPosition);
        if(instruction==null || !instruction.runnable())
            return false; //We are done!
        mInstructionCounter++;
        if(instruction.getConditionCode().conditionCodeMet((mFlags&FLAG_ZERO)!=0, (mFlags&FLAG_NEGATIVE)!=0, (mFlags&FLAG_SIGNED)!=0))
            instruction.run(this);
        return true;
    }

    public int getRegister(int id) {
        return mRegisters[id];
    }

    public void setRegister(int id, int value) {
        mRegisters[id]=value;
    }

    public void setFlags(boolean zero, boolean negative, boolean signed) {
        Log.d("Assembly Fun", "Flags were set! Z="+zero+ " N="+negative + " S="+signed);
        mFlags = (zero?FLAG_ZERO:0) + (negative?FLAG_NEGATIVE:0) + (signed?FLAG_SIGNED:0);
    }

    public int getFlags() {
        return mFlags;
    }

    public int getInstructionCounter() {
        return mInstructionCounter;
    }
    public int getMemoryCounter() {
        return mMemoryCounter;
    }

}