package esvar.ua.botreport.store;

import java.math.BigDecimal;

public record Store(
        String key,        // "Бровари"
        String name,       // "Green State"
        String address,    // "м. Бровари, Київська 294/1"
        BigDecimal plan,   // 533000
        BigDecimal fact,   // факт виторгу по всім типам платежів
        BigDecimal cash    // в касі
) {}