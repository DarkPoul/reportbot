package esvar.ua.botreport.report;

import esvar.ua.botreport.session.PaymentData;
import esvar.ua.botreport.session.ReportDraft;
import esvar.ua.botreport.store.Store;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ReportCalculationService {

    private static final ZoneId UA_ZONE = ZoneId.of("Europe/Kyiv");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public String today() {
        return LocalDate.now(UA_ZONE).format(DATE_FORMAT);
    }

    public BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    public BigDecimal averageCheck(BigDecimal turnover, Integer buyers) {
        if (turnover == null || buyers == null || buyers == 0) {
            return BigDecimal.ZERO;
        }
        return turnover.divide(BigDecimal.valueOf(buyers), 2, RoundingMode.HALF_UP);
    }

    public String conversionPercent(Integer buyers, Integer visitorsWithoutPurchase) {
        if (buyers == null || visitorsWithoutPurchase == null) {
            return "0%";
        }
        int totalVisitors = buyers + visitorsWithoutPurchase;
        if (totalVisitors == 0) {
            return "0%";
        }
        BigDecimal percent = BigDecimal.valueOf(buyers)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalVisitors), 1, RoundingMode.HALF_UP);
        return percent + "%";
    }

    public String planPercent(BigDecimal fact, BigDecimal plan) {
        if (plan == null || plan.compareTo(BigDecimal.ZERO) == 0) {
            return "0%";
        }
        BigDecimal percent = safeMoney(fact)
                .multiply(BigDecimal.valueOf(100))
                .divide(plan, 1, RoundingMode.HALF_UP);
        return percent + "%";
    }

    public String formatMoney(BigDecimal value) {
        return String.format("%,.2f", safeMoney(value));
    }

    public String buildReportHeader(ReportDraft draft, Store store) {
        String storeName = store == null ? "Р СњР ВµР Р†РЎвЂ“Р Т‘Р С•Р СР С‘Р в„– Р СР В°Р С–Р В°Р В·Р С‘Р Р…" : store.name();
        String address = store == null ? draft.getLocationKey() : store.address();
        PaymentData payment = draft.getPaymentData();

        return (storeName + "\n" + address).stripTrailing()
                + "\n"
                + draft.getEmployeeName()
                + "\n\n"
                + "СЂСџвЂ™В° Р СџРЎР‚Р С•Р Т‘Р В°Р В¶РЎвЂ“/Р С›Р В±Р С•РЎР‚Р С•РЎвЂљ\n"
                + "| Р вЂќР В°РЎвЂљР В°: " + today() + "\n"
                + "| Р вЂ™РЎРѓРЎРЉР С•Р С–Р С• Р С—Р С•Р С”РЎС“Р С—РЎвЂ РЎвЂ“Р Р†: " + safeInt(draft.getBuyers()) + "\n"
                + "| Р вЂ™РЎвЂ“Р Т‘Р Р†РЎвЂ“Р Т‘РЎС“Р Р†Р В°РЎвЂЎРЎвЂ“Р Р† Р В±Р ВµР В· Р С—Р С•Р С”РЎС“Р С—Р С”Р С‘: " + safeInt(draft.getVisitorsWithoutPurchase()) + "\n"
                + "| Р С™Р С•Р Р…Р Р†Р ВµРЎР‚РЎРѓРЎвЂ“РЎРЏ: " + conversionPercent(draft.getBuyers(), draft.getVisitorsWithoutPurchase()) + "\n\n"
                + "СЂСџвЂњР‰ Р вЂ”Р вЂ™Р вЂ Р Сћ Р СџР В Р С› Р СџР В Р С›Р вЂќР С’Р вЂ“Р вЂ :\n"
                + "| СЂСџвЂ™В° Р вЂ™РЎРѓРЎРЉР С•Р С–Р С• Р С•Р В±Р С•РЎР‚Р С•РЎвЂљ: " + formatMoney(draft.getTurnover()) + " UAH\n"
                + "| СЂСџвЂњС“ Р В§Р ВµР С”РЎвЂ“Р Р†: " + safeInt(draft.getBuyers()) + "\n"
                + "| СЂСџС™В» Р СџР С•Р С”РЎС“Р С—РЎвЂ РЎвЂ“Р Р†: " + safeInt(draft.getBuyers()) + "\n"
                + "| СЂСџвЂ™В° Р РЋР ВµРЎР‚Р ВµР Т‘Р Р…РЎвЂ“Р в„– РЎвЂЎР ВµР С”: " + formatMoney(averageCheck(draft.getTurnover(), draft.getBuyers())) + " UAH\n"
                + "| СЂСџвЂ™С– Р С™РЎР‚Р ВµР Т‘Р С‘РЎвЂљР Р…РЎвЂ“ Р С”Р В°РЎР‚РЎвЂљР С”Р С‘: " + formatMoney(payment.getCard()) + " UAH\n"
                + "| СЂСџвЂ™Вµ Р вЂњР С•РЎвЂљРЎвЂ“Р Р†Р С”Р В°: " + formatMoney(payment.getCash()) + " UAH\n"
                + "| СЂСџРЉС’ Р С›Р Р…Р В»Р В°Р в„–Р Р… РЎРѓР В°Р в„–РЎвЂљ: " + formatMoney(payment.getOnlineCard()) + " UAH\n"
                + "| СЂСџРЉС’ Р вЂњР С•РЎвЂљРЎвЂ“Р Р†Р С”Р В° Р С•Р Р…Р В»Р В°Р в„–Р Р…: " + formatMoney(payment.getOnlineCash()) + " UAH\n"
                + "| СЂСџвЂ™С‘ Р вЂњР С•РЎвЂљРЎвЂ“Р Р†Р С”Р В° Р Р…Р Вµ РЎвЂћРЎвЂ“РЎРѓР С”Р В°Р В»РЎРЉР Р…Р В°: " + formatMoney(payment.getCashFiscal()) + " UAH\n"
                + "| СЂСџС™С™ Р вЂќР С•РЎРѓРЎвЂљР В°Р Р†Р С”Р В°: " + formatMoney(draft.getDeliveryAmount()) + " UAH\n"
                + "| СЂСџРЏВ¦ Р вЂ Р Р…Р С”Р В°РЎРѓР В°РЎвЂ РЎвЂ“РЎРЏ: " + formatMoney(draft.getCollectionAmount()) + " UAH\n"
                + "| СЂСџВ§С• Р вЂ™ Р С”Р В°РЎРѓРЎвЂ“ Р Р…Р В° Р С”РЎвЂ“Р Р…Р ВµРЎвЂ РЎРЉ Р Т‘Р Р…РЎРЏ: " + formatMoney(draft.getCashInRegister()) + " UAH\n"
                + "| СЂСџВ§С• Р вЂ™Р С‘РЎвЂљРЎР‚Р В°РЎвЂљР С‘: " + formatMoney(draft.getExpenses()) + " UAH\n";
    }
}
