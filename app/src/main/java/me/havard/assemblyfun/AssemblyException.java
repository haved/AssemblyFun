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

    public static final int TEST_PARSE_ERROR = 1;
    public static final int UNKNOWN_EXCEPTION = 2;
    public static final int INSTRUCTION_ADDRESS_NOT_ALIGNED = 3;
    public static final int UNKNOWN_REGISTER_NAME = 4;
    public static final int INSTRUCTION_INSTANTIATION_FAILED = 5;
    public static final int ROM_BYTE_OUT_OF_BOUNDS = 6;
    public static final int ROM_BYTE_NOT_A_WORD_INSTRUCTION = 7;
    public static final int NOT_RUNNABLE_INSTRUCTION_RUN = 8;
    public static final int TEST_FAILED_PUBLIC = 9; //Expected output, Given output
    public static final int TEST_FAILED = 10;
}
