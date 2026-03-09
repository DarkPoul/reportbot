package esvar.ua.botreport.report;

import java.math.BigDecimal;

public record ReportSaveRequest(
        String date,
        String key,
        String employeeName,
        int totalCustomers,
        int visitorsWithoutPurchase,
        String conversionPercent,
        int newCustomers,
        BigDecimal turnover,
        BigDecimal cashNonFiscal,
        BigDecimal cashFiscal,
        BigDecimal creditCards,
        BigDecimal cashOnline,
        BigDecimal cardOnline,
        BigDecimal delivery,
        BigDecimal collection,
        BigDecimal cashAtEndOfDay,
        BigDecimal expenses,
        String tomorrowOnShift
) {
}
