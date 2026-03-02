package esvar.ua.botreport.sheets;

import com.google.api.services.sheets.v4.Sheets;
import esvar.ua.botreport.bot.EveningReportBot;
import esvar.ua.botreport.config.GoogleSheetsProperties;
import esvar.ua.botreport.session.UserSession;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static esvar.ua.botreport.bot.EveningReportBot.DAILY_PLAN;

@Repository
public class GoogleSheetsReportRepository {

    private static final ZoneId UA_ZONE = ZoneId.of("Europe/Kyiv");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Sheets sheets;
    private final GoogleSheetsProperties props;

    public GoogleSheetsReportRepository(Sheets sheets, GoogleSheetsProperties props) {
        this.sheets = sheets;
        this.props = props;
    }

    public void appendReport(UserSession s,
                             EveningReportBot.StoreInfo store,
                             String today,
                             String conversion,
                             BigDecimal avgCheck,
                             BigDecimal fact,
                             String planPercent) throws Exception {

        // Повернення поки що 0 (як у прикладі)
        int returns = 0;

        // Нормалізація всіх значень, щоб уникнути NullPointerException у List.of(...)
        String storeName = nn(store.storeName());
        String storeAddress = nn(store.address());

        String fullName = nn(s.getFullName());
        Integer checks = nn(s.getChecks());
        Integer noClients = nn(s.getNoClients());
        String safeConversion = nn(conversion);
        BigDecimal turnover = nn(s.getTurnover());
        BigDecimal safeAvgCheck = nn(avgCheck);
        BigDecimal safeFact = nn(fact);
        String safePlanPercent = nn(planPercent);

        BigDecimal card = nn(s.getCard());
        // "Готівка (UAH)" — це тип оплати, беремо з cashPayment
        BigDecimal cashPayment = nn(s.getCashPayment());
        BigDecimal onlineCard = nn(s.getOnlineCard());
        BigDecimal onlineCash = nn(s.getOnlineCash());
        BigDecimal cashF = nn(s.getCashF());
        BigDecimal taxi = nn(s.getTaxi());
        BigDecimal attorney = nn(s.getAttorney());
        BigDecimal collection = nn(s.getCollection());
        BigDecimal withdrawal = nn(s.getWithdrawal());
        BigDecimal endOfDayCash = nn(s.getCash());      // залишок у касі на кінець дня
        BigDecimal expenses = nn(s.getExpenses());

        Integer oldClients = nn(s.getOldClients());
        Integer newClients = nn(s.getNewClients());
        String nextFullName = nn(s.getNextFullName());

        List<Object> row = List.of(
                today,                 // Дата
                storeName,             // Назва магазину
                storeAddress,          // Адреса локації
                fullName,              // Прізвище Ім'я
                checks,                // Всього покупців
                noClients,             // Відвідувачів без покупки
                safeConversion,        // Конверсія %
                returns,               // Повернення
                turnover,              // Всього оборот (UAH)
                checks,                // Чеків
                safeAvgCheck,          // Середній чек (UAH)
                DAILY_PLAN,            // План
                safeFact,              // Факт (вчора + оборот)
                safePlanPercent,       // Виконання плану %

                card,                  // Кредитні картки (UAH)
                cashPayment,           // Готівка (UAH) — тип оплати
                onlineCard,            // Онлайн сайт картка (UAH)
                onlineCash,            // Готівка онлайн (UAH)
                cashF,                 // Готівка не фіскальна (UAH)
                taxi,                  // Доставка (UAH)
                attorney,              // Повірена (UAH)
                collection,            // Інкасація (UAH)
                withdrawal,            // Вилучення (UAH)
                endOfDayCash,          // В касі на кінець дня (UAH)
                expenses,              // Витрати (UAH)

                oldClients,            // Старих клієнтів
                newClients,            // Нових клієнтів
                nextFullName           // Завтра на зміні
        );

        var body = new com.google.api.services.sheets.v4.model.ValueRange()
                .setValues(List.of(row));

        String range = "'" + props.sheetName() + "'!A1";

        sheets.spreadsheets().values()
                .append(props.spreadsheetId(), range, body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    public void ensureHeader() throws Exception {
        String range = "'" + props.sheetName() + "'!A1:AB1"; // 28 колонок = AB
        var resp = sheets.spreadsheets().values()
                .get(props.spreadsheetId(), range)
                .execute();

        boolean empty = resp.getValues() == null || resp.getValues().isEmpty() || resp.getValues().get(0).isEmpty();
        if (!empty) return;

        List<Object> header = List.of(
                "Дата",
                "Назва магазину",
                "Адреса локації",
                "Прізвище Ім'я",
                "Всього покупців",
                "Відвідувачів без покупки",
                "Конверсія %",
                "Повернення",
                "Всього оборот (UAH)",
                "Чеків",
                "Середній чек (UAH)",
                "План",
                "Факт (вчора + оборот)",
                "Виконання плану %",
                "Кредитні картки (UAH)",
                "Готівка (UAH)",
                "Онлайн сайт картка (UAH)",
                "Готівка онлайн (UAH)",
                "Готівка не фіскальна (UAH)",
                "Доставка (UAH)",
                "Повірена (UAH)",
                "Інкасація (UAH)",
                "Вилучення (UAH)",
                "В касі на кінець дня (UAH)",
                "Витрати (UAH)",
                "Старих клієнтів",
                "Нових клієнтів",
                "Завтра на зміні"
        );

        var body = new com.google.api.services.sheets.v4.model.ValueRange()
                .setValues(List.of(header));

        sheets.spreadsheets().values()
                .update(props.spreadsheetId(), "'" + props.sheetName() + "'!A1", body)
                .setValueInputOption("RAW")
                .execute();
    }

    // Допоміжні методи для уникнення null у List.of(...)

    private BigDecimal nn(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer nn(Integer value) {
        return value == null ? 0 : value;
    }

    private String nn(String value) {
        return value == null ? "" : value;
    }
}