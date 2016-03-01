package com.rbnquintero.personal.mygoproapp.service;

import android.os.AsyncTask;
import android.util.Log;

import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProStatusServiceDelegate;
import com.rbnquintero.personal.mygoproapp.utils.RESTUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by rbnquintero on 2/25/16.
 */
public class GoProStatusService extends AsyncTask<String, Void, GoProStatus> {
    private String TAG = this.getClass().getSimpleName();
    private GoProStatusServiceDelegate delegate;
    private static long timeout = 20000;

    public GoProStatusService(GoProStatusServiceDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected GoProStatus doInBackground(String... params) {
        if (params.length >0 && params[0] != null) {
            try {
                timeout = Long.parseLong(params[0]);
            } catch (Exception e) {
                Log.e(TAG, "Could not set custom timeout for " + TAG + " : " + params[0]);
            }
        }
        GoProStatus status = null;
        long initialTime = System.currentTimeMillis();
        while (status == null && (System.currentTimeMillis() - initialTime) < timeout) {
            status = getCameraStatus();
            if (status == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Could not sleep thread", e);
                }
            }
        }
        return status;
    }

    @Override
    protected void onPostExecute(GoProStatus result) {
        if (result != null) {
            delegate.updateStatus(result);
        } else {
            delegate.updateStatusTimeout();
        }
    }

    /*public static void main(String[] args) {
        System.out.println(getCameraStatus().Mode());
        System.out.println(getCameraStatus().SubMode());
    }*/

    private GoProStatus getCameraStatus() {
        try {
            String respuesta = RESTUtil.httpConnection("http://10.5.5.9/gp/gpControl/status/", RESTUtil.JSON, RESTUtil.HTTPMethod.GET);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(respuesta);
            GoProStatus status = new GoProStatus((JSONObject) obj);
            return status;
        } catch (Exception e) {
            Log.e(TAG, "Could not obtain GoPro status");
        }
        return null;
    }

}
