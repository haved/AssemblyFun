package me.havard.assemblyfun.assembly;

import java.util.ArrayList;
import java.util.List;

/** A class that can run assembly!
 * Created by Havard on 15.10.2015.
 */
public class AssemblyRunner {

    private static final int MAX_USER_REGISTER = 12;
    private static final int STACK_POINTER = 13;
    private static final int LINK_REGISTER = 14;
    private static final int PROGRAM_COUNTER = 15;
    private static final int TOTAL_REGISTERS = 16;

    private int mInstructionCounter;
    private int mMemoryCounter;

    Register[] mRegisters;
    public AssemblyRunner() {
        mRegisters = new Register[TOTAL_REGISTERS];
        for(int i = 0; i <= MAX_USER_REGISTER; i++) {
            mRegisters[i]=new Register("r"+i);
        }
        mRegisters[STACK_POINTER] = new Register("sp");
        mRegisters[LINK_REGISTER] = new Register("lr");
        mRegisters[PROGRAM_COUNTER] = new Register("pc");
    }

    public void runCurrentInstruction() {

    }

    public Register programCounter() {
        return mRegisters[PROGRAM_COUNTER];
    }
}

class Register {
    public String name;
    public int value;

    public Register(String name) {
        this.name = name;
    }
}
