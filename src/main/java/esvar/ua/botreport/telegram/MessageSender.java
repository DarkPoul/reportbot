package esvar.ua.botreport.telegram;

import esvar.ua.botreport.config.BotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Component
public class MessageSender {

    private final OkHttpTelegramClient telegramClient;
    private final Method executeMethod;

    public MessageSender(BotProperties props) {
        if (props.token() == null || props.token().isBlank()) {
            throw new IllegalStateException("bot.telegram.token is empty");
        }
        this.telegramClient = new OkHttpTelegramClient(props.token());
        this.executeMethod = resolveExecuteMethod();
    }

    public void sendText(String chatId, String text) {
        send(chatId, text, null);
    }

    public void sendWithKeyboard(String chatId, String text, ReplyKeyboard keyboard) {
        send(chatId, text, keyboard);
    }

    public void sendAndRemoveKeyboard(String chatId, String text, ReplyKeyboard keyboardToRemove) {
        send(chatId, text, keyboardToRemove);
    }

    private void send(String chatId, String text, ReplyKeyboard keyboard) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();
            executeMethod.invoke(telegramClient, message);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Throwable cause = ex instanceof InvocationTargetException ? ex.getCause() : ex;
            log.warn("telegram_send_failed chatId={}", chatId, cause);
        }
    }

    private Method resolveExecuteMethod() {
        try {
            Class<?> parameterType = Class.forName("org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage");
            return telegramClient.getClass().getMethod("execute", parameterType);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Cannot resolve Telegram execute method for SendMessage", ex);
        }
    }
}
