package esvar.ua.botreport;

import esvar.ua.botreport.config.BotProperties;
import esvar.ua.botreport.config.GoogleSheetsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BotProperties.class, GoogleSheetsProperties.class})
public class BotreportApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotreportApplication.class, args);
    }
}