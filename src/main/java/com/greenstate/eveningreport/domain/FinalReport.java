package com.greenstate.eveningreport.domain;

import java.time.Instant;

public class FinalReport {
    private long telegramUserId;
    private ReportDraft data;
    private String renderedText;
    private Instant createdAt;

    public long getTelegramUserId() { return telegramUserId; }
    public void setTelegramUserId(long telegramUserId) { this.telegramUserId = telegramUserId; }
    public ReportDraft getData() { return data; }
    public void setData(ReportDraft data) { this.data = data; }
    public String getRenderedText() { return renderedText; }
    public void setRenderedText(String renderedText) { this.renderedText = renderedText; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
