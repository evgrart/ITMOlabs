package com.example.lab4.service;

import jakarta.ejb.Stateless;

@Stateless
public class AreaCheckService {
    public boolean check(double x, double y, double r) {
        if (r <= 0) return false;
        
        if (x <= 0 && y >= 0) {
            return (x * x + y * y <= r * r);
        }

        if (x <= 0 && y <= 0) {
            return (x >= -r && y >= -r / 2.0);
        }

        if (x >= 0 && y <= 0) {
            return (y >= x - r / 2.0);
        }

        return false;
    }
}