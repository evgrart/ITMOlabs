package com.example.lab3.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AreaHitServiceTest {
    @Test
    void shouldHitInsideRectangle() {
        assertTrue(AreaHitService.isHit(1.0, -0.5, 2.0));
    }

    @Test
    void shouldHitInsideQuarterCircle() {
        assertTrue(AreaHitService.isHit(-0.5, 0.5, 2.0));
    }

    @Test
    void shouldHitInsideTriangle() {
        assertTrue(AreaHitService.isHit(-1.0, -0.2, 2.0));
    }

    @Test
    void shouldMissOutsideArea() {
        assertFalse(AreaHitService.isHit(2.0, 2.0, 1.0));
    }
}
