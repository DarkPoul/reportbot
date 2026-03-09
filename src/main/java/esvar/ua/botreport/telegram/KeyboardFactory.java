package esvar.ua.botreport.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactory {

    public ReplyKeyboard buildLocationKeyboard(List<String> locationKeys) {
        List<KeyboardRow> rows = new ArrayList<>();
        for (String key : locationKeys) {
            KeyboardRow row = new KeyboardRow();
            row.add(key);
            rows.add(row);
        }
        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }

    public ReplyKeyboard buildPaymentKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(BotMessages.PAYMENT_CASH);
        row1.add(BotMessages.PAYMENT_CASH_FISCAL);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(BotMessages.PAYMENT_CARD);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(BotMessages.PAYMENT_ONLINE_CARD);
        row3.add(BotMessages.PAYMENT_ONLINE_CASH);

        KeyboardRow row4 = new KeyboardRow();
        row4.add(BotMessages.PAYMENT_CONTINUE);

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3, row4))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }

    public ReplyKeyboard removeKeyboard() {
        return new ReplyKeyboardRemove(true);
    }
}
