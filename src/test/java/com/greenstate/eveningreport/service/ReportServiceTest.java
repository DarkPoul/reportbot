package com.greenstate.eveningreport.service;

import com.greenstate.eveningreport.domain.ReportDraft;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportServiceTest {
    private final ReportService service = new ReportService();

    @Test
    void calculatesConversion() {
        ReportDraft d = new ReportDraft();
        d.setBuyersTotal(16);
        d.setVisitorsNoBuy(0);
        assertEquals(100, service.conversionPct(d));
    }

    @Test
    void calculatesAvgCheck() {
        ReportDraft d = new ReportDraft();
        d.setTurnoverUah(8485);
        d.setChecksCount(16);
        assertEquals(530, service.avgCheck(d));
    }

    @Test
    void calculatesPlanFact() {
        ReportDraft d = new ReportDraft();
        d.setPlanUah(533000);
        d.setFactUah(109740);
        assertEquals("20.6", service.planFactPct(d));
    }
}
