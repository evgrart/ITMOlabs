package com.example.lab3.service;

public final class AreaHitService {
    private AreaHitService() {
    }

    public static boolean isHit(double x, double y, double r) {
        if (x >= 0 && y <= 0 && x <= r && y >= -r / 2) {
            return true;
        }

        if (x <= 0 && y >= 0 && (x * x + y * y <= (r * r / 4))) {
            return true;
        }

        return x <= 0 && y <= 0 && y >= (-x / 2 - r / 2);
    }
}
