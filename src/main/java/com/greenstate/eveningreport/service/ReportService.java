package com.greenstate.eveningreport.service;

import com.greenstate.eveningreport.domain.EmployeeProfile;
import com.greenstate.eveningreport.domain.ProductBreakdown;
import com.greenstate.eveningreport.domain.ReportDraft;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public int conversionPct(ReportDraft draft) {
        int denominator = draft.getBuyersTotal() + draft.getVisitorsNoBuy();
        if (denominator == 0) return 0;
        return (int) Math.round((draft.getBuyersTotal() * 100.0) / denominator);
    }

    public int avgCheck(ReportDraft draft) {
        if (draft.getChecksCount() == 0) return 0;
        return (int) Math.round(draft.getTurnoverUah() / (double) draft.getChecksCount());
    }

    public String planFactPct(ReportDraft draft) {
        if (draft.getPlanUah() == 0) return "0.0";
        double pct = draft.getFactUah() * 100.0 / draft.getPlanUah();
        return String.format(Locale.US, "%.1f", pct);
    }

    public int paymentSum(ReportDraft draft) {
        return draft.getPayCardUah() + draft.getPayCashUah() + draft.getPayOnlineSiteUah() + draft.getPayCashOnlineUah() + draft.getPayNonFiscalCashUah();
    }

    public String render(EmployeeProfile p, ReportDraft d, boolean withWarning) {
        ProductBreakdown b = d.getProductBreakdown();
        StringBuilder sb = new StringBuilder();
        sb.append(p.getStoreName()).append("\n")
                .append("м. ").append(p.getCity()).append(", ").append(p.getAddress()).append("\n")
                .append(p.getFullName()).append("\n")
                .append("__\n")
                .append("| Продажі/Оборот\n")
                .append("| Дата: ").append(d.getReportDate().format(DATE_FORMAT)).append("\n")
                .append("| Всього покупців: ").append(d.getBuyersTotal()).append("\n")
                .append("| Відвідувачів без покупки: ").append(d.getVisitorsNoBuy()).append("\n")
                .append("| Конверсія: ").append(conversionPct(d)).append("%\n")
                .append("| Повернення: ").append(d.getReturnsCount()).append("\n")
                .append("__\n")
                .append("📊 ЗВІТ ПРО ПРОДАЖ:\n")
                .append("| 💰Всього оборот: ").append(d.getTurnoverUah()).append(" UAH\n")
                .append("| 📃Чеків: ").append(d.getChecksCount()).append("\n")
                .append("| 🚻Покупців: ").append(d.getBuyersTotal()).append("\n")
                .append("| 💰 Середній чек: ").append(avgCheck(d)).append("\n")
                .append("| 🧾 ПЛАН/ФАКТ. ").append(d.getPlanUah()).append("/").append(d.getFactUah()).append("( ").append(planFactPct(d)).append("%)\n")
                .append("| 💳Кредитні картки: ").append(d.getPayCardUah()).append(" UAH\n")
                .append("|  Готівка: ").append(d.getPayCashUah()).append("\n")
                .append("|  Онлайн сайт: ").append(d.getPayOnlineSiteUah()).append(" UAH\n")
                .append("|  Готівка онлайн: ").append(d.getPayCashOnlineUah()).append("\n")
                .append("| 💸Готівка не фіскальна: ").append(d.getPayNonFiscalCashUah()).append(" UAH\n")
                .append("| 🚚Доставка: ").append(d.getDeliveryUah()).append("\n")
                .append("| 📥Поверенная: ").append(d.getVerifiedUah()).append("\n")
                .append("|  Инкассация : ").append(d.getIncasationUah()).append("\n")
                .append("| 📤Вилучення: ").append(d.getWithdrawalUah()).append("\n")
                .append("|  В кассі на кінець дня: ").append(d.getCashEnddayUah()).append(" UAH\n")
                .append("|  Витрати: ").append(d.getExpensesUah()).append("\n")
                .append("__\n")
                .append("👥 ПОКУПЦІ:\n")
                .append("• старих: ").append(d.getBuyersOld()).append("\n")
                .append("• нових: ").append(d.getBuyersNew()).append("\n\n")
                .append("GG “L” Light -").append(b.getGgLLight()).append("\n")
                .append("GG “L” Strong- ").append(b.getGgLStrong()).append("\n")
                .append("OG “L” Light - ").append(b.getOgLLight()).append("\n")
                .append("OG “L” Strong-").append(b.getOgLStrong()).append("\n")
                .append("GG \"L\" super strong -").append(b.getGgLSuperStrong()).append("\n")
                .append("OG \"L\" super strong - ").append(b.getOgLSuperStrong()).append("\n\n")
                .append("GG “XL” Light - ").append(b.getGgXlLight()).append("\n")
                .append("GG “XL” Strong - ").append(b.getGgXlStrong()).append("\n")
                .append("OG “XL” Light - ").append(b.getOgXlLight()).append("\n")
                .append("OG “XL” Strong - ").append(b.getOgXlStrong()).append("\n")
                .append("GG “XL” Super Strong - ").append(b.getGgXlSuperStrong()).append("\n")
                .append("OG “XL” super Strong - ").append(b.getOgXlSuperStrong()).append("\n\n")
                .append("На вагу по 3:\n")
                .append("Ог - ").append(b.getWeightOg()).append("\n")
                .append("Гг - ").append(b.getWeightGg());

        if (withWarning && paymentSum(d) != d.getTurnoverUah()) {
            sb.append("\n\n⚠️ Увага: сума оплат (").append(paymentSum(d)).append(") не дорівнює обороту (").append(d.getTurnoverUah()).append("). Продовжити?");
        }
        return sb.toString();
    }
}
