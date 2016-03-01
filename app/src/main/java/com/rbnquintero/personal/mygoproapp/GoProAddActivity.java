package com.rbnquintero.personal.mygoproapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rbnquintero.personal.mygoproapp.objects.GoPro;
import com.rbnquintero.personal.mygoproapp.service.GoProDiscoveryService;

public class GoProAddActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private GoPro goPro;
    private GoProDiscoveryService goProDiscoveryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pro_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        goProDiscoveryService = new GoProDiscoveryService(getApplicationContext(), null);

        Intent intent = getIntent();
        goPro = (GoPro) intent.getParcelableExtra("goPro");
        Log.i("Sent data", goPro.title);
    }

    public void addCamera(View view) {
        EditText add_passwordField = (EditText) findViewById(R.id.add_passwordField);
        if(add_passwordField.getText() != null && !add_passwordField.getText().toString().isEmpty()) {
            if (goProDiscoveryService.addGoPro(goPro, add_passwordField.getText().toString())) {
                goToControlActivity(goPro);
            } else {
                Toast.makeText(getApplicationContext(),"Invalid password", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Please enter a password", Toast.LENGTH_LONG).show();
        }
    }

    private void goToControlActivity(GoPro item) {
        Intent intent = new Intent(getApplicationContext(), GoProControlActivity.class);
        intent.putExtra("goPro", item);
        startActivity(intent);
    }

}
