package com.greenstate.eveningreport.ui;

import com.greenstate.eveningreport.domain.*;
import com.greenstate.eveningreport.service.RegistrationService;
import com.greenstate.eveningreport.service.ReportService;
import com.greenstate.eveningreport.storage.repositories.DraftRepository;
import com.greenstate.eveningreport.storage.repositories.ReportRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class WizardEngine {
    private final DraftRepository draftRepository;
    private final ReportRepository reportRepository;
    private final RegistrationService registrationService;
    private final ReportService reportService;
    private static final DateTimeFormatter DATE_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public WizardEngine(DraftRepository draftRepository, ReportRepository reportRepository, RegistrationService registrationService, ReportService reportService) {
        this.draftRepository = draftRepository;
        this.reportRepository = reportRepository;
        this.registrationService = registrationService;
        this.reportService = reportService;
    }

    public Optional<ReportDraft> getDraft(Long userId) { return draftRepository.findByTelegramUserId(userId); }

    public ReportDraft startRegistration(Long userId, boolean editMode) {
        ReportDraft d = new ReportDraft();
        d.setTelegramUserId(userId);
        d.setWizardType(editMode ? WizardType.PROFILE_EDIT : WizardType.REGISTRATION);
        d.setCurrentStep(WizardState.REG_FULL_NAME);
        registrationService.findProfile(userId).ifPresent(p -> {
            d.setRegFullName(p.getFullName());
            d.setRegCity(p.getCity());
            d.setRegAddress(p.getAddress());
            d.setRegBrandName(p.getBrandName());
        });
        draftRepository.save(d);
        return d;
    }

    public ReportDraft startReport(Long userId, LocalDate date) {
        ReportDraft d = new ReportDraft();
        d.setTelegramUserId(userId);
        d.setWizardType(WizardType.REPORT);
        d.setCurrentStep(WizardState.REPORT_DATE);
        d.setDate(date);
        draftRepository.save(d);
        return d;
    }

    public String processInput(Long userId, String text) {
        ReportDraft d = draftRepository.findByTelegramUserId(userId).orElse(null);
        if (d == null) return "Немає активної чернетки. Натисніть «🧾 Створити звіт» або /start.";

        if ("/back".equals(text)) {
            moveBack(d);
            draftRepository.save(d);
            return prompt(d);
        }
        if (d.getWizardType() == WizardType.REGISTRATION || d.getWizardType() == WizardType.PROFILE_EDIT) {
            return processRegistration(d, text);
        }
        return processReport(d, text);
    }

    private String processRegistration(ReportDraft d, String text) {
        switch (d.getCurrentStep()) {
            case REG_FULL_NAME -> { d.setRegFullName(text); d.setCurrentStep(WizardState.REG_CITY); }
            case REG_CITY -> { d.setRegCity(text); d.setCurrentStep(WizardState.REG_ADDRESS); }
            case REG_ADDRESS -> { d.setRegAddress(text); d.setCurrentStep(WizardState.REG_BRAND); }
            case REG_BRAND -> {
                d.setRegBrandName(text.isBlank() ? "Green State" : text);
                d.setCurrentStep(WizardState.REG_CONFIRM);
                draftRepository.save(d);
                return "Підтвердіть профіль:\n" + profileSummary(d) + "\n\nНапишіть: ТАК або НІ";
            }
            case REG_CONFIRM -> {
                if ("ТАК".equalsIgnoreCase(text.trim())) {
                    EmployeeProfile p = new EmployeeProfile(d.getTelegramUserId(), d.getRegFullName(), d.getRegCity(), d.getRegAddress(),
                            (d.getRegBrandName() == null || d.getRegBrandName().isBlank()) ? "Green State" : d.getRegBrandName());
                    registrationService.save(p);
                    draftRepository.delete(d.getTelegramUserId());
                    return "✅ Профіль збережено! Тепер можна створювати вечірній звіт.";
                }
                d.setCurrentStep(WizardState.REG_FULL_NAME);
                draftRepository.save(d);
                return "Ок, почнемо наново.\n" + prompt(d);
            }
            default -> { }
        }
        draftRepository.save(d);
        return prompt(d);
    }

    private String processReport(ReportDraft d, String text) {
        if (d.getCurrentStep() == WizardState.REPORT_CONFIRM) {
            return handleReportConfirm(d, text);
        }
        try {
            switch (d.getCurrentStep()) {
                case REPORT_DATE -> d.setDate(parseDate(text));
                case BUYERS_TOTAL -> d.setBuyersTotal(parseInt(text));
                case VISITORS_NO_BUY -> d.setVisitorsNoBuy(parseInt(text));
                case RETURNS_COUNT -> d.setReturnsCount(parseInt(text));
                case TURNOVER_UAH -> d.setTurnoverUah(parseInt(text));
                case CHECKS_COUNT -> d.setChecksCount(parseInt(text));
                case PLAN_UAH -> d.setPlanUah(parseInt(text));
                case FACT_UAH -> d.setFactUah(parseInt(text));
                case PAY_CARD_UAH -> d.setPayCardUah(parseInt(text));
                case PAY_CASH_UAH -> d.setPayCashUah(parseInt(text));
                case PAY_ONLINE_SITE_UAH -> d.setPayOnlineSiteUah(parseInt(text));
                case PAY_CASH_ONLINE_UAH -> d.setPayCashOnlineUah(parseInt(text));
                case PAY_NON_FISCAL_CASH_UAH -> d.setPayNonFiscalCashUah(parseInt(text));
                case DELIVERY_UAH -> d.setDeliveryUah(parseInt(text));
                case VERIFIED_UAH -> d.setVerifiedUah(parseInt(text));
                case INCASATION_UAH -> d.setIncasationUah(parseInt(text));
                case WITHDRAWAL_UAH -> d.setWithdrawalUah(parseInt(text));
                case CASH_ENDDAY_UAH -> d.setCashEnddayUah(parseInt(text));
                case EXPENSES_UAH -> d.setExpensesUah(parseInt(text));
                case BUYERS_OLD -> d.setBuyersOld(parseInt(text));
                case BUYERS_NEW -> d.setBuyersNew(parseInt(text));
                case GG_L_LIGHT -> d.getProductBreakdown().setGgLLight(parseInt(text));
                case GG_L_STRONG -> d.getProductBreakdown().setGgLStrong(parseInt(text));
                case OG_L_LIGHT -> d.getProductBreakdown().setOgLLight(parseInt(text));
                case OG_L_STRONG -> d.getProductBreakdown().setOgLStrong(parseInt(text));
                case GG_L_SUPER_STRONG -> d.getProductBreakdown().setGgLSuperStrong(parseInt(text));
                case OG_L_SUPER_STRONG -> d.getProductBreakdown().setOgLSuperStrong(parseInt(text));
                case GG_XL_LIGHT -> d.getProductBreakdown().setGgXlLight(parseInt(text));
                case GG_XL_STRONG -> d.getProductBreakdown().setGgXlStrong(parseInt(text));
                case OG_XL_LIGHT -> d.getProductBreakdown().setOgXlLight(parseInt(text));
                case OG_XL_STRONG -> d.getProductBreakdown().setOgXlStrong(parseInt(text));
                case GG_XL_SUPER_STRONG -> d.getProductBreakdown().setGgXlSuperStrong(parseInt(text));
                case OG_XL_SUPER_STRONG -> d.getProductBreakdown().setOgXlSuperStrong(parseInt(text));
                case WEIGHT_OG -> d.getProductBreakdown().setWeightOg(parseInt(text));
                case WEIGHT_GG -> d.getProductBreakdown().setWeightGg(parseInt(text));
                default -> { }
            }
        } catch (DateTimeParseException e) {
            return Messages.INVALID_DATE;
        } catch (NumberFormatException e) {
            return Messages.INVALID_INT;
        }
        d.setCurrentStep(nextState(d.getCurrentStep()));
        draftRepository.save(d);

        if (d.getCurrentStep() == WizardState.REPORT_CONFIRM) {
            return preview(d);
        }
        return prompt(d);
    }

    private String handleReportConfirm(ReportDraft d, String text) {
        if ("✅ Підтвердити".equals(text)) {
            EmployeeProfile profile = registrationService.findProfile(d.getTelegramUserId()).orElseThrow();
            String reportText = reportService.formatReport(profile, d);
            FinalReport finalReport = new FinalReport();
            finalReport.setTelegramUserId(d.getTelegramUserId());
            finalReport.setReportDate(d.getDate());
            finalReport.setFinalizedAt(LocalDateTime.now(ZoneId.of("Europe/Kyiv")));
            finalReport.setSnapshot(d);
            finalReport.setFormattedText(reportText);
            reportRepository.save(finalReport);
            draftRepository.delete(d.getTelegramUserId());
            return "✅ Звіт зафіксовано.\n\n" + reportText;
        }
        if ("✏️ Виправити".equals(text)) {
            d.setCurrentStep(WizardState.BUYERS_TOTAL);
            draftRepository.save(d);
            return "Повертаю до редагування.\n" + prompt(d);
        }
        draftRepository.delete(d.getTelegramUserId());
        return Messages.CANCELLED;
    }

    public String prompt(ReportDraft d) {
        List<WizardState> order = stepOrder(d.getWizardType());
        int idx = Math.max(0, order.indexOf(d.getCurrentStep()));
        String progress = String.format("Крок %d/%d\n", idx + 1, order.size());
        return progress + switch (d.getCurrentStep()) {
            case REG_FULL_NAME -> "Введіть ПІБ (приклад: Гончар Павло).";
            case REG_CITY -> "Вкажіть місто (приклад: Бровари).";
            case REG_ADDRESS -> "Вкажіть адресу (приклад: Київська 294/1).";
            case REG_BRAND -> "Вкажіть бренд/назву магазину (Enter = Green State).";
            case REG_CONFIRM -> "Підтвердіть: ТАК або НІ.";
            case REPORT_DATE -> "Дата звіту (dd.MM.yyyy або yyyy-MM-dd).";
            default -> "Вкажіть " + d.getCurrentStep().name() + " (ціле число).";
        };
    }

    private String preview(ReportDraft d) {
        EmployeeProfile profile = registrationService.findProfile(d.getTelegramUserId()).orElseThrow();
        StringBuilder sb = new StringBuilder("Перевірте звіт перед збереженням:\n\n");
        sb.append(reportService.formatReport(profile, d));
        int payments = reportService.paymentsSum(d);
        if (payments != d.getTurnoverUah()) {
            sb.append("\n⚠️ Увага: сума оплат (").append(payments).append(") не дорівнює обороту (").append(d.getTurnoverUah()).append("). Продовжити?");
        }
        sb.append("\n\nОберіть дію: ✅ Підтвердити / ✏️ Виправити / ❌ Скасувати");
        return sb.toString();
    }

    private void moveBack(ReportDraft d) {
        List<WizardState> order = stepOrder(d.getWizardType());
        int idx = order.indexOf(d.getCurrentStep());
        if (idx > 0) d.setCurrentStep(order.get(idx - 1));
    }

    private List<WizardState> stepOrder(WizardType type) {
        if (type == WizardType.REGISTRATION || type == WizardType.PROFILE_EDIT) {
            return List.of(WizardState.REG_FULL_NAME, WizardState.REG_CITY, WizardState.REG_ADDRESS, WizardState.REG_BRAND, WizardState.REG_CONFIRM);
        }
        return Arrays.asList(WizardState.REPORT_DATE, WizardState.BUYERS_TOTAL, WizardState.VISITORS_NO_BUY, WizardState.RETURNS_COUNT,
                WizardState.TURNOVER_UAH, WizardState.CHECKS_COUNT, WizardState.PLAN_UAH, WizardState.FACT_UAH,
                WizardState.PAY_CARD_UAH, WizardState.PAY_CASH_UAH, WizardState.PAY_ONLINE_SITE_UAH, WizardState.PAY_CASH_ONLINE_UAH,
                WizardState.PAY_NON_FISCAL_CASH_UAH, WizardState.DELIVERY_UAH, WizardState.VERIFIED_UAH, WizardState.INCASATION_UAH,
                WizardState.WITHDRAWAL_UAH, WizardState.CASH_ENDDAY_UAH, WizardState.EXPENSES_UAH, WizardState.BUYERS_OLD,
                WizardState.BUYERS_NEW, WizardState.GG_L_LIGHT, WizardState.GG_L_STRONG, WizardState.OG_L_LIGHT,
                WizardState.OG_L_STRONG, WizardState.GG_L_SUPER_STRONG, WizardState.OG_L_SUPER_STRONG,
                WizardState.GG_XL_LIGHT, WizardState.GG_XL_STRONG, WizardState.OG_XL_LIGHT, WizardState.OG_XL_STRONG,
                WizardState.GG_XL_SUPER_STRONG, WizardState.OG_XL_SUPER_STRONG, WizardState.WEIGHT_OG, WizardState.WEIGHT_GG,
                WizardState.REPORT_CONFIRM);
    }

    private WizardState nextState(WizardState current) {
        List<WizardState> order = stepOrder(WizardType.REPORT);
        int idx = order.indexOf(current);
        if (idx < 0 || idx == order.size() - 1) return WizardState.REPORT_CONFIRM;
        return order.get(idx + 1);
    }

    private int parseInt(String text) {
        return Integer.parseInt(text.trim());
    }

    private LocalDate parseDate(String text) {
        String t = text.trim();
        try {
            return LocalDate.parse(t, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            return LocalDate.parse(t, DATE_ISO);
        }
    }

    private String profileSummary(ReportDraft d) {
        String brand = (d.getRegBrandName() == null || d.getRegBrandName().isBlank()) ? "Green State" : d.getRegBrandName();
        return String.format("%s\nм. %s, %s\nБренд: %s", d.getRegFullName(), d.getRegCity(), d.getRegAddress(), brand);
    }
}
