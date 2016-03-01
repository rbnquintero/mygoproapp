package com.rbnquintero.personal.mygoproapp.service;

import android.os.AsyncTask;

import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProStatusServiceDelegate;
import com.rbnquintero.personal.mygoproapp.utils.RESTUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by rbnquintero on 2/29/16.
 */
public class GoProStatusServiceNew {
    private String TAG = this.getClass().getSimpleName();

    private static final String ROOT_URL = "http://10.5.5.9";
    private static final String STATUS_URL = "/gp/gpControl/status/";

    public GoProStatus getStatus() throws IOException, ParseException {
        String respuesta = RESTUtil.httpConnection(ROOT_URL + STATUS_URL, RESTUtil.JSON, RESTUtil.HTTPMethod.GET);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(respuesta);
        GoProStatus status = new GoProStatus((JSONObject) obj);
        return status;
    }

}
