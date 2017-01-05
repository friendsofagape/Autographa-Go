package com.bridgeconn.autographago.ormutils;

public interface RepositoryRequestListener {

    void onSuccess();

    void onError(Throwable throwable);
}
