package com.rbnquintero.personal.mygoproapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rbnquintero.personal.mygoproapp.objects.GoPro;
import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.service.GoProDiscoveryService;
import com.rbnquintero.personal.mygoproapp.service.GoProStatusService;
import com.rbnquintero.personal.mygoproapp.service.GoProWakeUpService;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProStatusServiceDelegate;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProWakeUpServiceDelegate;

public class GoProControlOldActivity extends AppCompatActivity implements GoProStatusServiceDelegate, GoProWakeUpServiceDelegate {
    private String TAG = this.getClass().getSimpleName();
    private boolean getStatus = false;
    private GoPro goPro;

    private GoProDiscoveryService discoveryService;
    private GoProWakeUpService wakeUpService;
    private GoProStatusService statusService;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pro_control_old);

        // Get the GoPro object from the previous intent
        Intent intent = getIntent();
        goPro = (GoPro) intent.getParcelableExtra("goPro");
        Log.i(TAG, "Initializing camera: " + goPro.title);

        // We set the services
        discoveryService = new GoProDiscoveryService(getApplicationContext(), null);

        initializeCamera();
    }

    private void initializeCamera() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Checking if GoPro has finished connecting");
                if (discoveryService.isGoProConnected(goPro)) {
                    Log.i(TAG, "GoPro connected, trying to wake up");
                    getStatusInitial();
                } else {
                    initializeCamera();
                }
            }
        }, 1000);
    }

    public void shutterAction(View view) {
        Log.d(TAG, "Shutter action");
    }

    @Override
    public void updateStatus(GoProStatus status) {
        LinearLayout ctrl_loadingLayout = (LinearLayout) findViewById(R.id.ctrl_loadingLayout);
        ctrl_loadingLayout.setAlpha(0);
        Log.d(TAG, "Battery level: " + status.getStatusItem(GoProStatus.InternalBatteryLevel));
        LinearLayout ctrl_mainControls = (LinearLayout) findViewById(R.id.ctrl_mainControls);
        TextView ctrl_batteryLevel = (TextView) findViewById(R.id.ctrl_batteryLevel);
        ctrl_batteryLevel.setText("Battery: " + ((long) status.getStatusItem(GoProStatus.InternalBatteryLevel) * 100 / 4) + "%");
        ctrl_mainControls.setAlpha(1);
    }

    @Override
    public void updateStatusTimeout() {
        Log.d(TAG, "Camera status timeout");
        if (getStatus = true) {
            getStatus = false;
            wakeUpCamera();
        }
    }

    @Override
    public void wakeUpSuccess() {
        getStatus();
    }

    @Override
    public void wakeUpError() {
        Log.d(TAG, "Camera wakeup timeout");
    }

    private void wakeUpCamera() {
        wakeUpService = new GoProWakeUpService(this);
        wakeUpService.execute(goPro.bssid);
    }

    private void getStatus() {
        Log.i(TAG, "Getting camera status");
        statusService = new GoProStatusService(this);
        statusService.execute();
    }

    private void getStatusInitial() {
        getStatus = true;
        statusService = new GoProStatusService(this);
        statusService.execute("3000");
    }
}
