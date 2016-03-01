package com.rbnquintero.personal.mygoproapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.rbnquintero.personal.mygoproapp.objects.GoPro;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProDiscoveryServiceDelegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rbnquintero on 2/15/16.
 */
public class GoProDiscoveryService extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();
    public static final String wifi_not_saved = "Available";
    public static final String wifi_saved = "Saved";
    public static final String wifi_connected = "Connected";

    private Context context;
    private WifiManager mainWifi;
    private GoProDiscoveryServiceDelegate delegate;

    private boolean working = false;
    private StringBuilder sb = new StringBuilder();
    private final Handler handler = new Handler();
    private static List<Pattern> patterns = new ArrayList<Pattern>();

    public GoProDiscoveryService(Context context, GoProDiscoveryServiceDelegate delegate) {
        this.delegate = delegate;
        this.mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.context = context;
    }

    public void searchGoPros() {
        mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mainWifi.setWifiEnabled(true);

        context.registerReceiver(this, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(mainWifi.isWifiEnabled()==false)
        {
            mainWifi.setWifiEnabled(true);
        }

        this.working = true;
        doInBack();
    }

    public void stopSearch() {
        this.working = false;
    }

    public boolean connectToGoPro(GoPro item) {
        return mainWifi.enableNetwork(item.netId, true);
    }

    public boolean isGoProConnected(GoPro item) {
        WifiInfo wifiInfo = mainWifi.getConnectionInfo();
        if(wifiInfo.getBSSID() != null && wifiInfo.getBSSID().equals(item.bssid)) {
            return true;
        }
        return false;
    }

    public boolean addGoPro(GoPro item, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ("\"" + item.title + "\"");
        config.preSharedKey = ("\"" + password + "\"");
        config.networkId = mainWifi.addNetwork(config);
        return mainWifi.enableNetwork(config.networkId, true);
    }

    private void doInBack() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainWifi.startScan();
                if(working) {
                    doInBack();
                }
            }
        }, 5000);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        // Check the recieved intent
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                //do stuff
                Log.i(TAG, "Connected");
            } else {
                // wifi connection was lost
                Log.i(TAG, "Disconnected");
            }
        } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            List<GoPro> connections = new ArrayList<GoPro>();
            List<WifiConfiguration> wifiConfigurationList = mainWifi.getConfiguredNetworks();
            List<String> savedWifis = new ArrayList<String>();
            Map<String, Integer> savedWifisIds = new HashMap<String, Integer>();
            for (WifiConfiguration wifi : wifiConfigurationList) {
                String ssid = wifi.SSID.substring(1, wifi.SSID.length() - 1);
                savedWifis.add(ssid);
                savedWifisIds.put(ssid, wifi.networkId);
            }
            WifiInfo wifiInfo = mainWifi.getConnectionInfo();

            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for (ScanResult wifi : wifiList) {
                String ssid = wifi.SSID;
                String bssid = wifi.BSSID;
                if (bssid != null && matches(bssid, patterns)) {
                    GoPro goPro = new GoPro(ssid, bssid);
                    goPro.scanResult = wifi;
                    if (savedWifis.contains(ssid)) {
                        goPro.status = wifi_saved;
                        goPro.netId = savedWifisIds.get(ssid);
                    }
                    //TODO if not connected
                    if (wifiInfo.getBSSID()!=null && wifiInfo.getBSSID().equals(bssid)) {
                        goPro.status = wifi_connected;
                    }
                    connections.add(goPro);
                }
            }
            this.delegate.updateGoPros(connections);
        }
    }

    public boolean matches (String string, List<Pattern> patterns) {
        for(Pattern p : patterns) {
            Matcher m = p.matcher(string);
            if(m.matches()) {
                return true;
            }
        }
        return false;
    }

    static {
        patterns.add(Pattern.compile(buildPattern("96", "85")));
        patterns.add(Pattern.compile(buildPattern("d9", "19")));
        patterns.add(Pattern.compile(buildPattern("dd", "9e")));
        // patrones de pruebas
        //patterns.add(Pattern.compile(buildPattern("51", "72")));
        //patterns.add(Pattern.compile(buildPattern("3d", "37")));
    }

    public static String buildPattern(String paramString1, String paramString2)
    {
        return "^([0-9a-fA-F][0-9a-fA-F]:)(" + paramString1 + ":)(" + paramString2 + ":)([0-9a-fA-F][0-9a-fA-F]:){2}([0-9a-fA-F][0-9a-fA-F])$";
    }

}
