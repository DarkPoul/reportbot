package esvar.ua.botreport.bot;

import esvar.ua.botreport.config.BotProperties;
import esvar.ua.botreport.session.SessionService;
import esvar.ua.botreport.session.UserSession;
import esvar.ua.botreport.sheets.GoogleSheetsReportRepository;
import esvar.ua.botreport.store.ShopSheetsRepository;
import esvar.ua.botreport.store.Store;
import esvar.ua.botreport.store.StoreCatalog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
public class EveningReportBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotProperties props;
    private final TelegramClient telegramClient;
    private final SessionService sessionService;
    private final GoogleSheetsReportRepository sheetsRepo;
    private final StoreCatalog storeCatalog;
    private final ShopSheetsRepository shopSheetsRepository;
    public static final BigDecimal DAILY_PLAN = BigDecimal.valueOf(533000);
    private static final ZoneId UA_ZONE = ZoneId.of("Europe/Kyiv");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");



    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public EveningReportBot(BotProperties props, SessionService sessionService, GoogleSheetsReportRepository sheetsRepo, StoreCatalog storeCatalog, ShopSheetsRepository shopSheetsRepository) {
        this.props = props;
        this.sessionService = sessionService;
        this.sheetsRepo = sheetsRepo;
        this.storeCatalog = storeCatalog;
        this.shopSheetsRepository = shopSheetsRepository;

        if (props.token() == null || props.token().isBlank()) {
            throw new IllegalStateException("bot.telegram.token is empty");
        }

        this.telegramClient = new OkHttpTelegramClient(props.token());
    }

    // Spring starter бере токен звідси
    @Override
    public String getBotToken() {
        return props.token();
    }

    // Spring starter бере consumer звідси
    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    // Сюди прилітають апдейти
    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        var message = update.getMessage();
        var text = message.getText().trim();

        long userId = message.getFrom().getId();
        var chatId = String.valueOf(message.getChatId());

        UserSession session = sessionService.getOrCreate(userId);

        try {
            // /start — вибір локації
            if ("/start".equals(text)) {
                session.setStep(UserSession.Step.WAIT_LOCATION);
                session.setLocation(null);
                session.setFullName(null);
                session.setTurnover(null);
                session.setChecks(null);
                session.setNewClients(null);
                session.setOldClients(null);
                session.setCash(null);

                sendChooseLocation(chatId);
                return;
            }

            // WAIT_LOCATION
            if (session.getStep() == UserSession.Step.WAIT_LOCATION) {

                Store selectedStore = storeCatalog.getByKey(text);
                if (selectedStore == null) {
                    sendText(chatId, "Будь ласка, оберіть локацію кнопкою з меню нижче.");
                    sendChooseLocation(chatId);
                    return;
                }

                session.setLocation(selectedStore.key());
                session.setStep(UserSession.Step.WAIT_NAME);

                telegramClient.execute(
                        SendMessage.builder()
                                .chatId(chatId)
                                .text("✅ Локація: " + selectedStore.key() + "\n"
                                        + selectedStore.name() + "\n"
                                        + selectedStore.address() + "\n\n"
                                        + "Введіть ваше ім’я прізвище:")
                                .replyMarkup(new ReplyKeyboardRemove(true))
                                .build()
                );
                return;
            }

            // WAIT_NAME
            if (session.getStep() == UserSession.Step.WAIT_NAME) {
                if (text.length() < 3) {
                    sendText(chatId, "Напишіть, будь ласка, ім’я та прізвище (наприклад: Павло Гончар).");
                    return;
                }

                session.setFullName(text);
                session.setStep(UserSession.Step.WAIT_TURNOVER);

                sendText(chatId, "Введіть оборот за сьогодні (UAH), наприклад: 8485 або 8485.50");
                return;
            }

            // WAIT_TURNOVER
            if (session.getStep() == UserSession.Step.WAIT_TURNOVER) {
                Optional<BigDecimal> turnover = parseMoney(text);
                if (turnover.isEmpty()) {
                    sendText(chatId, "Не схоже на число. Введіть оборот, наприклад: 8485 або 8485.50");
                    return;
                }
                session.setTurnover(turnover.get());
                session.setStep(UserSession.Step.WAIT_CHECKS);

                sendText(chatId, "Введіть кількість чеків за сьогодні (ціле число), наприклад: 16");
                return;
            }

            // WAIT_CHECKS
            if (session.getStep() == UserSession.Step.WAIT_CHECKS) {
                Optional<Integer> checks = parsePositiveInt(text);
                if (checks.isEmpty()) {
                    sendText(chatId, "Введіть кількість чеків цілим числом, наприклад: 16");
                    return;
                }
                session.setChecks(checks.get());
                session.setStep(UserSession.Step.WAIT_NEW_CLIENTS);

                sendText(chatId, "Введіть к-сть НОВИХ клієнтів, наприклад: 3");
                return;
            }

            // WAIT_NEW_CLIENTS
            if (session.getStep() == UserSession.Step.WAIT_NEW_CLIENTS) {
                Optional<Integer> val = parseNonNegativeInt(text);
                if (val.isEmpty()) {
                    sendText(chatId, "Введіть к-сть нових клієнтів цілим числом (0 або більше), наприклад: 3");
                    return;
                }
                session.setNewClients(val.get());
                session.setStep(UserSession.Step.WAIT_NO_CLIENTS);

                sendText(chatId, "Введіть кількість клієнтів без покупки");
                return;
            }

            // WAIT_OLD_CLIENTS
    //            if (session.getStep() == UserSession.Step.WAIT_OLD_CLIENTS) {
    //                Optional<Integer> val = parseNonNegativeInt(text);
    //                if (val.isEmpty()) {
    //                    sendText(chatId, "Введіть к-сть старих клієнтів цілим числом (0 або більше), наприклад: 13");
    //                    return;
    //                }
    //                session.setOldClients(val.get());
    //                session.setStep(UserSession.Step.WAIT_NO_CLIENTS);
    //
    //                sendText(chatId, "Введіть кількість клієнтів без покупки");
    //                return;
    //            }

//             WAIT_NO_CLIENTS
            if (session.getStep() == UserSession.Step.WAIT_NO_CLIENTS) {
                Optional<Integer> val = parseNonNegativeInt(text);
                if (val.isEmpty()) {
                    sendText(chatId, "Введіть к-сть клієнтів без покупки цілим числом (0 або більше), наприклад: 13");
                    return;
                }
                session.setNoClients(val.get());

                // ініціалізація оплат, щоб не було null
                if (session.getCashPayment() == null) session.setCashPayment(BigDecimal.ZERO);
                if (session.getCashF() == null) session.setCashF(BigDecimal.ZERO);
                if (session.getCard() == null) session.setCard(BigDecimal.ZERO);
                if (session.getOnlineCard() == null) session.setOnlineCard(BigDecimal.ZERO);
                if (session.getOnlineCash() == null) session.setOnlineCash(BigDecimal.ZERO);

                session.setStep(UserSession.Step.WAIT_PAYMENT_MENU);
                sendPaymentMenu(chatId);

                sendText(chatId, "Введіть готівку в касі (UAH), наприклад: 2500 або 2500.00");
                return;
            }

            // WAIT_PAYMENT_MENU
            if (session.getStep() == UserSession.Step.WAIT_PAYMENT_MENU) {
                switch (text) {
                    case "Готівка" -> {
                        session.setStep(UserSession.Step.WAIT_PAY_CASH);
                        sendText(chatId, "Введіть суму готівки (тип оплати):");
                        return;
                    }
                    case "Готівка фіскальна" -> {
                        session.setStep(UserSession.Step.WAIT_PAY_CASH_F);
                        sendText(chatId, "Введіть суму готівки фіскальної:");
                        return;
                    }
                    case "Кредитні картки" -> {
                        session.setStep(UserSession.Step.WAIT_PAY_CARD);
                        sendText(chatId, "Введіть суму оплат кредитними картками:");
                        return;
                    }
                    case "Онлайн сайт" -> {
                        session.setStep(UserSession.Step.WAIT_PAY_ONLINE_CARD);
                        sendText(chatId, "Введіть суму оплат онлайн (сайт, карта):");
                        return;
                    }
                    case "Онлайн готівка" -> {
                        session.setStep(UserSession.Step.WAIT_PAY_ONLINE_CASH);
                        sendText(chatId, "Введіть суму оплат онлайн (готівка):");
                        return;
                    }
                    case "✅ Продовжити" -> {
                        // прибираємо клавіатуру (опціонально)
                        telegramClient.execute(SendMessage.builder()
                                .chatId(chatId)
                                .text("Продовжуємо. Введіть суму доставки:")
                                .replyMarkup(new ReplyKeyboardRemove(true))
                                .build());

                        session.setStep(UserSession.Step.WAIT_TAXI);
                        return;
                    }
                    default -> {
                        sendText(chatId, "Оберіть тип оплати кнопкою нижче або натисніть ✅ Продовжити.");
                        sendPaymentMenu(chatId);
                        return;
                    }
                }
            }
//
//            // WAIT_CASH
//            if (session.getStep() == UserSession.Step.WAIT_CASH) {
//                Optional<BigDecimal> cash = parseMoney(text);
//                if (cash.isEmpty()) {
//                    sendText(chatId, "Не схоже на число. Введіть готівку.");
//                    return;
//                }
//
//                session.setCash(cash.get());
//                session.setStep(UserSession.Step.WAIT_OLD_CASH);
//
//                sendText(chatId, "Введіть залишок в касі НА ВЧОРА:");
//                return;
//            }

//            if (handleMoneyStep(session, text, chatId,
//                    UserSession.Step.WAIT_OLD_CASH,
//                    session::setOldCash,
//                    UserSession.Step.WAIT_CARD,
//                    "Введіть оплату картою:")) return;
//
//            if (handleMoneyStep(session, text, chatId,
//                    UserSession.Step.WAIT_CARD,
//                    session::setCard,
//                    UserSession.Step.WAIT_SITE_CARD,
//                    "Введіть оплату online картою:")) return;
//
//            if (handleMoneyStep(session, text, chatId,
//                    UserSession.Step.WAIT_SITE_CARD,
//                    session::setOnlineCard,
//                    UserSession.Step.WAIT_SITE_CASH,
//                    "Введіть оплату online готівкою:")) return;
//
//            if (handleMoneyStep(session, text, chatId,
//                    UserSession.Step.WAIT_SITE_CASH,
//                    session::setOnlineCash,
//                    UserSession.Step.WAIT_CASH_F,
//                    "Введіть оплату готівкою фіскал:")) return;

            if (handlePaymentValue(session, text, chatId,
                    UserSession.Step.WAIT_PAY_CASH, session::setCashPayment)) return;

            if (handlePaymentValue(session, text, chatId,
                    UserSession.Step.WAIT_PAY_CASH_F, session::setCashF)) return;

            if (handlePaymentValue(session, text, chatId,
                    UserSession.Step.WAIT_PAY_CARD, session::setCard)) return;

            if (handlePaymentValue(session, text, chatId,
                    UserSession.Step.WAIT_PAY_ONLINE_CARD, session::setOnlineCard)) return;

            if (handlePaymentValue(session, text, chatId,
                    UserSession.Step.WAIT_PAY_ONLINE_CASH, session::setOnlineCash)) return;

            if (handleMoneyStep(session, text, chatId,
                    UserSession.Step.WAIT_TAXI,
                    session::setTaxi,
                    UserSession.Step.WAIT_ATTORNEY,
                    "Введіть суму по повіреній:")) return;

            if (handleMoneyStep(session, text, chatId,
                    UserSession.Step.WAIT_ATTORNEY,
                    session::setAttorney,
                    UserSession.Step.WAIT_COLLECTION,
                    "Введіть суму інкасації:")) return;

            if (handleMoneyStep(session, text, chatId,
                    UserSession.Step.WAIT_COLLECTION,
                    session::setCollection,
                    UserSession.Step.WAIT_WITHDRAWAL,
                    "Введіть суму вилучення:")) return;

            if (handleMoneyStep(session, text, chatId,
                    UserSession.Step.WAIT_WITHDRAWAL,
                    session::setWithdrawal,
                    UserSession.Step.WAIT_CASH_EXPENSES,
                    "Введіть витрати:")) return;

            if (handleMoneyStep(session, text, chatId,
                    UserSession.Step.WAIT_CASH_EXPENSES,
                    session::setExpenses,
                    UserSession.Step.WAIT_NEXT_NAME,
                    "Хто виходить завтра на зміну?")) {

                sendText(chatId, "");
                return;
            }

            if (session.getStep() == UserSession.Step.WAIT_NEXT_NAME) {
                if (text.length() < 3) {
                    sendText(chatId, "Напишіть, будь ласка, ім’я та прізвище (наприклад: Павло Гончар).");
                    return;
                }
                session.setNextFullName(text);
                session.setStep(UserSession.Step.READY);

                String today = LocalDate.now(UA_ZONE).format(DATE_FORMAT);

                BigDecimal avgCheck = calculateAverageCheck(session.getTurnover(), session.getChecks());
                String conversion = calculateConversion(session.getChecks(), session.getNoClients());

                BigDecimal fact = safe(session.getOldCash()).add(safe(session.getTurnover()));
                String planPercent = calculatePlanPercent(fact, DAILY_PLAN); // повертає типу "20.5%"

                Store store = storeCatalog.getByKey(session.getLocation());

                StoreInfo storeInfo = new StoreInfo(
                        store == null ? "Невідомо" : store.name(),
                        store == null ? session.getLocation() : store.address()
                );

                try {
                    sheetsRepo.ensureHeader();
                    sheetsRepo.appendReport(session, storeInfo, today, conversion, avgCheck, fact, planPercent);

                    Store currentStore = storeCatalog.getByKey(session.getLocation());
                    if (currentStore == null) {
                        sendText(chatId, "❗ Не знайдено магазин у довіднику Shop. Перевір ключ локації: " + session.getLocation());
                        return;
                    }

                    BigDecimal newFact = safe(currentStore.fact()).add(safe(session.getTurnover()));
                    log.info("Факт {}", newFact);
                    BigDecimal newCash = safe(currentStore.cash())
                            .add(safe(session.getCash()))
                            .add(safe(session.getCashF()));
                    log.info("Готівка {}", newCash);
                    shopSheetsRepository.updateFactAndCash(session.getLocation(), newFact, newCash);

                    sendText(chatId, "✅ Звіт збережено в Google таблицю (лист \"Звіти\").");
                } catch (Exception e) {
                    log.warn("Failed to save report to Google Sheets", e);
                    sendText(chatId, "❗ Не вдалося зберегти звіт у Google таблицю. Перевір доступ service account і spreadsheetId.");
                }

                // Тимчасове підтвердження (пізніше згенеруємо красивий фінальний звіт)
                sendText(chatId,
                        "✅ Дані прийнято!\n" +
                                "Локація: " + session.getLocation() + "\n" +
                                "Працівник: " + session.getFullName() + "\n" +
                                "Оборот: " + session.getTurnover() + " UAH\n" +
                                "Чеків: " + session.getChecks() + "\n" +
                                "Нові: " + session.getNewClients() + "\n" +
                                "Старі: " + session.getOldClients() + "\n" +
                                "Готівка: " + session.getCash() + " UAH\n\n" +
                                "Завтра на зміні: " + session.getNextFullName() + "\n" +
                                "Далі: зробимо генерацію вечірнього звіту одним повідомленням."
                );

                String headerPreview = buildReportHeader(session);

                sendText(chatId,
                        "✅ Шапка звіту буде такою:\n\n" +
                                headerPreview
                );
                return;
            }

            // READY — поки просто підказка
            if (session.getStep() == UserSession.Step.READY) {
                sendText(chatId, "Дані вже заповнені. Напишіть /start щоб заповнити заново.");
            }

        } catch (TelegramApiException e) {
            log.warn("Telegram API error", e);
        }

    }

    private void sendChooseLocation(String chatId) throws TelegramApiException {
        List<String> keys = storeCatalog.getKeys();

        if (keys.isEmpty()) {
            sendText(chatId, "❗ Локації не завантажені з Google Sheets. Перевір лист Shop.");
            return;
        }

        List<KeyboardRow> rows = new java.util.ArrayList<>();

        for (String key : keys) {
            KeyboardRow row = new KeyboardRow();
            row.add(key);
            rows.add(row);
        }

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();

        telegramClient.execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Оберіть локацію зі списку:")
                        .replyMarkup(keyboard)
                        .build()
        );
    }

    // Оце і є “реагував на старт” (після реєстрації в polling)
    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("✅ Bot registered. running={}", botSession.isRunning());

        if (props.adminChatId() != null) {
            try {
                telegramClient.execute(
                        SendMessage.builder()
                                .chatId(props.adminChatId().toString())
                                .text("🟢 Bot started (registered). running=" + botSession.isRunning())
                                .build()
                );
            } catch (TelegramApiException e) {
                log.warn("Failed to notify adminChatId={}", props.adminChatId(), e);
            }
        }
    }

    private void sendText(String chatId, String text) throws TelegramApiException {
        telegramClient.execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(text)
                        .build()
        );
    }

    /**
     * Дозволяє: "8485", "8485.50", "8 485", "8,485.50", "8485,50", "8485 UAH"
     */
    private Optional<BigDecimal> parseMoney(String raw) {
        if (raw == null) return Optional.empty();

        String s = raw.trim()
                .toLowerCase(Locale.ROOT)
                .replace("uah", "")
                .replace("грн", "")
                .replace("₴", "")
                .replace(" ", "");

        // якщо ввели "8485,50" — робимо крапку
        // якщо ввели "8,485.50" — прибираємо розділювач тисяч
        if (s.contains(",") && s.contains(".")) {
            s = s.replace(",", "");
        } else {
            s = s.replace(",", ".");
        }

        // залишаємо тільки цифри і крапку
        s = s.replaceAll("[^0-9.]", "");

        if (s.isBlank()) return Optional.empty();

        try {
            return Optional.of(new BigDecimal(s));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Integer> parsePositiveInt(String raw) {
        Optional<Integer> v = parseNonNegativeInt(raw);
        return v.filter(x -> x > 0);
    }

    private Optional<Integer> parseNonNegativeInt(String raw) {
        if (raw == null) return Optional.empty();
        String s = raw.trim().replaceAll("[^0-9]", "");
        if (s.isBlank()) return Optional.empty();
        try {
            int val = Integer.parseInt(s);
            if (val < 0) return Optional.empty();
            return Optional.of(val);
        } catch (Exception e) {
            return Optional.empty();
        }
    }



    private String buildReportHeader(UserSession s) {
        Store store = storeCatalog.getByKey(s.getLocation());

        String storeName = (store == null) ? "Невідомий магазин" : store.name();
        String address = (store == null) ? s.getLocation() : store.address();
        BigDecimal plan = (store == null || store.plan() == null) ? DAILY_PLAN : store.plan();

        String today = LocalDate.now(UA_ZONE).format(DATE_FORMAT);

        BigDecimal averageCheck = calculateAverageCheck(s.getTurnover(), s.getChecks());
        String conversion = calculateConversion(s.getChecks(), s.getNoClients());

        BigDecimal fact = safe(s.getOldCash()).add(safe(s.getTurnover()));
        String planPercent = calculatePlanPercent(fact, plan);

        return (storeName + "\n" + address).stripTrailing()
                + "\n"
                + s.getFullName()
                + "\n\n"

                + "💰 Продажі/Оборот\n"
                + "| Дата: " + today + "\n"
                + "| Всього покупців: " + s.getChecks() + "\n"
                + "| Відвідувачів без покупки: " + s.getNoClients() + "\n"
                + "| Конверсія: " + conversion + "\n\n"

                + "📊 ЗВІТ ПРО ПРОДАЖ:\n"
                + "| 💰 Всього оборот: " + formatMoney(s.getTurnover()) + " UAH\n"
                + "| 📃 Чеків: " + s.getChecks() + "\n"
                + "| 🚻 Покупців: " + s.getChecks() + "\n"
                + "| 💰 Середній чек: " + formatMoney(averageCheck) + " UAH\n"
                + "| 🧾 ПЛАН/ФАКТ.  " + formatMoney(plan) + "/" + formatMoney(fact) + " (" + planPercent + ")\n"
                + "| 💳 Кредитні картки: " + formatMoney(s.getCard()) + " UAH\n"
                + "| 💵 Готівка: " + formatMoney(s.getCash()) + " UAH\n"
                + "| 🌐 Онлайн сайт: " + formatMoney(s.getOnlineCard()) + " UAH\n"
                + "| 🌐 Готівка онлайн: " + formatMoney(s.getOnlineCash()) + " UAH\n"
                + "| 💸 Готівка не фіскальна: " + formatMoney(s.getCashF()) + " UAH\n"
                + "| 🚚 Доставка: " + formatMoney(s.getTaxi()) + " UAH\n"
                + "| 📥 Повірена: " + formatMoney(s.getAttorney()) + " UAH\n"
                + "| 🏦 Інкасація: " + formatMoney(s.getCollection()) + " UAH\n"
                + "| 📤 Вилучення: " + formatMoney(s.getWithdrawal()) + " UAH\n"
                + "| 🧾 В касі на кінець дня: " + formatMoney(s.getCash()) + " UAH\n"
                + "| 🧾 Витрати: " + formatMoney(s.getExpenses()) + " UAH\n";
    }

    private String calculatePlanPercent(BigDecimal fact, BigDecimal plan) {
        if (plan == null || plan.compareTo(BigDecimal.ZERO) == 0) {
            return "0%";
        }

        BigDecimal percent = fact
                .multiply(BigDecimal.valueOf(100))
                .divide(plan, 1, RoundingMode.HALF_UP);

        return percent + "%";
    }

    private BigDecimal calculateAverageCheck(BigDecimal turnover, Integer checks) {
        if (turnover == null || checks == null || checks == 0) {
            return BigDecimal.ZERO;
        }

        return turnover.divide(
                BigDecimal.valueOf(checks),
                2,
                RoundingMode.HALF_UP
        );
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0.00";

        return String.format("%,.2f", value);
    }

    private String calculateConversion(Integer checks, Integer noClients) {

        if (checks == null || noClients == null) return "0%";

        int totalVisitors = checks + noClients;

        if (totalVisitors == 0) return "0%";

        BigDecimal conversion = BigDecimal.valueOf(checks)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalVisitors), 1, RoundingMode.HALF_UP);

        return conversion + "%";
    }

    private boolean handleMoneyStep(
            UserSession session,
            String text,
            String chatId,
            UserSession.Step currentStep,
            Consumer<BigDecimal> setter,
            UserSession.Step nextStep,
            String nextQuestion
    ) throws TelegramApiException {

        if (session.getStep() != currentStep) return false;

        Optional<BigDecimal> value = parseMoney(text);
        if (value.isEmpty()) {
            sendText(chatId, "Не схоже на число. Спробуйте ще раз.");
            return true;
        }

        setter.accept(value.get());
        session.setStep(nextStep);
        sendText(chatId, nextQuestion);

        return true;
    }

    public record StoreInfo(String storeName, String address) {}

    private static final Map<String, StoreInfo> STORE_BY_LOCATION = Map.of(
            "Бровари", new StoreInfo("Green State", "м. Бровари, Київська 294/1"),
            "Ірпінь", new StoreInfo("U420", "м. Ірпінь, Центральна 2а"),
            "Борщагівка", new StoreInfo("U420", "м. Софіївська Борщагівка, Соборна 126/1"),
            "Святопетрівське", new StoreInfo("U420", "м. Святопетрівське, Богдана Хмельницького 2")
    );

    private boolean handlePaymentValue(
            UserSession session,
            String text,
            String chatId,
            UserSession.Step step,
            Consumer<BigDecimal> setter
    ) throws TelegramApiException {

        if (session.getStep() != step) return false;

        Optional<BigDecimal> value = parseMoney(text);
        if (value.isEmpty()) {
            sendText(chatId, "Не схоже на число. Введіть суму, наприклад: 1500 або 1500.00");
            return true;
        }

        setter.accept(value.get());
        session.setStep(UserSession.Step.WAIT_PAYMENT_MENU);

        sendText(chatId, "✅ Записав. Можна внести інші типи оплат або натиснути ✅ Продовжити.");
        sendPaymentMenu(chatId);
        return true;
    }

    private void sendPaymentMenu(String chatId) throws TelegramApiException {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Готівка");
        row1.add("Готівка фіскальна");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Кредитні картки");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Онлайн сайт");
        row3.add("Онлайн готівка");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("✅ Продовжити");

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3, row4))
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();

        telegramClient.execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Внесіть відповідні типи оплат:")
                        .replyMarkup(keyboard)
                        .build()
        );
    }


}