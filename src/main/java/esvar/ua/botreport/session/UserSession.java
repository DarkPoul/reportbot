package esvar.ua.botreport.session;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class UserSession {

    public enum Step {
        WAIT_LOCATION,
        WAIT_NAME,

        WAIT_TURNOVER,       // оборот
        WAIT_CHECKS,         // кількість чеків
        WAIT_NEW_CLIENTS,    // нові клієнти
        WAIT_OLD_CLIENTS,    // старі клієнти
        WAIT_NO_CLIENTS,     // клієнти без покупки

        WAIT_CASH,           // готівка в касі
        WAIT_OLD_CASH,       // факт на вчора

        WAIT_NEXT_NAME,      // завтра на зміні

        WAIT_PAYMENT_MENU,   // <-- НОВЕ: меню типів оплат
        WAIT_PAY_CASH,       // <-- НОВЕ: введення готівки (як тип оплати)
        WAIT_PAY_CASH_F,     // <-- НОВЕ: готівка фіскальна
        WAIT_PAY_CARD,       // <-- НОВЕ: кредитні картки
        WAIT_PAY_ONLINE_CARD,// <-- НОВЕ: онлайн сайт (карта)
        WAIT_PAY_ONLINE_CASH,// <-- НОВЕ: онлайн готівка


        WAIT_CARD,           // оплата картою
        WAIT_SITE_CARD,      // оплата online картою
        WAIT_SITE_CASH,      // оплата online готівкою
        WAIT_CASH_F,         // оплата готівкою фіскал
        WAIT_TAXI,           // Доставка
        WAIT_ATTORNEY,       // Повірена
        WAIT_COLLECTION,     // Інкасація
        WAIT_WITHDRAWAL,     // Вилучення
        WAIT_CASH_EXPENSES,  // Витрати

        READY
    }

    private Step step = Step.WAIT_LOCATION;

    private String location;
    private String fullName;

    private BigDecimal turnover; // UAH
    private Integer checks;
    private Integer newClients;
    private Integer oldClients;
    private Integer noClients;

    private BigDecimal cash; // UAH
    private BigDecimal oldCash; // UAH

    private String nextFullName;

    private BigDecimal card; // UAH
    private BigDecimal onlineCard; // UAH
    private BigDecimal onlineCash; // UAH
    private BigDecimal cashF; // UAH
    private BigDecimal taxi; // UAH
    private BigDecimal attorney; // UAH
    private BigDecimal collection; // UAH
    private BigDecimal withdrawal; // UAH
    private BigDecimal expenses; // UAH

    private BigDecimal cashPayment; // <-- НОВЕ: “Готівка” як тип оплати
}