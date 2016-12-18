package com.bridgeconn.autographago;

import android.app.Application;

import com.bridgeconn.autographago.utils.USFMParser;

/**
 * Created by Admin on 16-12-2016.
 */

public class AutographaGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        USFMParser usfmParser = new USFMParser();
        usfmParser.parseUSFMFile(this, "65-3JN.usfm");
    }
}
