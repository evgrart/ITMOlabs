package com.example.lab3.mbean;

final class MonitoringState {

    private long totalPoints;
    private long missPoints;
    private double currentRadius = 1.5;

    synchronized Snapshot registerPoint(boolean hit, double radius) {
        totalPoints++;
        currentRadius = radius;
        if (!hit) {
            missPoints++;
        }

        return snapshot(totalPoints % 10 == 0);
    }

    synchronized Snapshot snapshot() {
        return snapshot(false);
    }

    synchronized void updateRadius(double radius) {
        currentRadius = radius;
    }

    synchronized void reset() {
        totalPoints = 0;
        missPoints = 0;
    }

    private Snapshot snapshot(boolean tenthPoint) {
        return new Snapshot(totalPoints, missPoints, currentRadius, tenthPoint);
    }

    record Snapshot(long totalPoints, long missPoints, double currentRadius, boolean tenthPoint) {

        double area() {
            return currentRadius * currentRadius * (0.75 + Math.PI / 16.0);
        }
    }
}
