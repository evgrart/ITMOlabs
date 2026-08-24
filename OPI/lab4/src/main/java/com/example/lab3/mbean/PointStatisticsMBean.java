package com.example.lab3.mbean;

public interface PointStatisticsMBean {

    long getTotalPoints();

    long getMissPoints();

    long getHitPoints();

    void reset();
}
