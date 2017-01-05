package com.bridgeconn.autographago.ormutils;

public interface Mapper<From, To> {
    To map(From from);
}