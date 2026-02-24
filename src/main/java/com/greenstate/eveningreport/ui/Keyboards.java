package com.greenstate.eveningreport.ui;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class Keyboards {
    public static ReplyKeyboard mainMenu() {
        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        kb.setResizeKeyboard(true);
        kb.setKeyboard(List.of(
                row("🧾 Створити звіт", "📌 Продовжити чернетку"),
                row("📄 Останній звіт", "👤 Профіль")
        ));
        return kb;
    }

    public static ReplyKeyboard confirmKeyboard() {
        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        kb.setResizeKeyboard(true);
        kb.setKeyboard(List.of(
                row("✅ Підтвердити", "✏️ Виправити", "❌ Скасувати")
        ));
        return kb;
    }

    private static KeyboardRow row(String... values) {
        KeyboardRow r = new KeyboardRow();
        for (String v : values) r.add(v);
        return r;
    }
}
