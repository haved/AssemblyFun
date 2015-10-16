package me.havard.assemblyfun.assembly;

/**The format of the test is as follows:
 * 1,2,3>6,6!2,3,4,1>9,3;5,2>4;5>2;8>4:3>3,6:
 *
 * Each test contains a '>'
 * The 32-bit integers on the left side of the '>' are the inputs, starting at r0, separated by commas.
 * The 32-bit integers on the right side of the '>' are the expected outputs, starting at r0, separated by commas.
 * Each ends with one of three characters: ! ; :
 * An exclamations mark means the test is public. If it fails, the user will be told of the given and expected output, but not of the inputs
 * A semi-colon is a normal hidden task
 * A colon means that the tests are server side only, and never downloaded. These are not a part of the scoring calculation.
 *
 * Created by Havard on 16.10.2015.
 */
public final class TaskTestsTextSpecification {
    public static final String INPUT_OUTPUT_SEPARATOR = ">";
    public static final String NUMBER_SEPARATOR = ",";
    public static final char PUBLIC_TEST_SUFFIX = '!';
    public static final char HIDDEN_TEST_SUFFIX = ';';
    public static final char PRIVATE_TEST_SUFFIX = ':';
}
