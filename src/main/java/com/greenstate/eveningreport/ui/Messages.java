package com.greenstate.eveningreport.ui;

public class Messages {
    public static final String MENU = "Оберіть дію з меню нижче 👇";
    public static final String NEED_REG = "Спочатку потрібно зареєструватися. Напишіть /start";

    public static String progress(int current, int total, String question) {
        return "Крок " + current + "/" + total + "\n" + question;
    }
}
