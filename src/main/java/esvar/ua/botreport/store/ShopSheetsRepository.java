package esvar.ua.botreport.store;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import esvar.ua.botreport.config.GoogleSheetsProperties;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class ShopSheetsRepository {

    private final Sheets sheets;
    private final GoogleSheetsProperties props;

    public ShopSheetsRepository(Sheets sheets, GoogleSheetsProperties props) {
        this.sheets = sheets;
        this.props = props;
    }

    public Map<String, Store> loadStores() throws Exception {
        // Читаємо A:D, 1-й рядок це заголовки
        String range = "'" + props.shopSheetName() + "'!A:G";

        var resp = sheets.spreadsheets().values()
                .get(props.spreadsheetId(), range)
                .execute();

        List<List<Object>> values = resp.getValues();
        if (values == null || values.size() <= 1) {
            return Map.of();
        }

        Map<String, Store> result = new LinkedHashMap<>();

        for (int i = 1; i < values.size(); i++) { // пропускаємо header
            List<Object> row = values.get(i);
            if (row == null || row.isEmpty()) continue;

            String key = getString(row, 0);
            String name = getString(row, 1);
            String address = getString(row, 2);
            BigDecimal plan = getBigDecimal(row, 3);
            BigDecimal fact = getBigDecimal(row, 4);
            BigDecimal cash = getBigDecimal(row, 6);

            if (key == null || key.isBlank()) continue;

            result.put(key.trim(), new Store(
                    key.trim(),
                    name == null ? "" : name.trim(),
                    address == null ? "" : address.trim(),
                    plan == null ? BigDecimal.ZERO : plan,
                    fact == null ? BigDecimal.ZERO : fact,
                    cash == null ? BigDecimal.ZERO : cash
            ));
        }

        return result;
    }

    private String getString(List<Object> row, int idx) {
        if (idx >= row.size()) return null;
        Object v = row.get(idx);
        return v == null ? null : v.toString();
    }

    private BigDecimal getBigDecimal(List<Object> row, int idx) {
        String s = getString(row, idx);
        if (s == null || s.isBlank()) return null;

        // Sheets може віддати "533000" або "533000.0"
        try {
            return new BigDecimal(s.replace(" ", "").replace(",", ""));
        } catch (Exception e) {
            return null;
        }
    }

    public void updateFactAndCash(String storeKey, BigDecimal fact, BigDecimal cash) throws Exception {
        int rowIndex = findRowByKey(storeKey); // 1-based index в Sheets (рядок 1 = заголовки)

        // E = 5-та колонка, G = 7-ма
        String factRange = "'" + props.shopSheetName() + "'!E" + rowIndex;
        String cashRange = "'" + props.shopSheetName() + "'!G" + rowIndex;

        List<ValueRange> data = List.of(
                new ValueRange().setRange(factRange).setValues(List.of(List.of(toSheetsNumber(fact)))),
                new ValueRange().setRange(cashRange).setValues(List.of(List.of(toSheetsNumber(cash))))
        );

        sheets.spreadsheets().values()
                .batchUpdate(props.spreadsheetId(), new com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest()
                        .setValueInputOption("USER_ENTERED")
                        .setData(data))
                .execute();
    }

    private int findRowByKey(String storeKey) throws Exception {
        if (storeKey == null || storeKey.isBlank()) {
            throw new IllegalArgumentException("storeKey is blank");
        }

        String keyRange = "'" + props.shopSheetName() + "'!A:A";

        var resp = sheets.spreadsheets().values()
                .get(props.spreadsheetId(), keyRange)
                .execute();

        List<List<Object>> values = resp.getValues();
        if (values == null || values.isEmpty()) {
            throw new IllegalStateException("Shop sheet is empty: " + props.shopSheetName());
        }

        String needle = storeKey.trim();

        // values.get(0) == A1 (заголовок)
        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row == null || row.isEmpty()) continue;

            String key = row.getFirst() == null ? "" : row.getFirst().toString().trim();
            if (needle.equalsIgnoreCase(key)) {
                return i + 1; // i=1 -> рядок 2
            }
        }

        throw new IllegalArgumentException("Store key not found in Shop sheet: " + storeKey);
    }

    private Object toSheetsNumber(BigDecimal v) {
        // щоб не писало null
        if (v == null) return 0;
        // USER_ENTERED норм сприймає Number, краще передати double
        return v.doubleValue();
    }
}