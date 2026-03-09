package esvar.ua.botreport.session;

import java.math.BigDecimal;

public class ReportDraft {

    private String locationKey;
    private String employeeName;
    private BigDecimal turnover;
    private Integer buyers;
    private Integer newClients;
    private Integer visitorsWithoutPurchase;
    private BigDecimal cashInRegister;
    private final PaymentData paymentData = new PaymentData();
    private BigDecimal deliveryAmount;
    private BigDecimal collectionAmount;
    private BigDecimal expenses;
    private String nextEmployeeName;

    public String getLocationKey() {
        return locationKey;
    }

    public void setLocationKey(String locationKey) {
        this.locationKey = locationKey;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public Integer getBuyers() {
        return buyers;
    }

    public void setBuyers(Integer buyers) {
        this.buyers = buyers;
    }

    public Integer getNewClients() {
        return newClients;
    }

    public void setNewClients(Integer newClients) {
        this.newClients = newClients;
    }

    public Integer getVisitorsWithoutPurchase() {
        return visitorsWithoutPurchase;
    }

    public void setVisitorsWithoutPurchase(Integer visitorsWithoutPurchase) {
        this.visitorsWithoutPurchase = visitorsWithoutPurchase;
    }

    public BigDecimal getCashInRegister() {
        return cashInRegister;
    }

    public void setCashInRegister(BigDecimal cashInRegister) {
        this.cashInRegister = cashInRegister;
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public BigDecimal getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(BigDecimal deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public BigDecimal getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(BigDecimal collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public String getNextEmployeeName() {
        return nextEmployeeName;
    }

    public void setNextEmployeeName(String nextEmployeeName) {
        this.nextEmployeeName = nextEmployeeName;
    }
}
