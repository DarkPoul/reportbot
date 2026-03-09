package esvar.ua.botreport.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import esvar.ua.botreport.config.GoogleSheetsProperties;
import esvar.ua.botreport.report.ReportSaveRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GoogleSheetsReportRepository {

    private final Sheets sheets;
    private final GoogleSheetsProperties properties;

    public GoogleSheetsReportRepository(Sheets sheets, GoogleSheetsProperties properties) {
        this.sheets = sheets;
        this.properties = properties;
    }

    public void saveReport(ReportSaveRequest request) throws Exception {
        ensureHeader();

        List<Object> row = List.of(
                request.date(),
                request.key(),
                request.employeeName(),
                request.totalCustomers(),
                request.visitorsWithoutPurchase(),
                request.conversionPercent(),
                request.newCustomers(),
                0,
                request.turnover(),
                request.cashNonFiscal(),
                request.cashFiscal(),
                request.creditCards(),
                request.cashOnline(),
                request.cardOnline(),
                request.delivery(),
                request.collection(),
                request.cashAtEndOfDay(),
                request.expenses(),
                request.tomorrowOnShift()
        );

        ValueRange body = new ValueRange().setValues(List.of(row));
        int existingRow = findReportRow(request.date(), request.key());
        if (existingRow > 0) {
            String updateRange = "'" + properties.sheetName() + "'!A" + existingRow + ":S" + existingRow;
            sheets.spreadsheets().values()
                    .update(properties.spreadsheetId(), updateRange, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            return;
        }

        String appendRange = "'" + properties.sheetName() + "'!A:S";
        sheets.spreadsheets().values()
                .append(properties.spreadsheetId(), appendRange, body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    public void ensureHeader() throws Exception {
        String range = "'" + properties.sheetName() + "'!A1:S1";
        var response = sheets.spreadsheets().values()
                .get(properties.spreadsheetId(), range)
                .execute();

        boolean empty = response.getValues() == null
                || response.getValues().isEmpty()
                || response.getValues().getFirst().isEmpty();
        if (!empty) {
            return;
        }

        List<Object> header = List.of(
                "Р”Р°С‚Р°",
                "РљР»СЋС‡",
                "РџСЂС–Р·РІРёС‰Рµ Р†Рј'СЏ",
                "Р’СЃСЊРѕРіРѕ РїРѕРєСѓРїС†С–РІ",
                "Р’С–РґРІС–РґСѓРІР°С‡С–РІ Р±РµР· РїРѕРєСѓРїРєРё",
                "РљРѕРЅРІРµСЂСЃС–СЏ %",
                "РќРѕРІРёС… РєР»С–С”РЅС‚С–РІ",
                "РџРѕРІРµСЂРЅРµРЅРЅСЏ",
                "Р’СЃСЊРѕРіРѕ РѕР±РѕСЂРѕС‚ (UAH)",
                "Р“РѕС‚С–РІРєР° РЅРµ С„С–СЃРєР°Р»СЊРЅР° (UAH)",
                "Р“РѕС‚С–РІРєР° (UAH)",
                "РљСЂРµРґРёС‚РЅС– РєР°СЂС‚РєРё (UAH)",
                "Р“РѕС‚С–РІРєР° РѕРЅР»Р°Р№РЅ (UAH)",
                "РљР°СЂС‚РєР° РѕРЅР»Р°Р№РЅ (UAH)",
                "Р”РѕСЃС‚Р°РІРєР° (UAH)",
                "Р†РЅРєР°СЃР°С†С–СЏ (UAH)",
                "Р’ РєР°СЃС– РЅР° РєС–РЅРµС†СЊ РґРЅСЏ (UAH)",
                "Р’РёС‚СЂР°С‚Рё (UAH)",
                "Р—Р°РІС‚СЂР° РЅР° Р·РјС–РЅС–"
        );

        ValueRange body = new ValueRange().setValues(List.of(header));
        sheets.spreadsheets().values()
                .update(properties.spreadsheetId(), range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    private int findReportRow(String date, String key) throws Exception {
        String range = "'" + properties.sheetName() + "'!A:B";
        var response = sheets.spreadsheets().values()
                .get(properties.spreadsheetId(), range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.size() <= 1) {
            return -1;
        }

        String normalizedDate = valueOrEmpty(date).trim();
        String normalizedKey = valueOrEmpty(key).trim();
        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row == null || row.isEmpty()) {
                continue;
            }
            String rowDate = valueOrEmpty(asString(row, 0)).trim();
            String rowKey = valueOrEmpty(asString(row, 1)).trim();
            if (normalizedDate.equals(rowDate) && normalizedKey.equalsIgnoreCase(rowKey)) {
                return i + 1;
            }
        }
        return -1;
    }

    private String asString(List<Object> row, int index) {
        if (index >= row.size()) {
            return "";
        }
        Object value = row.get(index);
        return value == null ? "" : value.toString();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
