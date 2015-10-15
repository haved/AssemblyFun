package me.havard.assemblyfun.assembly;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

/** A simple implementation of an AssemblyROMProvider
 * Created by Havard on 15.10.2015.
 */
public class SimpleAssemblyROMProvider implements  AssemblyROMProvider {

    private List<String> mInstructions;

    public SimpleAssemblyROMProvider(String text) {
        parseText(text);
    }

    private void parseText(String text) {
        HashMap<String, Integer> labels = new HashMap<>();

        String[] lines = text.split("\n");
        for (String line : lines) {
            Log.i("Assembly Fun", line);
        }
    }

    @Override
    public String getInstruction(int address) {
        return mInstructions.get(address);
    }

    @Override
    public int getROMSize() {
        return mInstructions.size();
    }
}
