package com.greenstate.eveningreport.bot;

import com.greenstate.eveningreport.domain.EmployeeProfile;
import com.greenstate.eveningreport.domain.FinalReport;
import com.greenstate.eveningreport.domain.ReportDraft;
import com.greenstate.eveningreport.service.RegistrationService;
import com.greenstate.eveningreport.service.ReportService;
import com.greenstate.eveningreport.storage.DraftRepository;
import com.greenstate.eveningreport.storage.ReportRepository;
import com.greenstate.eveningreport.ui.Keyboards;
import com.greenstate.eveningreport.ui.Messages;
import com.greenstate.eveningreport.ui.WizardEngine;
import com.greenstate.eveningreport.ui.WizardState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UpdateRouter {
    private final RegistrationService registrationService;
    private final DraftRepository draftRepository;
    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final WizardEngine wizardEngine;

    private final Map<Long, WizardState> regState = new HashMap<>();
    private final Map<Long, EmployeeProfile> regDraft = new HashMap<>();

    public UpdateRouter(RegistrationService registrationService, DraftRepository draftRepository,
                        ReportRepository reportRepository, ReportService reportService, WizardEngine wizardEngine) {
        this.registrationService = registrationService;
        this.draftRepository = draftRepository;
        this.reportRepository = reportRepository;
        this.reportService = reportService;
        this.wizardEngine = wizardEngine;
    }

    public SendMessage route(long chatId, long userId, String text) {
        text = text == null ? "" : text.trim();
        if (text.equals("/cancel") || text.equals("❌ Скасувати")) {
            regState.remove(userId);
            regDraft.remove(userId);
            draftRepository.delete(userId);
            return msg(chatId, "Дію скасовано.", true);
        }

        if (isInRegistration(userId)) {
            return handleRegistration(chatId, userId, text);
        }

        if (!registrationService.isRegistered(userId) && !text.equals("/start")) {
            return msg(chatId, Messages.NEED_REG, false);
        }

        if (text.equals("/start")) return start(chatId, userId);
        if (text.equals("/profile") || text.equals("👤 Профіль")) return profile(chatId, userId);
        if (text.equals("/profile_edit")) return startRegistration(chatId, userId, true);
        if (text.startsWith("/report") || text.equals("🧾 Створити звіт")) return startReport(chatId, userId, text);
        if (text.equals("/draft") || text.equals("📌 Продовжити чернетку")) return continueDraft(chatId, userId);
        if (text.equals("/last") || text.equals("📄 Останній звіт")) return lastReport(chatId, userId);

        ReportDraft draft = draftRepository.findByUserId(userId);
        if (draft != null) {
            return handleDraft(chatId, userId, text, draft);
        }

        return msg(chatId, Messages.MENU, true);
    }

    private SendMessage start(long chatId, long userId) {
        if (!registrationService.isRegistered(userId)) {
            return startRegistration(chatId, userId, false);
        }
        return msg(chatId, "Вітаю! " + Messages.MENU, true);
    }

    private SendMessage startRegistration(long chatId, long userId, boolean edit) {
        EmployeeProfile p = new EmployeeProfile();
        p.setTelegramUserId(userId);
        p.setStoreName("Green State");
        if (edit) {
            EmployeeProfile old = registrationService.getProfile(userId);
            if (old != null) p = old;
        }
        regDraft.put(userId, p);
        regState.put(userId, WizardState.REG_FULL_NAME);
        return msg(chatId, "Реєстрація\nКрок 1/5\nВкажіть ПІБ. Приклад: Гончар Павло", false);
    }

    private SendMessage handleRegistration(long chatId, long userId, String text) {
        WizardState state = regState.get(userId);
        EmployeeProfile p = regDraft.get(userId);
        switch (state) {
            case REG_FULL_NAME -> {
                p.setFullName(text);
                regState.put(userId, WizardState.REG_CITY);
                return msg(chatId, "Крок 2/5\nВкажіть місто. Приклад: Бровари", false);
            }
            case REG_CITY -> {
                p.setCity(text);
                regState.put(userId, WizardState.REG_ADDRESS);
                return msg(chatId, "Крок 3/5\nВкажіть адресу. Приклад: Київська 294/1", false);
            }
            case REG_ADDRESS -> {
                p.setAddress(text);
                regState.put(userId, WizardState.REG_STORE_NAME);
                return msg(chatId, "Крок 4/5\nВкажіть бренд/назву магазину (або '-' для Green State)", false);
            }
            case REG_STORE_NAME -> {
                if (!text.equals("-")) p.setStoreName(text);
                regState.put(userId, WizardState.REG_CONFIRM);
                return msg(chatId, "Крок 5/5\nПідтвердіть: \n" + p.getFullName() + "\nм. " + p.getCity() + ", " + p.getAddress() + "\n" + p.getStoreName() + "\n\nНапишіть: так/ні", false);
            }
            case REG_CONFIRM -> {
                if (text.equalsIgnoreCase("так")) {
                    registrationService.saveProfile(p);
                    regState.remove(userId);
                    regDraft.remove(userId);
                    return msg(chatId, "✅ Профіль збережено. " + Messages.MENU, true);
                }
                return msg(chatId, "Введіть 'так' для збереження або /cancel", false);
            }
            default -> {
                return msg(chatId, "Помилка реєстрації. /start", false);
            }
        }
    }

    private SendMessage profile(long chatId, long userId) {
        EmployeeProfile p = registrationService.getProfile(userId);
        if (p == null) return msg(chatId, Messages.NEED_REG, false);
        return msg(chatId, "👤 Ваш профіль:\n" + p.getFullName() + "\nм. " + p.getCity() + ", " + p.getAddress() + "\n" + p.getStoreName() + "\n\nДля редагування: /profile_edit", true);
    }

    private SendMessage startReport(long chatId, long userId, String text) {
        ReportDraft d = new ReportDraft();
        d.setTelegramUserId(userId);
        d.setState(WizardState.REPORT_DATE);
        wizardEngine.initDefaultDate(d);

        String[] parts = text.split("\\s+");
        if (parts.length == 2 && parts[0].equals("/report")) {
            try {
                d.setReportDate(LocalDate.parse(parts[1]));
                d.setState(WizardState.REPORT_BUYERS_TOTAL);
            } catch (Exception ignore) {
            }
        }
        draftRepository.save(d);
        return msg(chatId, wizardEngine.question(d) + "\n\nКоманди: /back, /cancel", false);
    }

    private SendMessage continueDraft(long chatId, long userId) {
        ReportDraft d = draftRepository.findByUserId(userId);
        if (d == null) return msg(chatId, "Чернетку не знайдено. Створіть нову: /report", true);
        if (d.getState() == WizardState.REPORT_CONFIRM) {
            return preview(chatId, userId, d);
        }
        return msg(chatId, "Продовжуємо чернетку:\n" + wizardEngine.question(d) + "\n\nКоманди: /back, /cancel", false);
    }

    private SendMessage handleDraft(long chatId, long userId, String text, ReportDraft d) {
        if (text.equals("/back")) {
            wizardEngine.goBack(d);
            draftRepository.save(d);
            return msg(chatId, wizardEngine.question(d), false);
        }

        if (d.getState() == WizardState.REPORT_CONFIRM) {
            if (text.equals("✅ Підтвердити")) {
                FinalReport fr = new FinalReport();
                fr.setTelegramUserId(userId);
                fr.setData(d);
                fr.setCreatedAt(Instant.now());
                fr.setRenderedText(reportService.render(registrationService.getProfile(userId), d, false));
                reportRepository.save(fr);
                draftRepository.delete(userId);
                return msg(chatId, "✅ Звіт зафіксовано як фінальний.\n\n" + fr.getRenderedText(), true);
            }
            if (text.equals("✏️ Виправити")) {
                wizardEngine.goBack(d);
                draftRepository.save(d);
                return msg(chatId, "Добре, виправляємо.\n" + wizardEngine.question(d), false);
            }
            SendMessage sm = msg(chatId, "Натисніть кнопку підтвердження або скасування.", false);
            sm.setReplyMarkup(Keyboards.confirmKeyboard());
            return sm;
        }

        String error = wizardEngine.applyInput(d, text);
        draftRepository.save(d);
        if (error != null) {
            return msg(chatId, error + "\n" + wizardEngine.question(d), false);
        }

        if (d.getState() == WizardState.REPORT_CONFIRM) {
            return preview(chatId, userId, d);
        }
        return msg(chatId, wizardEngine.question(d) + "\n\nКоманди: /back, /cancel", false);
    }

    private SendMessage preview(long chatId, long userId, ReportDraft d) {
        String rendered = reportService.render(registrationService.getProfile(userId), d, true);
        SendMessage sm = msg(chatId, "Попередній перегляд:\n\n" + rendered, false);
        sm.setReplyMarkup(Keyboards.confirmKeyboard());
        return sm;
    }

    private SendMessage lastReport(long chatId, long userId) {
        FinalReport report = reportRepository.findLastForUser(userId);
        if (report == null) return msg(chatId, "Фінальних звітів поки немає.", true);
        return msg(chatId, report.getRenderedText(), true);
    }

    private boolean isInRegistration(long userId) {
        return regState.containsKey(userId);
    }

    private SendMessage msg(long chatId, String text, boolean menu) {
        SendMessage sm = new SendMessage(String.valueOf(chatId), text);
        if (menu) sm.setReplyMarkup(Keyboards.mainMenu());
        return sm;
    }
}
