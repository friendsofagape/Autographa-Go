package com.bridgeconn.autographago.models;

import io.realm.RealmObject;

public class RealmInteger extends RealmObject {

    private int value;

    public RealmInteger(int value) {
        this.value = value;
    }

    public RealmInteger() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
