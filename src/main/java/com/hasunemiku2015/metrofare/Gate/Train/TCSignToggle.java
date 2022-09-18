package com.hasunemiku2015.metrofare.Gate.Train;

import com.bergerkiller.bukkit.tc.signactions.SignAction;

public class TCSignToggle {
    private static final ClearanceTC ctc = new ClearanceTC();

    public static void init() {
        SignAction.register(ctc);
    }
    public static void deInit() {
        SignAction.unregister(ctc);
    }
}
