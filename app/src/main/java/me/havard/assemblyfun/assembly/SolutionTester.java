package me.havard.assemblyfun.assembly;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.havard.assemblyfun.util.HUtil;

import static me.havard.assemblyfun.assembly.TaskTestsTextSpecification.*;

/** A tester!
 * Created by Havard on 15.10.2015.
 */
public class SolutionTester {
    private AssemblyRunner mRunner;

    public SolutionTester() {
        mRunner = new AssemblyRunner();
    }

    public void runAllTests(String testsTexts, String solution) {
        SimpleAssemblyROMProvider provider = new SimpleAssemblyROMProvider(solution);
        List<Test> tests = new ArrayList<>();

        int start;
        for(start = 0; start < testsTexts.length();){
            try {
                int nextSymbol = testsTexts.length();
                TestType type = null;
                for (TestType tt : TestType.values()) {
                    int indexOfTestType = testsTexts.indexOf(tt.getDef(), start);
                    if (indexOfTestType >= 0 & indexOfTestType < nextSymbol) {
                        nextSymbol = indexOfTestType;
                        type = tt;
                    }
                }

                if(type != null) {
                    int oldStart = start;
                    start = nextSymbol + 1;
                    tests.add(new Test(type, testsTexts.substring(oldStart, nextSymbol))); //Keep in mind. This substring does not include the nextSymbol, but stops before it.
                }
                else {
                    Log.w("Assembly Fun", "The testsText parser had to stop parsing before the end of the line. Didn't recognise any parsable symbols.");
                    break;
                }
            }
            catch (Exception e) {
                Log.e("Assembly Fun", "Failed to parse a test from the testsText in the SolutionTester");
                Log.wtf("Assembly Fun", e);
            }
        }

        for(Test test:tests) {
            StringBuilder builder = new StringBuilder("Found a test with type: ").append(test.getType()).append(" text: ").append(test.getTestText()).append(" inputs: [");
            for(int input:test.getInputs())
                builder.append(input).append(", ");
            builder.append("] outputs: [");
            for(int output:test.getExpectedOutputs())
                builder.append(output).append(", ");
            builder.append("]");
            Log.d("Assembly Fun", builder.toString());
        }

        for(Test test:tests)
            runTest(test, provider);
    }

    private void runTest(Test test, AssemblyROMProvider provider) {
        mRunner.setRAM(provider, 4000); //TODO: Maybe not hard code that in?
        while(true)
            if(!mRunner.runCurrentInstruction())
                break; //There was not runnable instruction at pc, we are done!
        Log.d("Assembly Fun", "Finished running a test! The speed was " + mRunner.getInstructionCounter());
    }
}

class Test {
    private TestType mType;
    private String mTestText;
    private int[] mInputs;
    private int[] mExpectedOutputs;
    public Test(TestType type, String testText) {
        mType = type;
        mTestText = testText;

        String[] io = testText.split(INPUT_OUTPUT_SEPARATOR);
        if(io.length != 2)
            throw new IllegalArgumentException("The taskText supplied doesn't have exactly one input/output separator! " + testText);

        mInputs = HUtil.parseAllIntegers(io[0].split(NUMBER_SEPARATOR));
        mExpectedOutputs = HUtil.parseAllIntegers(io[1].split(NUMBER_SEPARATOR));
    }

    public TestType getType() {
        return mType;
    }

    public String getTestText() {
        return mTestText;
    }

    public int[] getInputs() {
        return mInputs;
    }

    public int[] getExpectedOutputs() {
        return mExpectedOutputs;
    }
}

enum TestType {
    PUBLIC(PUBLIC_TEST_SUFFIX), HIDDEN(HIDDEN_TEST_SUFFIX), PRIVATE(PRIVATE_TEST_SUFFIX);

    private char mDef;

    TestType(char def) {
        mDef = def;
    }

    public char getDef() {
        return mDef;
    }
}
