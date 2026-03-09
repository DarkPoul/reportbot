package esvar.ua.botreport.session;

import java.math.BigDecimal;

public class PaymentData {

    private BigDecimal cash = BigDecimal.ZERO;
    private BigDecimal cashFiscal = BigDecimal.ZERO;
    private BigDecimal card = BigDecimal.ZERO;
    private BigDecimal onlineCard = BigDecimal.ZERO;
    private BigDecimal onlineCash = BigDecimal.ZERO;

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = defaultMoney(cash);
    }

    public BigDecimal getCashFiscal() {
        return cashFiscal;
    }

    public void setCashFiscal(BigDecimal cashFiscal) {
        this.cashFiscal = defaultMoney(cashFiscal);
    }

    public BigDecimal getCard() {
        return card;
    }

    public void setCard(BigDecimal card) {
        this.card = defaultMoney(card);
    }

    public BigDecimal getOnlineCard() {
        return onlineCard;
    }

    public void setOnlineCard(BigDecimal onlineCard) {
        this.onlineCard = defaultMoney(onlineCard);
    }

    public BigDecimal getOnlineCash() {
        return onlineCash;
    }

    public void setOnlineCash(BigDecimal onlineCash) {
        this.onlineCash = defaultMoney(onlineCash);
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
