package com.greenstate.eveningreport.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FinalReport {
    private Long telegramUserId;
    private LocalDate reportDate;
    private LocalDateTime finalizedAt;
    private ReportDraft snapshot;
    private String formattedText;

    public Long getTelegramUserId() { return telegramUserId; }
    public void setTelegramUserId(Long telegramUserId) { this.telegramUserId = telegramUserId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public LocalDateTime getFinalizedAt() { return finalizedAt; }
    public void setFinalizedAt(LocalDateTime finalizedAt) { this.finalizedAt = finalizedAt; }
    public ReportDraft getSnapshot() { return snapshot; }
    public void setSnapshot(ReportDraft snapshot) { this.snapshot = snapshot; }
    public String getFormattedText() { return formattedText; }
    public void setFormattedText(String formattedText) { this.formattedText = formattedText; }
}
