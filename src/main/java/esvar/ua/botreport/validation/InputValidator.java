package esvar.ua.botreport.validation;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

@Component
public class InputValidator {

    public Optional<BigDecimal> parseMoney(String raw) {
        if (raw == null) {
            return Optional.empty();
        }

        String normalized = raw.trim()
                .toLowerCase(Locale.ROOT)
                .replace("uah", "")
                .replace("РіСЂРЅ", "")
                .replace("в‚ґ", "")
                .replace(" ", "");

        if (normalized.contains(",") && normalized.contains(".")) {
            normalized = normalized.replace(",", "");
        } else {
            normalized = normalized.replace(",", ".");
        }

        normalized = normalized.replaceAll("[^0-9.]", "");
        if (normalized.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(new BigDecimal(normalized));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public Optional<Integer> parsePositiveInt(String raw) {
        return parseNonNegativeInt(raw).filter(value -> value > 0);
    }

    public Optional<Integer> parseNonNegativeInt(String raw) {
        if (raw == null) {
            return Optional.empty();
        }

        String normalized = raw.trim().replaceAll("\\s+", "");
        if (!normalized.matches("\\d+")) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(normalized));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public String normalizeText(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("\\s+", " ");
    }
}
