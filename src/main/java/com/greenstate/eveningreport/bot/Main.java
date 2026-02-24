package com.greenstate.eveningreport.bot;

import com.greenstate.eveningreport.service.RegistrationService;
import com.greenstate.eveningreport.service.ReportService;
import com.greenstate.eveningreport.storage.JsonStorage;
import com.greenstate.eveningreport.storage.repositories.DraftRepository;
import com.greenstate.eveningreport.storage.repositories.ReportRepository;
import com.greenstate.eveningreport.storage.repositories.UserRepository;
import com.greenstate.eveningreport.ui.WizardEngine;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        String token = requireEnv("BOT_TOKEN");
        String username = requireEnv("BOT_USERNAME");

        JsonStorage storage = new JsonStorage(Paths.get("data"));
        UserRepository userRepository = new UserRepository(storage);
        DraftRepository draftRepository = new DraftRepository(storage);
        ReportRepository reportRepository = new ReportRepository(storage);
        RegistrationService registrationService = new RegistrationService(userRepository);
        ReportService reportService = new ReportService();
        WizardEngine wizardEngine = new WizardEngine(draftRepository, reportRepository, registrationService, reportService);
        UpdateRouter router = new UpdateRouter(registrationService, wizardEngine, reportRepository, draftRepository);

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new EveningReportBot(token, username, router));
        System.out.println("evening_report_bot запущено");
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Відсутня змінна середовища: " + name);
        }
        return value;
    }
}
