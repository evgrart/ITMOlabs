package com.example.lab3.mbean;

public class Area implements AreaMBean {

    private final MonitoringState state;

    Area(MonitoringState state) {
        this.state = state;
    }

    @Override
    public double getRadius() {
        return state.snapshot().currentRadius();
    }

    @Override
    public double getArea() {
        return state.snapshot().area();
    }
}
