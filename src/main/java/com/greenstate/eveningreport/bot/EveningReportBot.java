package com.greenstate.eveningreport.bot;

import com.greenstate.eveningreport.ui.Keyboards;
import com.greenstate.eveningreport.ui.WizardState;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class EveningReportBot extends TelegramLongPollingBot {
    private final String botToken;
    private final String botUsername;
    private final UpdateRouter router;

    public EveningReportBot(String botToken, String botUsername, UpdateRouter router) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.router = router;
    }

    @Override
    public String getBotUsername() { return botUsername; }

    @Override
    public String getBotToken() { return botToken; }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        String response = router.route(userId, update.getMessage().getText());
        SendMessage message = new SendMessage(chatId.toString(), response);
        message.setReplyMarkup(selectKeyboard(userId));
        try { execute(message); } catch (Exception e) { e.printStackTrace(); }
    }

    private ReplyKeyboard selectKeyboard(Long userId) {
        return router.currentDraft(userId)
                .filter(d -> d.getCurrentStep() == WizardState.REPORT_CONFIRM)
                .map(d -> Keyboards.confirmKeyboard())
                .orElse(Keyboards.mainMenu());
    }
}
