package com.rbnquintero.personal.mygoproapp.service;

import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.utils.RESTUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by rbnquintero on 2/29/16.
 */
public class GoProControlServiceNew {
    private String TAG = this.getClass().getSimpleName();

    private static final String ROOT_URL = "http://10.5.5.9";
    private static final String turnOffUrl = "/gp/gpControl/command/system/sleep";
    private static final String shutterOnURL = "/gp/gpControl/command/shutter?p=1";
    private static final String shutterOffURL = "/gp/gpControl/command/shutter?p=0";

    public void turnOffCamera() throws IOException {
        RESTUtil.httpConnection(ROOT_URL + turnOffUrl, RESTUtil.JSON, RESTUtil.HTTPMethod.GET);
    }

    public void shutterOnCamera() throws IOException {
        RESTUtil.httpConnection(ROOT_URL + shutterOnURL, RESTUtil.JSON, RESTUtil.HTTPMethod.GET);
    }

    public void shutterOffCamera() throws IOException {
        RESTUtil.httpConnection(ROOT_URL + shutterOffURL, RESTUtil.JSON, RESTUtil.HTTPMethod.GET);
    }

}
