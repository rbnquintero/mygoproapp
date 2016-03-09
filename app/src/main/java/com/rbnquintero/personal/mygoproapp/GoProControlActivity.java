package com.rbnquintero.personal.mygoproapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rbnquintero.personal.mygoproapp.adapters.GoProSectionsPageAdapter;
import com.rbnquintero.personal.mygoproapp.business.GoProControlBusiness;
import com.rbnquintero.personal.mygoproapp.business.GoProStatusBusiness;
import com.rbnquintero.personal.mygoproapp.business.delegate.GoProControlBusinessDelegate;
import com.rbnquintero.personal.mygoproapp.business.delegate.GoProStatusBusinessDelegate;
import com.rbnquintero.personal.mygoproapp.objects.GoPro;
import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;
import com.rbnquintero.personal.mygoproapp.service.GoProDiscoveryService;

import java.util.List;

public class GoProControlActivity extends AppCompatActivity implements GoProControlBusinessDelegate, GoProStatusBusinessDelegate {
    private String TAG = this.getClass().getSimpleName();
    public boolean cameraOn = true;
    public GoPro goPro;

    private GoProControlBusiness controlBusiness;
    private GoProStatusBusiness statusBusiness;

    private GoProDiscoveryService discoveryService;

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

    private void initializeCamera() {
        controlBusiness = new GoProControlBusiness(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cameraOn) {
                    if (discoveryService.isGoProConnected(goPro)) {
                        Log.i(TAG, "GoPro connected");
                        controlBusiness.execute(GoProControlBusiness.INITIALIZE_CAMERA, goPro.bssid);
                    } else {
                        Log.d(TAG, "Connecting");
                        setTextConnecting();
                        initializeCamera();
                    }
                }
            }
        }, 1000);
    }

    private void getStatus() {
        statusBusiness = new GoProStatusBusiness(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cameraOn) {
                    statusBusiness.execute(GoProStatusBusiness.GET_STATUS);
                    getStatus();
                }
            }
        }, 2000);
    }

    public void shutterAction(View view) {
        Log.d(TAG, "Shutter action");
        controlBusiness = new GoProControlBusiness(this);
        controlBusiness.execute(GoProControlBusiness.SHUTTER_ACTION);
    }

    @Override
    public void updateStatus(GoProStatus status) {
        try {
            Log.i(TAG, "Battery level: " + status.Battery());
            TextView ctrl_batteryLevel = (TextView) findViewById(R.id.ctrl_batteryLevel);
            ctrl_batteryLevel.setText("Mode: " + status.Mode() + " - " + status.SubMode() + " | Battery: " + status.Battery() + "%");
        } catch (Exception e) {
            setTextConnecting();
            Log.e(TAG, "Could not get status");
            Log.d(TAG, "Could not get status", e);
        }
    }

    @Override
    public void updateMediaList(List<String> media) {
        //TODO nothing
    }

    @Override
    public void onFinishingInitialization(boolean result) {
        if (result) {
            // start receiving status updates
            getStatus();
        } else {
            // could not connect
        }
    }

    private void setTextConnecting() {
        TextView ctrl_batteryLevel = (TextView) findViewById(R.id.ctrl_batteryLevel);
        ctrl_batteryLevel.setText("Connecting to camera");
    }


    // CONTROL FRAGMENT
    public void turnOnOffCamera(View view) {
        ToggleButton button = (ToggleButton) findViewById(R.id.ctrl_toggleButton);
        controlBusiness = new GoProControlBusiness(this);
        if (button.isChecked()) {
            cameraOn = true;
            initializeCamera();
        } else {
            cameraOn = false;
            controlBusiness.execute(GoProControlBusiness.TURN_OFF_CAMERA);
        }
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
}
