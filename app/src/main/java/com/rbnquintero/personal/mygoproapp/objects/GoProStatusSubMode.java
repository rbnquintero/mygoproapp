package com.rbnquintero.personal.mygoproapp.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rbnquintero on 2/29/16.
 */
public class GoProStatusSubMode {
    public static String statusSubMode = "44";
    public String submode;
    public String submodeCode;
    public GoProStatusMode mode;

    public GoProStatusSubMode(String submode, String submodeCode, GoProStatusMode mode) {
        this.submode = submode;
        this.submodeCode = submodeCode;
        this.mode = mode;
    }
}
