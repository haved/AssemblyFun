package me.havard.assemblyfun;

/** An exception thrown when something went wrong in the Assembly parsing/running
 * Created by Havard on 06.11.2015.
 */
public class AssemblyException extends RuntimeException {
    private int mExceptionId;
    private String[] mParams;
    private int mLineNumber = -1;

    public AssemblyException(String reason, int exceptionId, String... params) {
        super(reason);
        mExceptionId = exceptionId;
        mParams = params;
    }

    public void setLineNumber(int lineNumber) {
        mLineNumber = lineNumber;
    }

    public int getLineNumber() {
        return mLineNumber;
    }

    public int getExceptionID(){
        return mExceptionId;
    }

    public String[] getParams() {
        return mParams;
    }

    public String toString() {
        StringBuilder b = new StringBuilder().append(getMessage()).append(" ::: ").append(mExceptionId).append(" ::: ").append(mLineNumber).append(" :mParams: ");

        for(String s:mParams)
            b.append(s).append(" ::: ");

        return b.toString();
    }

    public static final int UNKNOWN_EXCEPTION = 10; //Exception message
    public static final int INSTRUCTION_ADDRESS_NOT_ALIGNED = 20; //Address tried
    public static final int UNKNOWN_REGISTER_NAME = 30; //Register name used
    public static final int INSTRUCTION_INSTANTIATION_FAILED = 40; //Mnemonic one tried to instantiate
    public static final int FAILED_TO_PARSE_IMMEDIATE_VALUE = 50; //string given as imm
    public static final int IMMEDIATE_VALUE_WITHOUT_HASH = 60; //string given as imm
    public static final int IMMEDIATE_VALUE_NOT_IN_BOUNDS = 70; //value given, min value, max value
    public static final int FSO_IMMEDIATE_VALUE_NOT_FROM_SHIFT = 80; //value given
    public static final int MNEMONIC_NOT_PARSED = 90; //line given
    public static final int LACK_OF_ARGUMENTS = 100; //line given, arguments wanted
    public static final int JUNK_ON_END_OF_LABEL_DEFINE_LINE = 110; //line given
    public static final int EMPTY_LABEL = 120; //line given
    public static final int LABEL_NOT_RECOGNIZED = 130; //line given, label given

    public static final int TEST_PARSE_ERROR = 1000; //Exception message

    public static final int ROM_BYTE_OUT_OF_BOUNDS = 2000; //Address used
    public static final int ROM_BYTE_NOT_A_WORD_INSTRUCTION = 2001; //Address used
    public static final int NOT_RUNNABLE_INSTRUCTION_RUN = 2002; //Instruction class simplified name
    public static final int DEBUG_ERROR_MESSAGE = 2003; //Instruction class simplified name

    public static final int TEST_FAILED_PUBLIC = 3000; //Expected output, Given output
    public static final int TEST_FAILED = 3010;
    public static final int TOO_MANY_INSTRUCTIONS = 3020; //Instructions run
}
