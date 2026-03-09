package esvar.ua.botreport.bot;

import esvar.ua.botreport.flow.ReportFlowService;
import esvar.ua.botreport.telegram.BotMessages;
import esvar.ua.botreport.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Component
public class TelegramUpdateRouter {

    private final ReportFlowService reportFlowService;
    private final MessageSender messageSender;

    public TelegramUpdateRouter(ReportFlowService reportFlowService, MessageSender messageSender) {
        this.reportFlowService = reportFlowService;
        this.messageSender = messageSender;
    }

    public void route(Update update) {
        if (update == null) {
            return;
        }

        if (!update.hasMessage()) {
            log.info("unsupported_update_type type=no_message");
            return;
        }

        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());

        if (!message.hasText()) {
            log.info("unsupported_update_type type=non_text chatId={}", chatId);
            messageSender.sendText(chatId, BotMessages.UNSUPPORTED_MESSAGE);
            return;
        }

        if (message.getFrom() == null) {
            log.warn("message_without_user chatId={}", chatId);
            messageSender.sendText(chatId, BotMessages.UNEXPECTED_ERROR);
            return;
        }

        long userId = message.getFrom().getId();
        String text = message.getText().trim();

        if ("/start".equals(text)) {
            reportFlowService.start(userId, chatId);
            return;
        }
        if ("/cancel".equals(text)) {
            reportFlowService.cancel(userId, chatId);
            return;
        }

        reportFlowService.handleText(userId, chatId, text);
    }
}
