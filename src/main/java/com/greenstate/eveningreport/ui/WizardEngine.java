package com.greenstate.eveningreport.ui;

import com.greenstate.eveningreport.domain.ProductBreakdown;
import com.greenstate.eveningreport.domain.ReportDraft;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class WizardEngine {
    private static final List<WizardState> REPORT_STEPS = List.of(
            WizardState.REPORT_DATE, WizardState.REPORT_BUYERS_TOTAL, WizardState.REPORT_VISITORS_NO_BUY,
            WizardState.REPORT_RETURNS, WizardState.REPORT_TURNOVER, WizardState.REPORT_CHECKS,
            WizardState.REPORT_PLAN, WizardState.REPORT_FACT, WizardState.REPORT_PAY_CARD,
            WizardState.REPORT_PAY_CASH, WizardState.REPORT_PAY_SITE, WizardState.REPORT_PAY_CASH_ONLINE,
            WizardState.REPORT_PAY_NON_FISCAL, WizardState.REPORT_DELIVERY, WizardState.REPORT_VERIFIED,
            WizardState.REPORT_INCASATION, WizardState.REPORT_WITHDRAWAL, WizardState.REPORT_CASH_ENDDAY,
            WizardState.REPORT_EXPENSES, WizardState.REPORT_BUYERS_OLD, WizardState.REPORT_BUYERS_NEW,
            WizardState.PRODUCT_GG_L_LIGHT, WizardState.PRODUCT_GG_L_STRONG, WizardState.PRODUCT_OG_L_LIGHT,
            WizardState.PRODUCT_OG_L_STRONG, WizardState.PRODUCT_GG_L_SUPER, WizardState.PRODUCT_OG_L_SUPER,
            WizardState.PRODUCT_GG_XL_LIGHT, WizardState.PRODUCT_GG_XL_STRONG, WizardState.PRODUCT_OG_XL_LIGHT,
            WizardState.PRODUCT_OG_XL_STRONG, WizardState.PRODUCT_GG_XL_SUPER, WizardState.PRODUCT_OG_XL_SUPER,
            WizardState.PRODUCT_WEIGHT_OG, WizardState.PRODUCT_WEIGHT_GG
    );

    public int totalSteps() { return REPORT_STEPS.size(); }

    public String question(ReportDraft d) {
        int idx = REPORT_STEPS.indexOf(d.getState()) + 1;
        return Messages.progress(idx, totalSteps(), switch (d.getState()) {
            case REPORT_DATE -> "Вкажіть дату звіту (dd.MM.yyyy або yyyy-MM-dd). Приклад: 23.02.2026";
            case REPORT_BUYERS_TOTAL -> "Всього покупців (ціле число). Приклад: 16";
            case REPORT_VISITORS_NO_BUY -> "Відвідувачів без покупки. Приклад: 0";
            case REPORT_RETURNS -> "Повернення. Приклад: 0";
            case REPORT_TURNOVER -> "Всього оборот (UAH). Приклад: 8485";
            case REPORT_CHECKS -> "Кількість чеків. Приклад: 16";
            case REPORT_PLAN -> "План (UAH). Приклад: 533000";
            case REPORT_FACT -> "Факт МТД (UAH). Приклад: 109740";
            case REPORT_PAY_CARD -> "Оплата картками (UAH). Приклад: 2390";
            case REPORT_PAY_CASH -> "Готівка. Приклад: 0";
            case REPORT_PAY_SITE -> "Онлайн сайт (UAH). Приклад: 0";
            case REPORT_PAY_CASH_ONLINE -> "Готівка онлайн. Приклад: 0";
            case REPORT_PAY_NON_FISCAL -> "Готівка не фіскальна (UAH). Приклад: 6095";
            case REPORT_DELIVERY -> "Доставка. Приклад: 0";
            case REPORT_VERIFIED -> "Поверенная. Приклад: 0";
            case REPORT_INCASATION -> "Инкассация. Приклад: 0";
            case REPORT_WITHDRAWAL -> "Вилучення. Приклад: 0";
            case REPORT_CASH_ENDDAY -> "В касі на кінець дня (UAH). Приклад: 38230";
            case REPORT_EXPENSES -> "Витрати. Приклад: 3576";
            case REPORT_BUYERS_OLD -> "Старих покупців. Приклад: 13";
            case REPORT_BUYERS_NEW -> "Нових покупців. Приклад: 3";
            case PRODUCT_GG_L_LIGHT -> "GG “L” Light. Приклад: 124";
            case PRODUCT_GG_L_STRONG -> "GG “L” Strong. Приклад: 45";
            case PRODUCT_OG_L_LIGHT -> "OG “L” Light. Приклад: 116";
            case PRODUCT_OG_L_STRONG -> "OG “L” Strong. Приклад: 45";
            case PRODUCT_GG_L_SUPER -> "GG " + '"' + "L" + '"' + " super strong. Приклад: 75";
            case PRODUCT_OG_L_SUPER -> "OG " + '"' + "L" + '"' + " super strong. Приклад: 110";
            case PRODUCT_GG_XL_LIGHT -> "GG “XL” Light. Приклад: 46";
            case PRODUCT_GG_XL_STRONG -> "GG “XL” Strong. Приклад: 1";
            case PRODUCT_OG_XL_LIGHT -> "OG “XL” Light. Приклад: 67";
            case PRODUCT_OG_XL_STRONG -> "OG “XL” Strong. Приклад: 0";
            case PRODUCT_GG_XL_SUPER -> "GG “XL” Super Strong. Приклад: 15";
            case PRODUCT_OG_XL_SUPER -> "OG “XL” super Strong. Приклад: 0";
            case PRODUCT_WEIGHT_OG -> "На вагу по 3: Ог. Приклад: 62";
            case PRODUCT_WEIGHT_GG -> "На вагу по 3: Гг. Приклад: 81";
            default -> "";
        });
    }

    public String applyInput(ReportDraft d, String input) {
        try {
            switch (d.getState()) {
                case REPORT_DATE -> d.setReportDate(parseDate(input));
                case REPORT_BUYERS_TOTAL -> d.setBuyersTotal(parseInt(input));
                case REPORT_VISITORS_NO_BUY -> d.setVisitorsNoBuy(parseInt(input));
                case REPORT_RETURNS -> d.setReturnsCount(parseInt(input));
                case REPORT_TURNOVER -> d.setTurnoverUah(parseInt(input));
                case REPORT_CHECKS -> d.setChecksCount(parseInt(input));
                case REPORT_PLAN -> d.setPlanUah(parseInt(input));
                case REPORT_FACT -> d.setFactUah(parseInt(input));
                case REPORT_PAY_CARD -> d.setPayCardUah(parseInt(input));
                case REPORT_PAY_CASH -> d.setPayCashUah(parseInt(input));
                case REPORT_PAY_SITE -> d.setPayOnlineSiteUah(parseInt(input));
                case REPORT_PAY_CASH_ONLINE -> d.setPayCashOnlineUah(parseInt(input));
                case REPORT_PAY_NON_FISCAL -> d.setPayNonFiscalCashUah(parseInt(input));
                case REPORT_DELIVERY -> d.setDeliveryUah(parseInt(input));
                case REPORT_VERIFIED -> d.setVerifiedUah(parseInt(input));
                case REPORT_INCASATION -> d.setIncasationUah(parseInt(input));
                case REPORT_WITHDRAWAL -> d.setWithdrawalUah(parseInt(input));
                case REPORT_CASH_ENDDAY -> d.setCashEnddayUah(parseInt(input));
                case REPORT_EXPENSES -> d.setExpensesUah(parseInt(input));
                case REPORT_BUYERS_OLD -> d.setBuyersOld(parseInt(input));
                case REPORT_BUYERS_NEW -> d.setBuyersNew(parseInt(input));
                default -> setProductValue(d.getProductBreakdown(), d.getState(), parseInt(input));
            }
        } catch (Exception e) {
            if (d.getState() == WizardState.REPORT_DATE) {
                return "❌ Некоректна дата. Формат: dd.MM.yyyy або yyyy-MM-dd";
            }
            return "❌ Введіть, будь ласка, ціле число.";
        }

        int i = REPORT_STEPS.indexOf(d.getState());
        if (i == REPORT_STEPS.size() - 1) {
            d.getHistory().add(d.getState());
            d.setState(WizardState.REPORT_CONFIRM);
            return null;
        }
        d.getHistory().add(d.getState());
        d.setState(REPORT_STEPS.get(i + 1));
        return null;
    }

    public void goBack(ReportDraft d) {
        if (d.getHistory().isEmpty()) return;
        WizardState prev = d.getHistory().remove(d.getHistory().size() - 1);
        d.setState(prev);
    }

    public void initDefaultDate(ReportDraft d) {
        if (d.getReportDate() == null) {
            d.setReportDate(LocalDate.now(ZoneId.of("Europe/Kyiv")));
        }
    }

    private int parseInt(String s) { return Integer.parseInt(s.trim()); }
    private LocalDate parseDate(String s) {
        String v = s.trim();
        try {
            return LocalDate.parse(v, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException ignore) {
            return LocalDate.parse(v);
        }
    }

    private void setProductValue(ProductBreakdown b, WizardState state, int value) {
        switch (state) {
            case PRODUCT_GG_L_LIGHT -> b.setGgLLight(value);
            case PRODUCT_GG_L_STRONG -> b.setGgLStrong(value);
            case PRODUCT_OG_L_LIGHT -> b.setOgLLight(value);
            case PRODUCT_OG_L_STRONG -> b.setOgLStrong(value);
            case PRODUCT_GG_L_SUPER -> b.setGgLSuperStrong(value);
            case PRODUCT_OG_L_SUPER -> b.setOgLSuperStrong(value);
            case PRODUCT_GG_XL_LIGHT -> b.setGgXlLight(value);
            case PRODUCT_GG_XL_STRONG -> b.setGgXlStrong(value);
            case PRODUCT_OG_XL_LIGHT -> b.setOgXlLight(value);
            case PRODUCT_OG_XL_STRONG -> b.setOgXlStrong(value);
            case PRODUCT_GG_XL_SUPER -> b.setGgXlSuperStrong(value);
            case PRODUCT_OG_XL_SUPER -> b.setOgXlSuperStrong(value);
            case PRODUCT_WEIGHT_OG -> b.setWeightOg(value);
            case PRODUCT_WEIGHT_GG -> b.setWeightGg(value);
            default -> throw new IllegalArgumentException("Unsupported state");
        }
    }
}
