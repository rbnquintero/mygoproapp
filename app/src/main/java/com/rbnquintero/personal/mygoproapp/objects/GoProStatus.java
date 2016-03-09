package com.rbnquintero.personal.mygoproapp.objects;

import android.util.Log;

import com.rbnquintero.personal.mygoproapp.utils.RESTUtil;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by rbnquintero on 2/26/16.
 */
public class GoProStatus {
    private String TAG = this.getClass().getSimpleName();

    private JSONObject response;
    private static final String STATUS = "status";

    public GoProStatus(JSONObject response) {
        this.response = response;
    }

    public Object getStatusItem(Object item) {
        JSONObject status = (JSONObject) response.get(STATUS);
        return status.get(item);
    }

    public static final String InternalBatteryPresent = "1";
    public static final String InternalBatteryLevel = "2";
    public static final String ExternalBatteryPresent = "3";
    public static final String ExternalBatteryLevel = "4";
    public static final String CurrentTemperature = "5";
    public static final String SystemHot = "6";
    public static final String SystemBusy = "8";
    public static final String QuickCaptureActive = "9";
    public static final String EncodingActive = "10";
    public static final String LcdLockActive = "11";
    public static final String CameraLocateActive = "45";

    private static final List<GoProStatusMode> modes = GoProStatusMode.buildObject();

    public String Mode() {
        JSONObject status = (JSONObject) response.get(STATUS);
        String modeCode = status.get(GoProStatusSubMode.statusSubMode).toString();
        for(GoProStatusMode mode : modes) {
            if(modeCode.equals(mode.modeCode)) {
                return mode.mode;
            }
        }
        return null;
    }

    public String SubMode() {
        JSONObject status = (JSONObject) response.get(STATUS);
        String modeCode = status.get(GoProStatusSubMode.statusSubMode).toString();
        String subModeCode = status.get(GoProStatusSubMode.statusSubMode).toString();
        for(GoProStatusMode mode : modes) {
            if(modeCode.equals(mode.modeCode)) {
                for(GoProStatusSubMode subMode : mode.subModes) {
                    if(subModeCode.equals(subMode.submodeCode)) {
                        return subMode.submode;
                    }
                }
            }
        }
        return null;
    }

    public String Battery() {
        return String.valueOf((long) this.getStatusItem(GoProStatus.InternalBatteryLevel + 1) * 100 / 4);
    }

    public static final String Xmode = "12";
    public static final String VideoProgressCounter = "13";
    public static final String VideoProtuneDefault = "46";
    public static final String PhotoProtuneDefault = "47";
    public static final String MultiShotProtuneDefault = "48";
    public static final String MultiShotCountDown = "49";
    public static final String BroadcastProgressCounter = "14";
    public static final String BroadcastViewersCount = "15";
    public static final String BroadcastBstatus = "16";
    public static final String WirelessEnable = "17";
    public static final String WirelessPairState = "19";
    public static final String WirelessPairType = "20";
    public static final String WirelessScanState = "22";
    public static final String WirelessScanTime = "23";
    public static final String WirelessScanCurrentTime = "18";
    public static final String WirelessPairing = "28";
    public static final String WirelessRemoveControlVersion = "26";
    public static final String WirelessRemoveControlConnected = "27";
    public static final String WirelessAppCount = "31";
    public static final String WirelessProvisionStatus = "24";
    public static final String WirelessRssiBars = "25";
    public static final String WirelessWlanSsid = "29";
    public static final String WirelessApSsid = "30";
    public static final String SdStatus = "33";
    public static final String RemainingPhotos = "34";
    public static final String RemainingVideoTime = "35";
    public static final String NumGroupPhotos = "36";
    public static final String NumGroupVideos = "37";
    public static final String NumTotalPhotos = "38";
    public static final String NumTotalVideos = "39";
    public static final String DateTime = "40";
    public static final String FWUpdateOtaStatus = "41";
    public static final String FWUpdateDownloadCancelRequestPending = "42";
}
