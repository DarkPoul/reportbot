package com.greenstate.eveningreport.bot;

import com.greenstate.eveningreport.service.RegistrationService;
import com.greenstate.eveningreport.service.ReportService;
import com.greenstate.eveningreport.storage.DraftRepository;
import com.greenstate.eveningreport.storage.JsonStorage;
import com.greenstate.eveningreport.storage.ReportRepository;
import com.greenstate.eveningreport.storage.UserRepository;
import com.greenstate.eveningreport.ui.WizardEngine;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        String token = System.getenv("BOT_TOKEN");
        String username = System.getenv("BOT_USERNAME");
        if (token == null || token.isBlank() || username == null || username.isBlank()) {
            throw new IllegalStateException("Задайте змінні BOT_TOKEN і BOT_USERNAME");
        }

        JsonStorage storage = new JsonStorage();
        Path dataDir = Path.of("data");
        UserRepository userRepository = new UserRepository(dataDir, storage);
        DraftRepository draftRepository = new DraftRepository(dataDir, storage);
        ReportRepository reportRepository = new ReportRepository(dataDir, storage);

        UpdateRouter router = new UpdateRouter(
                new RegistrationService(userRepository),
                draftRepository,
                reportRepository,
                new ReportService(),
                new WizardEngine()
        );

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(new EveningReportBot(token, username, router));
        System.out.println("evening_report_bot запущено.");
    }
}
