package com.rbnquintero.personal.mygoproapp.business;

import android.os.AsyncTask;
import android.util.Log;

import com.rbnquintero.personal.mygoproapp.business.delegate.GoProControlBusinessDelegate;
import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.service.GoProControlServiceNew;
import com.rbnquintero.personal.mygoproapp.service.GoProStatusServiceNew;
import com.rbnquintero.personal.mygoproapp.service.GoProWakeUpServiceNew;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rbnquintero on 2/29/16.
 */
public class GoProControlBusiness extends AsyncTask<String, Void, Map<String, Object>> {
    private String TAG = this.getClass().getSimpleName();
    GoProControlBusinessDelegate delegate;

    public static final String SUCCESS = "success";
    public static final String METHOD = "method";

    public static final String SHUTTER_ACTION = "shutter_action";
    public static final String INITIALIZE_CAMERA = "initialize_camera";
    public static final String TURN_OFF_CAMERA = "turn_off_camera";

    GoProStatusServiceNew statusServiceNew = new GoProStatusServiceNew();
    GoProControlServiceNew controlServiceNew = new GoProControlServiceNew();
    GoProWakeUpServiceNew wakeUpServiceNew = new GoProWakeUpServiceNew();

    public GoProControlBusiness(GoProControlBusinessDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Map<String, Object> doInBackground(String... params) {
        String method = params[0];
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS, false);
        response.put(METHOD, method);
        Log.d(TAG, method);
        if (SHUTTER_ACTION.equals(method)) {
            shutterAction();
            response.put(SUCCESS, true);
        } else if (INITIALIZE_CAMERA.equals(method)) {
            response = initializeCamera(params[1]);
        } else if (TURN_OFF_CAMERA.equals(method)) {
            response.put(SUCCESS, turnOffCamera());
        }
        return response;
    }

    @Override
    protected void onPostExecute(Map<String, Object> response) {
        String method = (String) response.get(METHOD);
        if (INITIALIZE_CAMERA.equals(method)) {
            delegate.onFinishingInitialization((Boolean) response.get(SUCCESS));
        }
    }

    private Map<String, Object> initializeCamera(String macAddress) {
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS, false);
        response.put(METHOD, INITIALIZE_CAMERA);
        try {
            statusServiceNew.getStatus();
            response.put(SUCCESS, true);
            Log.i(TAG, "Camera on");
        } catch (IOException e) {
            Log.d(TAG, "Camera unreachable");
            boolean result = wakeUpServiceNew.wakeUpCamera(macAddress);
            response.put(SUCCESS, result);
        } catch (ParseException e) {
            Log.e(TAG, "Could not parse the response", e);
        }
        return response;
    }

    private void shutterAction() {
        try {
            GoProStatus status = statusServiceNew.getStatus();
            long busy = (long) status.getStatusItem(GoProStatus.SystemBusy);
            if (busy > 0) {
                Log.d(TAG, "Shutter off");
                controlServiceNew.shutterOffCamera();
            } else {
                Log.d(TAG, "Shutter on");
                controlServiceNew.shutterOnCamera();
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not obtain GoPro status");
            Log.d(TAG, "Could not obtain GoPro status", e);
        }
    }

    private boolean turnOffCamera() {
        try {
            controlServiceNew.turnOffCamera();
            Log.i(TAG, "Camera off");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Could not turn off camera");
            Log.d(TAG, "Could not turn off camera", e);
        }
        return false;
    }
}
