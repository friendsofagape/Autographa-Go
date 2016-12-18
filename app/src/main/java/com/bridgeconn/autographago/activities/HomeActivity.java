package com.bridgeconn.autographago.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bridgeconn.autographago.R;

/**
 * Created by Admin on 18-12-2016.
 */

public class HomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        if(Intent.ACTION_VIEW.equals(intent.getAction())){
            String name = intent.getData().getPath();
        }
    }
}
