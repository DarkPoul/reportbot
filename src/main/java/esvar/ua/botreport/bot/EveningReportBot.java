package esvar.ua.botreport.bot;

import esvar.ua.botreport.config.BotProperties;
import esvar.ua.botreport.telegram.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class EveningReportBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotProperties properties;
    private final TelegramUpdateRouter updateRouter;
    private final MessageSender messageSender;

    public EveningReportBot(BotProperties properties,
                            TelegramUpdateRouter updateRouter,
                            MessageSender messageSender) {
        this.properties = properties;
        this.updateRouter = updateRouter;
        this.messageSender = messageSender;
    }

    @Override
    public String getBotToken() {
        return properties.token();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        updateRouter.route(update);
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("bot_registered running={}", botSession.isRunning());
        if (properties.adminChatId() != null) {
            messageSender.sendText(properties.adminChatId().toString(),
                    "Bot started (registered). running=" + botSession.isRunning());
        }
    }
}
