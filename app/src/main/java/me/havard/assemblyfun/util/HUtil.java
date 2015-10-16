package me.havard.assemblyfun.util;

/** A simple util class for putting stuff
 * Created by Havard on 17.10.2015.
 */
public class HUtil {
    public static int[] parseAllIntegers(String[] integers) {
        int[] output = new int[integers.length];

        for(int i = 0; i < output.length; i++) {
            output[i] = Integer.parseInt(integers[i]);
        }

        return output;
    }
}
