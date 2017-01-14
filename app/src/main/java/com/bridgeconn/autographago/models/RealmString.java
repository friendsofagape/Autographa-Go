package com.bridgeconn.autographago.models;

import io.realm.RealmObject;

public class RealmString extends RealmObject {

    private String value;

    public RealmString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
