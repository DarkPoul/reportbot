package esvar.ua.botreport.store;

import com.google.api.services.sheets.v4.Sheets;
import esvar.ua.botreport.config.GoogleSheetsProperties;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ShopSheetsRepository {

    private final Sheets sheets;
    private final GoogleSheetsProperties properties;

    public ShopSheetsRepository(Sheets sheets, GoogleSheetsProperties properties) {
        this.sheets = sheets;
        this.properties = properties;
    }

    public Map<String, Store> loadStores() throws Exception {
        String range = "'" + properties.shopSheetName() + "'!A:D";
        var response = sheets.spreadsheets().values()
                .get(properties.spreadsheetId(), range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.size() <= 1) {
            return Map.of();
        }

        Map<String, Store> result = new LinkedHashMap<>();
        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row == null || row.isEmpty()) {
                continue;
            }

            String key = getString(row, 0);
            if (key == null || key.isBlank()) {
                continue;
            }

            result.put(key.trim(), new Store(
                    key.trim(),
                    trimToEmpty(getString(row, 1)),
                    trimToEmpty(getString(row, 2)),
                    parseBigDecimal(getString(row, 3))
            ));
        }
        return result;
    }

    private String getString(List<Object> row, int idx) {
        if (idx >= row.size()) {
            return null;
        }
        Object value = row.get(idx);
        return value == null ? null : value.toString();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        String normalized = value.trim().replace(" ", "");
        if (normalized.contains(",") && normalized.contains(".")) {
            normalized = normalized.replace(",", "");
        } else {
            normalized = normalized.replace(",", ".");
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}
