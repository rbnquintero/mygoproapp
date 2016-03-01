package com.rbnquintero.personal.mygoproapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rbnquintero.personal.mygoproapp.adapters.GoProSectionsPageAdapter;
import com.rbnquintero.personal.mygoproapp.business.GoProControlBusiness;
import com.rbnquintero.personal.mygoproapp.business.delegate.GoProControlBusinessDelegate;
import com.rbnquintero.personal.mygoproapp.objects.GoPro;
import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.service.GoProControlService;
import com.rbnquintero.personal.mygoproapp.service.GoProDiscoveryService;
import com.rbnquintero.personal.mygoproapp.service.GoProStatusService;
import com.rbnquintero.personal.mygoproapp.service.GoProWakeUpService;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProStatusServiceDelegate;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProWakeUpServiceDelegate;

public class GoProControlActivity extends AppCompatActivity implements GoProStatusServiceDelegate, GoProWakeUpServiceDelegate, GoProControlBusinessDelegate {
    private String TAG = this.getClass().getSimpleName();
    private boolean getStatus = false;
    public boolean cameraOn = true;
    public GoPro goPro;

    private GoProControlBusiness controlBusiness;

    private GoProDiscoveryService discoveryService;
    private GoProWakeUpService wakeUpService;
    private GoProStatusService statusService;
    private GoProControlService controlService;
    private final Handler handler = new Handler();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private GoProSectionsPageAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pro_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new GoProSectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Get the GoPro object from the previous intent
        Intent intent = getIntent();
        goPro = (GoPro) intent.getParcelableExtra("goPro");
        Log.i(TAG, "Initializing camera: " + goPro.title);

        // We set the services
        discoveryService = new GoProDiscoveryService(getApplicationContext(), null);

        initializeCamera();

    }

    public void initializeCamera() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Checking if GoPro has finished connecting");
                if (discoveryService.isGoProConnected(goPro)) {
                    Log.i(TAG, "GoPro connected");
                    getStatusInitial();
                } else {
                    Log.d(TAG, "Connecting");
                    setTextConnecting();
                }
                if(cameraOn) {
                    initializeCamera();
                }
            }
        }, 5000);
    }

    public void shutterAction(View view) {
        Log.d(TAG, "Shutter action");
        controlBusiness = new GoProControlBusiness(this);
        controlBusiness.execute(GoProControlBusiness.SHUTTER_ACTION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_go_pro_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateStatus(GoProStatus status) {
        try {
            Log.d(TAG, "Battery level: " + status.Battery());
            TextView ctrl_batteryLevel = (TextView) findViewById(R.id.ctrl_batteryLevel);
            ctrl_batteryLevel.setText("Mode: " + status.Mode() + " - " + status.SubMode() + " | Battery: " + status.Battery() + "%");
        } catch (Exception e) {
            Log.e(TAG, "Could not get status");
        }
    }

    @Override
    public void updateStatusTimeout() {
        Log.d(TAG, "Camera status timeout");
        setTextConnecting();
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

    private void setTextConnecting() {
        TextView ctrl_batteryLevel = (TextView) findViewById(R.id.ctrl_batteryLevel);
        ctrl_batteryLevel.setText("Connecting to camera");
    }


    // CONTROL FRAGMENT
    public void turnOnOffCamera(View view) {
        ToggleButton button = (ToggleButton) findViewById(R.id.ctrl_toggleButton);
        controlService = new GoProControlService();
        if(button.isChecked()) {
            cameraOn = true;
            initializeCamera();
        } else {
            cameraOn = false;
            controlService.execute(GoProControlService.TURNOFF_CAMERA);
        }
    }

    @Override
    public void onFinishingInitialization(boolean result) {
        if(result) {
            // start receiving status updates
        } else {
            // could not connect
        }
    }
}
