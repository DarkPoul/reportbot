package com.greenstate.eveningreport.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportServiceTest {
    private final ReportService service = new ReportService();

    @Test
    void conversionShouldBeRounded() {
        assertEquals(100, service.conversionPct(16, 0));
        assertEquals(67, service.conversionPct(2, 1));
    }

    @Test
    void avgCheckShouldBeRounded() {
        assertEquals(530, service.avgCheckUah(8485, 16));
    }

    @Test
    void planFactShouldHaveOneDecimal() {
        assertEquals(20.6, service.planFactPct(533000, 109740));
    }
}
