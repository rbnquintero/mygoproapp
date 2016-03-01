package com.rbnquintero.personal.mygoproapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.rbnquintero.personal.mygoproapp.adapters.GoProListAdapter;
import com.rbnquintero.personal.mygoproapp.objects.GoPro;
import com.rbnquintero.personal.mygoproapp.service.GoProDiscoveryService;
import com.rbnquintero.personal.mygoproapp.service.delegate.GoProDiscoveryServiceDelegate;

import java.util.List;

public class GoProCamerasListActivity extends AppCompatActivity implements GoProDiscoveryServiceDelegate, AdapterView.OnItemClickListener {
    private String TAG = this.getClass().getSimpleName();

    private GoProDiscoveryService discoveryService;

    private ListView goprosLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pro_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        goprosLista = (ListView) findViewById(R.id.goprosList);
        goprosLista.setOnItemClickListener(this);

        discoveryService = new GoProDiscoveryService(getApplicationContext(), this);
        discoveryService.searchGoPros();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause()
    {
        discoveryService.stopSearch();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        discoveryService.searchGoPros();
        super.onResume();
    }

    @Override
    public void updateGoPros(List<GoPro> gopros) {
        TextView lobby_searching = (TextView) findViewById(R.id.lobby_searchingLabel);
        TextView lobby_fail = (TextView) findViewById(R.id.lobby_failLabel);
        GoProListAdapter goprosArrayAdapter = new GoProListAdapter(this, R.layout.listview_item_row, gopros);
        goprosLista.setAdapter(goprosArrayAdapter);
        if(gopros.size()>0) {
            Log.d(TAG, gopros.toString());
            lobby_searching.setAlpha(0f);
            lobby_fail.setAlpha(0f);
        } else {
            Log.d(TAG, "No GoPro camera detected");
            lobby_searching.setAlpha(1f);
            lobby_fail.setAlpha(1f);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoPro item = (GoPro) parent.getItemAtPosition(position);
        Log.d(TAG, "Item selected: " + item);
        if(item.status.equals(GoProDiscoveryService.wifi_connected)) {
            goToControlActivity(item);
        } else if(item.status.equals(GoProDiscoveryService.wifi_saved)) {
            // Connect
            if(discoveryService.connectToGoPro(item)) {
                goToControlActivity(item);
            }
        } else {
            // We have to add the network to the device
            goToAddActivity(item);
        }
    }

    private void goToAddActivity(GoPro item) {
        Intent intent = new Intent(getApplicationContext(), GoProAddActivity.class);
        intent.putExtra("goPro", item);
        startActivity(intent);
    }

    private void goToControlActivity(GoPro item) {
        Intent intent = new Intent(getApplicationContext(), GoProControlActivity.class);
        intent.putExtra("goPro", item);
        startActivity(intent);
    }
}
