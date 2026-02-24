package com.greenstate.eveningreport.domain;

import com.greenstate.eveningreport.ui.WizardState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDraft {
    private long telegramUserId;
    private LocalDate reportDate;
    private WizardState state = WizardState.REPORT_DATE;
    private List<WizardState> history = new ArrayList<>();

    private int buyersTotal;
    private int visitorsNoBuy;
    private int returnsCount;
    private int turnoverUah;
    private int checksCount;
    private int planUah;
    private int factUah;
    private int payCardUah;
    private int payCashUah;
    private int payOnlineSiteUah;
    private int payCashOnlineUah;
    private int payNonFiscalCashUah;
    private int deliveryUah;
    private int verifiedUah;
    private int incasationUah;
    private int withdrawalUah;
    private int cashEnddayUah;
    private int expensesUah;
    private int buyersOld;
    private int buyersNew;
    private ProductBreakdown productBreakdown = new ProductBreakdown();

    public long getTelegramUserId() { return telegramUserId; }
    public void setTelegramUserId(long telegramUserId) { this.telegramUserId = telegramUserId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public WizardState getState() { return state; }
    public void setState(WizardState state) { this.state = state; }
    public List<WizardState> getHistory() { return history; }
    public void setHistory(List<WizardState> history) { this.history = history; }
    public int getBuyersTotal() { return buyersTotal; }
    public void setBuyersTotal(int buyersTotal) { this.buyersTotal = buyersTotal; }
    public int getVisitorsNoBuy() { return visitorsNoBuy; }
    public void setVisitorsNoBuy(int visitorsNoBuy) { this.visitorsNoBuy = visitorsNoBuy; }
    public int getReturnsCount() { return returnsCount; }
    public void setReturnsCount(int returnsCount) { this.returnsCount = returnsCount; }
    public int getTurnoverUah() { return turnoverUah; }
    public void setTurnoverUah(int turnoverUah) { this.turnoverUah = turnoverUah; }
    public int getChecksCount() { return checksCount; }
    public void setChecksCount(int checksCount) { this.checksCount = checksCount; }
    public int getPlanUah() { return planUah; }
    public void setPlanUah(int planUah) { this.planUah = planUah; }
    public int getFactUah() { return factUah; }
    public void setFactUah(int factUah) { this.factUah = factUah; }
    public int getPayCardUah() { return payCardUah; }
    public void setPayCardUah(int payCardUah) { this.payCardUah = payCardUah; }
    public int getPayCashUah() { return payCashUah; }
    public void setPayCashUah(int payCashUah) { this.payCashUah = payCashUah; }
    public int getPayOnlineSiteUah() { return payOnlineSiteUah; }
    public void setPayOnlineSiteUah(int payOnlineSiteUah) { this.payOnlineSiteUah = payOnlineSiteUah; }
    public int getPayCashOnlineUah() { return payCashOnlineUah; }
    public void setPayCashOnlineUah(int payCashOnlineUah) { this.payCashOnlineUah = payCashOnlineUah; }
    public int getPayNonFiscalCashUah() { return payNonFiscalCashUah; }
    public void setPayNonFiscalCashUah(int payNonFiscalCashUah) { this.payNonFiscalCashUah = payNonFiscalCashUah; }
    public int getDeliveryUah() { return deliveryUah; }
    public void setDeliveryUah(int deliveryUah) { this.deliveryUah = deliveryUah; }
    public int getVerifiedUah() { return verifiedUah; }
    public void setVerifiedUah(int verifiedUah) { this.verifiedUah = verifiedUah; }
    public int getIncasationUah() { return incasationUah; }
    public void setIncasationUah(int incasationUah) { this.incasationUah = incasationUah; }
    public int getWithdrawalUah() { return withdrawalUah; }
    public void setWithdrawalUah(int withdrawalUah) { this.withdrawalUah = withdrawalUah; }
    public int getCashEnddayUah() { return cashEnddayUah; }
    public void setCashEnddayUah(int cashEnddayUah) { this.cashEnddayUah = cashEnddayUah; }
    public int getExpensesUah() { return expensesUah; }
    public void setExpensesUah(int expensesUah) { this.expensesUah = expensesUah; }
    public int getBuyersOld() { return buyersOld; }
    public void setBuyersOld(int buyersOld) { this.buyersOld = buyersOld; }
    public int getBuyersNew() { return buyersNew; }
    public void setBuyersNew(int buyersNew) { this.buyersNew = buyersNew; }
    public ProductBreakdown getProductBreakdown() { return productBreakdown; }
    public void setProductBreakdown(ProductBreakdown productBreakdown) { this.productBreakdown = productBreakdown; }
}
