package com.greenstate.eveningreport.bot;

import com.greenstate.eveningreport.domain.EmployeeProfile;
import com.greenstate.eveningreport.domain.ReportDraft;
import com.greenstate.eveningreport.service.RegistrationService;
import com.greenstate.eveningreport.storage.repositories.DraftRepository;
import com.greenstate.eveningreport.storage.repositories.ReportRepository;
import com.greenstate.eveningreport.ui.Messages;
import com.greenstate.eveningreport.ui.WizardEngine;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

public class UpdateRouter {
    private final RegistrationService registrationService;
    private final WizardEngine wizardEngine;
    private final ReportRepository reportRepository;
    private final DraftRepository draftRepository;

    public UpdateRouter(RegistrationService registrationService, WizardEngine wizardEngine, ReportRepository reportRepository, DraftRepository draftRepository) {
        this.registrationService = registrationService;
        this.wizardEngine = wizardEngine;
        this.reportRepository = reportRepository;
        this.draftRepository = draftRepository;
    }

    public Optional<ReportDraft> currentDraft(Long userId) { return wizardEngine.getDraft(userId); }

    public String route(Long userId, String text) {
        if ("/cancel".equals(text)) {
            draftRepository.delete(userId);
            return Messages.CANCELLED;
        }
        Optional<ReportDraft> activeDraft = wizardEngine.getDraft(userId);
        if (activeDraft.isPresent() && (!text.startsWith("/") || "/back".equals(text))) {
            return wizardEngine.processInput(userId, text);
        }

        return switch (normalizeCommand(text)) {
            case "/start" -> onStart(userId);
            case "/profile" -> profile(userId);
            case "/profile_edit" -> {
                wizardEngine.startRegistration(userId, true);
                yield "Редагування профілю.\n" + wizardEngine.prompt(wizardEngine.getDraft(userId).orElseThrow());
            }
            case "/report" -> startReport(userId, text);
            case "/draft" -> activeDraft.map(wizardEngine::prompt).orElse("Чернетка відсутня.");
            case "/last" -> reportRepository.findLastByUser(userId).map(r -> "Останній фінальний звіт:\n\n" + r.getFormattedText()).orElse("Поки що немає фінального звіту.");
            default -> handleMenuAliases(userId, text);
        };
    }

    private String onStart(Long userId) {
        if (registrationService.findProfile(userId).isEmpty()) {
            wizardEngine.startRegistration(userId, false);
            return Messages.NEED_REGISTRATION;
        }
        return "Вітаю! Оберіть дію з меню нижче.";
    }

    private String profile(Long userId) {
        Optional<EmployeeProfile> p = registrationService.findProfile(userId);
        if (p.isEmpty()) return "Профіль не знайдено. Запустіть /start";
        EmployeeProfile profile = p.get();
        return String.format("👤 Профіль:\n%s\nм. %s, %s\nБренд: %s", profile.getFullName(), profile.getCity(), profile.getAddress(), profile.getBrandName());
    }

    private String startReport(Long userId, String commandText) {
        if (registrationService.findProfile(userId).isEmpty()) {
            wizardEngine.startRegistration(userId, false);
            return Messages.NEED_REGISTRATION;
        }
        String[] parts = commandText.split("\\s+");
        LocalDate date = LocalDate.now(ZoneId.of("Europe/Kyiv"));
        if (parts.length > 1) {
            try {
                date = LocalDate.parse(parts[1]);
            } catch (Exception e) {
                return "Невірна дата для /report. Використайте формат YYYY-MM-DD.";
            }
        }
        wizardEngine.startReport(userId, date);
        return "Створюю нову чернетку звіту.\n" + wizardEngine.prompt(wizardEngine.getDraft(userId).orElseThrow());
    }

    private String handleMenuAliases(Long userId, String text) {
        return switch (text) {
            case "🧾 Створити звіт" -> startReport(userId, "/report");
            case "📌 Продовжити чернетку" -> wizardEngine.getDraft(userId).map(wizardEngine::prompt).orElse("Чернетка відсутня.");
            case "📄 Останній звіт" -> route(userId, "/last");
            case "👤 Профіль" -> route(userId, "/profile");
            default -> "Невідома команда. Доступно: /start /report /draft /last /profile /profile_edit";
        };
    }

    private String normalizeCommand(String text) {
        return text.trim().split("\\s+")[0];
    }
}
