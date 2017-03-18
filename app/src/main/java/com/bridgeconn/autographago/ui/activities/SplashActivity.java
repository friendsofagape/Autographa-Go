package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;

public class SplashActivity extends AppCompatActivity {

    private String languageCode, versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startHome();
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();

        if (!SharedPrefs.getBoolean(Constants.PrefKeys.FIRST_LAUNCH, false)){

            SharedPrefs.putBoolean(Constants.PrefKeys.FIRST_LAUNCH, true);
        } else {
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e) {
            }
        }
    }
*/
    private void startHome() {
        languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);

        new AutographaRepository<LanguageModel>().addToNewContainer(languageCode, versionCode);

        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);

        finish();
    }
}