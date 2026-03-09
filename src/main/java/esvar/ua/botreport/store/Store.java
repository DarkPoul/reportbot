package esvar.ua.botreport.store;

import java.math.BigDecimal;

public record Store(
        String key,        // "Р вЂРЎР‚Р С•Р Р†Р В°РЎР‚Р С‘"
        String name,       // "Green State"
        String address,    // "Р С. Р вЂРЎР‚Р С•Р Р†Р В°РЎР‚Р С‘, Р С™Р С‘РЎвЂ”Р Р†РЎРѓРЎРЉР С”Р В° 294/1"
        BigDecimal plan   // 533000
) {}