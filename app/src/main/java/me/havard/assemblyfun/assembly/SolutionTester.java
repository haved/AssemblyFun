package me.havard.assemblyfun.assembly;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.havard.assemblyfun.AssemblyException;
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

    public TestResult runAllTests(String testsTexts, String solution) {
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
                throw new AssemblyException("SolutionTester.runAllTests() parse error", AssemblyException.TEST_PARSE_ERROR, e.getMessage());
            }
        }

        int resultCount = 0;
        TestResult overallResult = new TestResult(true, 0, 0, 0);

        for(Test test:tests) {
            TestResult result = runTest(test, provider);
            if(test.getType()==TestType.PRIVATE)
                continue;
            overallResult.speed+=result.speed;
            overallResult.size+=result.size;
            overallResult.memUsage+=result.memUsage;
            resultCount++;
        }

        overallResult.speed/=resultCount;
        overallResult.size/=resultCount;
        overallResult.memUsage/=resultCount;

        return overallResult;
    }

    private TestResult runTest(Test test, AssemblyROMProvider provider) {
        TestResult result = new TestResult(false, 0, 0, 0);
        try {
            mRunner.setRAM(provider, 4000); //TODO: Maybe not hard code that in?
            for(int i = 0; i < test.getInputs().length; i++)
                mRunner.setRegister(i, test.getInputs()[i]);
            while (true) {
                if (!mRunner.runCurrentInstruction())
                    break; //There was not runnable instruction at pc, we are done!
                if(mRunner.getTotalInstructionCounter()>4000) {
                    throw new AssemblyException("SolutionTester.runTest() ran too many instructions!", AssemblyException.TOO_MANY_INSTRUCTIONS, Integer.toString(mRunner.getTotalInstructionCounter()));
                }
            }
            boolean success = true;
            for(int i = 0; i < test.getExpectedOutputs().length; i++)
                if(mRunner.getRegister(i) != test.getExpectedOutputs()[i]) {
                    success = false;
                    break;
                }
            if(success) {
                result.success = true;
                result.speed = mRunner.getTotalInstructionCounter();
                result.size = provider.getROMWordCount();
                result.memUsage = mRunner.getMemoryCounter();
            } else {
                if(test.getType() == TestType.PUBLIC) {
                    StringBuilder expectedOutput = new StringBuilder();
                    StringBuilder givenOutput = new StringBuilder();
                    for(int i = 0; i < test.getExpectedOutputs().length; i++) {
                        if(i!=0) {
                            expectedOutput.append(", ");
                            givenOutput.append(", ");
                        }
                        expectedOutput.append(test.getExpectedOutputs()[i]);
                        givenOutput.append(mRunner.getRegister(i));
                    }
                    throw new AssemblyException("SolutionTest.runTest() test failed!", AssemblyException.TEST_FAILED_PUBLIC, expectedOutput.toString(), givenOutput.toString());
                } else {
                    throw new AssemblyException("SolutionTest.runTest() test failed!", AssemblyException.TEST_FAILED);
                }
            }
        } catch (Exception e) {
            if(e instanceof AssemblyException)
                throw e;
            else
                throw new AssemblyException("SolutionTester.runTest() Unknown exception.", AssemblyException.UNKNOWN_EXCEPTION, e.getMessage());
        }
        return result;
    }

    public static class Test {
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

    public enum TestType {
        PUBLIC(PUBLIC_TEST_SUFFIX), HIDDEN(HIDDEN_TEST_SUFFIX), PRIVATE(PRIVATE_TEST_SUFFIX);

        private char mDef;

        TestType(char def) {
            mDef = def;
        }

        public char getDef() {
            return mDef;
        }
    }

    public static class TestResult {
        public float speed;
        public int size;
        public float memUsage;
        public boolean success;

        public TestResult(boolean success, float speed, int size, float memUsage) {
            this.success = success;
            this.speed = speed;
            this.size = size;
            this.memUsage = memUsage;
        }
    }
}
