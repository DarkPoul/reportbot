package com.greenstate.eveningreport.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class EveningReportBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final UpdateRouter router;

    public EveningReportBot(String botToken, String botUsername, UpdateRouter router) {
        super(botToken);
        this.botUsername = botUsername;
        this.router = router;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (!update.hasMessage() || !update.getMessage().hasText()) return;
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();
            String text = update.getMessage().getText();
            execute(router.route(chatId, userId, text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
