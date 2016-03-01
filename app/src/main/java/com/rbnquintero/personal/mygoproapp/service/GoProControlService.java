package com.rbnquintero.personal.mygoproapp.service;

import android.os.AsyncTask;
import android.util.Log;

import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.utils.RESTUtil;

/**
 * Created by rbnquintero on 2/29/16.
 */
public class GoProControlService  extends AsyncTask<String, Void, Void> {
    private static final String TAG = "GoProControlService";

    public static final String TURNOFF_CAMERA = "turn_off_camera";

    private static final String ROOT_URL = "http://10.5.5.9";
    private static final String turnOffUrl = "/gp/gpControl/command/system/sleep";

    private static void turnOffCamera() {
        try {
            RESTUtil.httpConnection(ROOT_URL + turnOffUrl, RESTUtil.JSON, RESTUtil.HTTPMethod.GET);
        } catch (Exception e) {
            Log.e(TAG, "Could not turn off camera", e);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String method = params[0];
        if(TURNOFF_CAMERA.equals(method)) {
            turnOffCamera();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "Camera turned off");
    }
}
