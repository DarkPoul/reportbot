package esvar.ua.botreport.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot.telegram")
public record BotProperties(
        String username,
        String token,
        Long adminChatId
) {}