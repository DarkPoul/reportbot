package com.greenstate.eveningreport.service;

import com.greenstate.eveningreport.domain.EmployeeProfile;
import com.greenstate.eveningreport.domain.ProductBreakdown;
import com.greenstate.eveningreport.domain.ReportDraft;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

public class ReportService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public int conversionPct(int buyers, int noBuy) {
        int total = buyers + noBuy;
        if (total == 0) return 0;
        return Math.round((buyers * 100f) / total);
    }

    public int avgCheckUah(int turnover, int checks) {
        if (checks <= 0) return 0;
        return Math.round((float) turnover / checks);
    }

    public double planFactPct(int plan, int fact) {
        if (plan <= 0) return 0.0;
        return BigDecimal.valueOf((fact * 100.0) / plan)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public int paymentsSum(ReportDraft draft) {
        return draft.getPayCardUah() + draft.getPayCashUah() + draft.getPayOnlineSiteUah() + draft.getPayCashOnlineUah() + draft.getPayNonFiscalCashUah();
    }

    public String formatReport(EmployeeProfile profile, ReportDraft draft) {
        ProductBreakdown p = draft.getProductBreakdown();
        return String.format("""
%s
м. %s, %s
%s
__
| Продажі/Оборот
| Дата: %s
| Всього покупців: %d
| Відвідувачів без покупки: %d
| Конверсія: %d%%
| Повернення: %d
__
📊 ЗВІТ ПРО ПРОДАЖ:
| 💰Всього оборот: %d UAH
| 📃Чеків: %d
| 🚻Покупців: %d
| 💰 Середній чек: %d
| 🧾 ПЛАН/ФАКТ. %d/%d( %.1f%%)
| 💳Кредитні картки: %d UAH
|  Готівка: %d
|  Онлайн сайт: %d UAH
|  Готівка онлайн: %d
| 💸Готівка не фіскальна: %d UAH
| 🚚Доставка: %d
| 📥Поверенная: %d
|  Инкассация : %d
| 📤Вилучення: %d
|  В кассі на кінець дня: %d UAH
|  Витрати: %d
__
👥 ПОКУПЦІ:
• старих: %d
• нових: %d

GG “L” Light -%d
GG “L” Strong- %d
OG “L” Light - %d
OG “L” Strong-%d
GG "L" super strong -%d
OG "L" super strong - %d

GG “XL” Light - %d
GG “XL” Strong - %d
OG “XL” Light - %d
OG “XL” Strong - %d
GG “XL” Super Strong - %d
OG “XL” super Strong - %d

На вагу по 3:
Ог - %d
Гг - %d
""",
                profile.getBrandName(), profile.getCity(), profile.getAddress(), profile.getFullName(),
                draft.getDate().format(DATE_FORMATTER),
                draft.getBuyersTotal(), draft.getVisitorsNoBuy(), conversionPct(draft.getBuyersTotal(), draft.getVisitorsNoBuy()), draft.getReturnsCount(),
                draft.getTurnoverUah(), draft.getChecksCount(), draft.getBuyersTotal(), avgCheckUah(draft.getTurnoverUah(), draft.getChecksCount()),
                draft.getPlanUah(), draft.getFactUah(), planFactPct(draft.getPlanUah(), draft.getFactUah()),
                draft.getPayCardUah(), draft.getPayCashUah(), draft.getPayOnlineSiteUah(), draft.getPayCashOnlineUah(), draft.getPayNonFiscalCashUah(),
                draft.getDeliveryUah(), draft.getVerifiedUah(), draft.getIncasationUah(), draft.getWithdrawalUah(), draft.getCashEnddayUah(), draft.getExpensesUah(),
                draft.getBuyersOld(), draft.getBuyersNew(),
                p.getGgLLight(), p.getGgLStrong(), p.getOgLLight(), p.getOgLStrong(), p.getGgLSuperStrong(), p.getOgLSuperStrong(),
                p.getGgXlLight(), p.getGgXlStrong(), p.getOgXlLight(), p.getOgXlStrong(), p.getGgXlSuperStrong(), p.getOgXlSuperStrong(),
                p.getWeightOg(), p.getWeightGg()
        );
    }
}
