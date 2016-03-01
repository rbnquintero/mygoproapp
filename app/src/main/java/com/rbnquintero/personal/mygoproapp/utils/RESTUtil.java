package com.rbnquintero.personal.mygoproapp.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rbnquintero on 2/26/16.
 */
public class RESTUtil {
    private static String TAG = "RESTUtil";

    public static enum HTTPMethod {GET, POST, PUT, DELETE};
    public static final String JSON = "application/json";

    public static String httpConnection(String urlStr, String contentType, HTTPMethod httpMethod) throws IOException {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestMethod(httpMethod.toString());
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);

            String respuesta = null;
            try {
                respuesta = getResponseFromStream(conn.getInputStream());
            } catch (FileNotFoundException e) {
                if (conn.getErrorStream() != null) {
                    respuesta = getResponseFromStream(conn.getErrorStream());
                } else {
                    throw e;
                }
            }

            //Log.d(TAG, "Status response \n" + respuesta);
            return respuesta;
        } catch (Exception e) {
            //Log.e(TAG, "Could not get HTTP request");
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String getResponseFromStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
}
