package com.example.tom.androidgraph;

import java.util.Date;

/**
 * Created by tom on 7-9-16.
 */
class SensorSample {

    public Date Date;
    public float Density;

    public SensorSample() {
        Date = new Date();
    }

    public long getTime() {
        return Date.getTime();
    }
}