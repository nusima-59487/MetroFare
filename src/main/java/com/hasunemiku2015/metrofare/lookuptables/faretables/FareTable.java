package com.hasunemiku2015.metrofare.lookuptables.faretables;

import lombok.Getter;

import java.util.List;

@Getter
public class FareTable {
    //Vars
    private final List<String> keys;
    private final int[][] csvOut;

    //Constructors
    protected FareTable(List<String> keys, int[][] csvOut) {
        this.keys = keys;
        this.csvOut = csvOut;
    }

    //Methods
    public int getFare1000(String from, String to) {
        if (!from.equalsIgnoreCase("") && !to.equalsIgnoreCase("")) {
            if (keys.contains(from) && keys.contains(to)) {
                int lut0 = keys.indexOf(from);
                int lut1 = keys.indexOf(to);

                if (csvOut.length > lut0 && csvOut[lut0].length > lut1) {
                    if (csvOut[lut0][lut1] >= 0) {
                        return csvOut[lut0][lut1];
                    }
                }
            }
        }
        return -1;
    }
}
