package com.rbnquintero.personal.mygoproapp.business;

import android.os.AsyncTask;
import android.util.Log;

import com.rbnquintero.personal.mygoproapp.business.delegate.GoProStatusBusinessDelegate;
import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.service.GoProControlServiceNew;
import com.rbnquintero.personal.mygoproapp.service.GoProStatusServiceNew;
import com.rbnquintero.personal.mygoproapp.service.GoProWakeUpServiceNew;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rbnquintero on 2/29/16.
 */
public class GoProStatusBusiness extends AsyncTask<String, Void, Map<String, Object>> {
    private String TAG = this.getClass().getSimpleName();
    private GoProStatusBusinessDelegate delegate;

    public static final String SUCCESS = "success";
    public static final String METHOD = "method";

    // Methods
    public static final String GET_STATUS = "get_status";
    public static final String GET_MEDIA_LIST = "get_media_list";

    // Returning objects
    public static final String STATUS = "status";

    GoProStatusServiceNew statusServiceNew = new GoProStatusServiceNew();
    GoProControlServiceNew controlServiceNew = new GoProControlServiceNew();
    GoProWakeUpServiceNew wakeUpServiceNew = new GoProWakeUpServiceNew();

    public GoProStatusBusiness(GoProStatusBusinessDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Map<String, Object> doInBackground(String... params) {
        String method = params[0];
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS, false);
        response.put(METHOD, method);
        Log.d(TAG, method);
        if (GET_STATUS.equals(method)) {
            response = getGoProStatus();
        } else if (GET_MEDIA_LIST.equals(method)) {
            getMediaList();
            response.put(SUCCESS, true);
        }
        return response;
    }

    @Override
    protected void onPostExecute(Map<String, Object> response) {
        String method = (String) response.get(METHOD);
        if (GET_STATUS.equals(method)) {
            delegate.updateStatus((GoProStatus) response.get(STATUS));
        }
    }

    private Map<String, Object> getGoProStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS, false);
        response.put(METHOD, GET_STATUS);

        try {
            GoProStatus status = statusServiceNew.getStatus();
            response.put(SUCCESS, true);
            response.put(STATUS, status);
        } catch (IOException e) {
            Log.e(TAG, "Could not reach the camera");
            Log.d(TAG, "Could not reach the camera", e);
        } catch (ParseException e) {
            Log.e(TAG, "Could not parse the response");
            Log.d(TAG, "Could not parse the response", e);
        }

        return response;
    }

    private void getMediaList() {
        try {
            List<String> filesURL = new ArrayList<String>();
            GoProStatusServiceNew statusServiceNew = new GoProStatusServiceNew();
            JSONObject mediaListJSON = statusServiceNew.getMediaList();
            JSONArray folders = (JSONArray) mediaListJSON.get("media");
            for (Object obj : folders) {
                JSONObject folder = (JSONObject) obj;
                String folderStr = (String) folder.get("d");
                JSONArray files = (JSONArray) folder.get("fs");
                for (Object objF : files) {
                    JSONObject file = (JSONObject) objF;
                    filesURL.add(folderStr + "/" + file.get("n"));
                }
            }
            delegate.updateMediaList(filesURL);
        } catch (IOException e) {
            Log.e(TAG, "Could not reach the camera");
            Log.d(TAG, "Could not reach the camera", e);
        } catch (ParseException e) {
            Log.e(TAG, "Could not parse the response");
            Log.d(TAG, "Could not parse the response", e);
        }
    }
}
