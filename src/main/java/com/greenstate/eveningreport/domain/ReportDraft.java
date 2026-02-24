package com.greenstate.eveningreport.domain;

import com.greenstate.eveningreport.ui.WizardState;

import java.time.LocalDate;

public class ReportDraft {
    private Long telegramUserId;
    private WizardType wizardType = WizardType.REPORT;
    private WizardState currentStep;
    private LocalDate date;
    private Integer buyersTotal;
    private Integer visitorsNoBuy;
    private Integer returnsCount;
    private Integer turnoverUah;
    private Integer checksCount;
    private Integer planUah;
    private Integer factUah;
    private Integer payCardUah;
    private Integer payCashUah;
    private Integer payOnlineSiteUah;
    private Integer payCashOnlineUah;
    private Integer payNonFiscalCashUah;
    private Integer deliveryUah;
    private Integer verifiedUah;
    private Integer incasationUah;
    private Integer withdrawalUah;
    private Integer cashEnddayUah;
    private Integer expensesUah;
    private Integer buyersOld;
    private Integer buyersNew;
    private ProductBreakdown productBreakdown = new ProductBreakdown();
    private String regFullName;
    private String regCity;
    private String regAddress;
    private String regBrandName;

    public Long getTelegramUserId() { return telegramUserId; }
    public void setTelegramUserId(Long telegramUserId) { this.telegramUserId = telegramUserId; }
    public WizardType getWizardType() { return wizardType; }
    public void setWizardType(WizardType wizardType) { this.wizardType = wizardType; }
    public WizardState getCurrentStep() { return currentStep; }
    public void setCurrentStep(WizardState currentStep) { this.currentStep = currentStep; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getBuyersTotal() { return buyersTotal; }
    public void setBuyersTotal(Integer buyersTotal) { this.buyersTotal = buyersTotal; }
    public Integer getVisitorsNoBuy() { return visitorsNoBuy; }
    public void setVisitorsNoBuy(Integer visitorsNoBuy) { this.visitorsNoBuy = visitorsNoBuy; }
    public Integer getReturnsCount() { return returnsCount; }
    public void setReturnsCount(Integer returnsCount) { this.returnsCount = returnsCount; }
    public Integer getTurnoverUah() { return turnoverUah; }
    public void setTurnoverUah(Integer turnoverUah) { this.turnoverUah = turnoverUah; }
    public Integer getChecksCount() { return checksCount; }
    public void setChecksCount(Integer checksCount) { this.checksCount = checksCount; }
    public Integer getPlanUah() { return planUah; }
    public void setPlanUah(Integer planUah) { this.planUah = planUah; }
    public Integer getFactUah() { return factUah; }
    public void setFactUah(Integer factUah) { this.factUah = factUah; }
    public Integer getPayCardUah() { return payCardUah; }
    public void setPayCardUah(Integer payCardUah) { this.payCardUah = payCardUah; }
    public Integer getPayCashUah() { return payCashUah; }
    public void setPayCashUah(Integer payCashUah) { this.payCashUah = payCashUah; }
    public Integer getPayOnlineSiteUah() { return payOnlineSiteUah; }
    public void setPayOnlineSiteUah(Integer payOnlineSiteUah) { this.payOnlineSiteUah = payOnlineSiteUah; }
    public Integer getPayCashOnlineUah() { return payCashOnlineUah; }
    public void setPayCashOnlineUah(Integer payCashOnlineUah) { this.payCashOnlineUah = payCashOnlineUah; }
    public Integer getPayNonFiscalCashUah() { return payNonFiscalCashUah; }
    public void setPayNonFiscalCashUah(Integer payNonFiscalCashUah) { this.payNonFiscalCashUah = payNonFiscalCashUah; }
    public Integer getDeliveryUah() { return deliveryUah; }
    public void setDeliveryUah(Integer deliveryUah) { this.deliveryUah = deliveryUah; }
    public Integer getVerifiedUah() { return verifiedUah; }
    public void setVerifiedUah(Integer verifiedUah) { this.verifiedUah = verifiedUah; }
    public Integer getIncasationUah() { return incasationUah; }
    public void setIncasationUah(Integer incasationUah) { this.incasationUah = incasationUah; }
    public Integer getWithdrawalUah() { return withdrawalUah; }
    public void setWithdrawalUah(Integer withdrawalUah) { this.withdrawalUah = withdrawalUah; }
    public Integer getCashEnddayUah() { return cashEnddayUah; }
    public void setCashEnddayUah(Integer cashEnddayUah) { this.cashEnddayUah = cashEnddayUah; }
    public Integer getExpensesUah() { return expensesUah; }
    public void setExpensesUah(Integer expensesUah) { this.expensesUah = expensesUah; }
    public Integer getBuyersOld() { return buyersOld; }
    public void setBuyersOld(Integer buyersOld) { this.buyersOld = buyersOld; }
    public Integer getBuyersNew() { return buyersNew; }
    public void setBuyersNew(Integer buyersNew) { this.buyersNew = buyersNew; }
    public ProductBreakdown getProductBreakdown() { return productBreakdown; }
    public void setProductBreakdown(ProductBreakdown productBreakdown) { this.productBreakdown = productBreakdown; }

    public String getRegFullName() { return regFullName; }
    public void setRegFullName(String regFullName) { this.regFullName = regFullName; }
    public String getRegCity() { return regCity; }
    public void setRegCity(String regCity) { this.regCity = regCity; }
    public String getRegAddress() { return regAddress; }
    public void setRegAddress(String regAddress) { this.regAddress = regAddress; }
    public String getRegBrandName() { return regBrandName; }
    public void setRegBrandName(String regBrandName) { this.regBrandName = regBrandName; }
}

