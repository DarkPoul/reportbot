package esvar.ua.botreport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.sheets")
public record GoogleSheetsProperties(
        String spreadsheetId,
        String sheetName,
        String shopSheetName,      // Shop
        String credentialsPath
) {}