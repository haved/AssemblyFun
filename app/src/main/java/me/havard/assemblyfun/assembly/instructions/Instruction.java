package me.havard.assemblyfun.assembly.instructions;

import java.util.HashMap;

import me.havard.assemblyfun.assembly.AssemblyRunner;

/** An abstract superclass for Instructions
 * Created by Havard on 05.11.2015.
 */
public abstract class Instruction {

    protected ConditionCodes mCondCode;

    public abstract void loadFromString(String s, HashMap<String, Integer> registerNames);
    public abstract void run(AssemblyRunner runner);
    public boolean runnable() {
        return true;
    }
    public Instruction setConditionCode(ConditionCodes code) {
        mCondCode = code;
        return this;
    }
    public ConditionCodes getConditionCode() {
        return mCondCode;
    }

    public enum ConditionCodes {
        ALWAYS(""),
        EQUAL("eq"),
        NOT_EQUAL("ne"),
        SIGNED_GREATER_THAN("gt"),
        SIGNED_LESS_THAN("lt"),
        SIGNED_GREATER_EQUAL("ge"),
        SIGNED_LESS_EQUAL("le"),
        UNSIGNED_HIGHER("hi"),
        UNSIGNED_LOWER_SAME("ls");

        private String mCode;
        ConditionCodes(String name) {
            mCode = name;
        }

        public String getCode() {
            return mCode;
        }

        public boolean conditionCodeMet(boolean zero, boolean negative, boolean signed) {
            return ConditionCodes.conditionMet(this, zero, negative, signed);
        }

        public static boolean conditionMet(ConditionCodes code, boolean zero, boolean negative, boolean signed) {
            switch(code) {
                case ALWAYS: return true;
                case EQUAL: return zero;
                case NOT_EQUAL: return !zero;
                case SIGNED_GREATER_THAN: return !negative^signed & !zero;
                case SIGNED_LESS_THAN: return negative^signed;
                case SIGNED_GREATER_EQUAL: return !negative^signed;
                case SIGNED_LESS_EQUAL: return negative^signed|zero;
                case UNSIGNED_HIGHER: return !(negative|zero);
                case UNSIGNED_LOWER_SAME: return negative|zero;
                default: return true;
            }
        }
    }
}
