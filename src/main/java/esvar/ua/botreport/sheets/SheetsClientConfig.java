package esvar.ua.botreport.sheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import esvar.ua.botreport.config.GoogleSheetsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;

@Configuration
public class SheetsClientConfig {

    @Bean
    public Sheets sheets(GoogleSheetsProperties props) throws Exception {
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(props.credentialsPath());

        if (in == null) {
            throw new IllegalStateException("Google credentials not found in resources: " + props.credentialsPath());
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(List.of(SheetsScopes.SPREADSHEETS)); // доступ до таблиць :contentReference[oaicite:2]{index=2}

        return new Sheets.Builder(
                transport,
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("botreport").build();
    }
}