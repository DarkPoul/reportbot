package com.greenstate.eveningreport.ui;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class Keyboards {
    public static ReplyKeyboard mainMenu() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(
                row("🧾 Створити звіт", "📌 Продовжити чернетку"),
                row("📄 Останній звіт", "👤 Профіль")
        ));
        return markup;
    }

    public static ReplyKeyboard confirmKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(
                row("✅ Підтвердити", "✏️ Виправити", "❌ Скасувати")
        ));
        return markup;
    }

    private static KeyboardRow row(String... buttons) {
        KeyboardRow row = new KeyboardRow();
        for (String b : buttons) row.add(b);
        return row;
    }
}
