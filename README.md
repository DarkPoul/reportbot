# evening_report_bot

Telegram long-polling бот для вечірніх магазинних звітів.

## Можливості
- Реєстрація співробітника через `/start`.
- Редагування профілю: `/profile`, `/profile_edit`.
- Покроковий майстер звіту: `/report` або `/report YYYY-MM-DD`.
- Продовження чернетки: `/draft`.
- Перегляд останнього фінального звіту: `/last`.
- Команди в майстрі: `/back`, `/cancel`.
- Локальне збереження у `./data`: `users.json`, `drafts.json`, `reports.json`.
- Дані стійкі до перезапуску (чернетки, користувачі, фінальні звіти).

## Вимоги
- Java 17
- Maven 3.9+

## Змінні середовища
```bash
export BOT_TOKEN=...
export BOT_USERNAME=...
```

## Запуск
```bash
mvn clean package
java -jar target/evening_report_bot-1.0.0-jar-with-dependencies.jar
```

## Структура
- `domain/` — моделі (`EmployeeProfile`, `ReportDraft`, `FinalReport`, `ProductBreakdown`).
- `storage/` — JSON збереження з atomic-write.
- `storage/repositories/` — репозиторії користувачів/чернеток/звітів.
- `service/` — обчислення звіту, форматування, реєстрація.
- `bot/` — `Main`, Telegram bot, роутинг update.
- `ui/` — стани майстра, підказки та клавіатури.

## Примітка
Усі відповіді бота українською мовою; фінальний формат звіту відповідає заданому шаблону з роздільниками `__` та емодзі.
