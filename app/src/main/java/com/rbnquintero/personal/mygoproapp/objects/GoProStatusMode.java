package com.rbnquintero.personal.mygoproapp.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rbnquintero on 2/29/16.
 */
public class GoProStatusMode {
    public static String statusMode = "43";
    public String mode;
    public String modeCode;

    public List<GoProStatusSubMode> subModes;

    public GoProStatusMode(String mode, String modeCode) {
        this.mode = mode;
        this.modeCode = modeCode;
        this.subModes = new ArrayList<GoProStatusSubMode>();
    }

    public static List<GoProStatusMode> buildObject() {
        List<GoProStatusMode> modes = new ArrayList<GoProStatusMode>();

        //Video Modes
        GoProStatusMode video = new GoProStatusMode("Video", "0");
        video.subModes.add(new GoProStatusSubMode("Video", "0", video));
        video.subModes.add(new GoProStatusSubMode("VideoPhoto", "1", video));
        video.subModes.add(new GoProStatusSubMode("Looping", "2", video));
        modes.add(video);

        //Photo Modes
        GoProStatusMode photo = new GoProStatusMode("Photo", "1");
        photo.subModes.add(new GoProStatusSubMode("Single", "0", photo));
        photo.subModes.add(new GoProStatusSubMode("Continuous", "1", photo));
        photo.subModes.add(new GoProStatusSubMode("Night", "2", photo));
        modes.add(photo);

        //Burst Modes
        GoProStatusMode burst = new GoProStatusMode("Burst", "2");
        burst.subModes.add(new GoProStatusSubMode("Burst", "0", burst));
        burst.subModes.add(new GoProStatusSubMode("TimeLapse", "1", burst));
        burst.subModes.add(new GoProStatusSubMode("NightLapse", "2", burst));
        modes.add(burst);

        modes.add(new GoProStatusMode("Broadcast", "3"));
        modes.add(new GoProStatusMode("Playback", "4"));
        modes.add(new GoProStatusMode("Settings", "5"));

        return modes;
    }
}
