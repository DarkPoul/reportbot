package esvar.ua.botreport.flow;

import esvar.ua.botreport.report.ReportCalculationService;
import esvar.ua.botreport.report.ReportPersistenceService;
import esvar.ua.botreport.session.ReportDraft;
import esvar.ua.botreport.session.UserSession;
import esvar.ua.botreport.store.Store;
import esvar.ua.botreport.store.StoreCatalog;
import esvar.ua.botreport.telegram.BotMessages;
import esvar.ua.botreport.telegram.MessageSender;
import esvar.ua.botreport.validation.InputValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NextNameStepHandler implements StepHandler {

    private final MessageSender messageSender;
    private final InputValidator inputValidator;
    private final ReportPersistenceService persistenceService;
    private final ReportCalculationService calculationService;
    private final StoreCatalog storeCatalog;

    public NextNameStepHandler(MessageSender messageSender,
                               InputValidator inputValidator,
                               ReportPersistenceService persistenceService,
                               ReportCalculationService calculationService,
                               StoreCatalog storeCatalog) {
        this.messageSender = messageSender;
        this.inputValidator = inputValidator;
        this.persistenceService = persistenceService;
        this.calculationService = calculationService;
        this.storeCatalog = storeCatalog;
    }

    @Override
    public List<UserSession.Step> supportedSteps() {
        return List.of(UserSession.Step.WAIT_NEXT_NAME, UserSession.Step.READY);
    }

    @Override
    public void handle(long userId, String chatId, String text, UserSession session) {
        if (session.getStep() == UserSession.Step.READY) {
            messageSender.sendText(chatId, BotMessages.READY_MESSAGE);
            return;
        }

        String normalized = inputValidator.normalizeText(text);
        if (normalized.length() < 3) {
            log.info("validation_error userId={} step={}", userId, session.getStep());
            messageSender.sendText(chatId, BotMessages.ASK_NEXT_NAME);
            return;
        }

        ReportDraft draft = session.getReportDraft();
        draft.setNextEmployeeName(normalized);
        session.setStep(UserSession.Step.READY);

        try {
            persistenceService.saveReport(session);

            Store store = storeCatalog.getByKey(draft.getLocationKey());
            if (store == null) {
                messageSender.sendText(chatId, BotMessages.STORE_NOT_FOUND + " " + draft.getLocationKey());
                return;
            }

            messageSender.sendText(chatId, BotMessages.REPORT_SAVED);
            messageSender.sendText(chatId,
                    "вњ… Р”Р°РЅС– РїСЂРёР№РЅСЏС‚Рѕ!\n"
                            + "Р›РѕРєР°С†С–СЏ: " + draft.getLocationKey() + "\n"
                            + "РџСЂР°С†С–РІРЅРёРє: " + draft.getEmployeeName() + "\n"
                            + "РћР±РѕСЂРѕС‚: " + calculationService.formatMoney(draft.getTurnover()) + " UAH\n"
                            + "Р§РµРєС–РІ: " + calculationService.safeInt(draft.getBuyers()) + "\n"
                            + "РќРѕРІС–: " + calculationService.safeInt(draft.getNewClients()) + "\n"
                            + "Р“РѕС‚С–РІРєР° РІ РєР°СЃС–: " + calculationService.formatMoney(draft.getCashInRegister()) + " UAH\n\n"
                            + "Р—Р°РІС‚СЂР° РЅР° Р·РјС–РЅС–: " + draft.getNextEmployeeName());
            messageSender.sendText(chatId,
                    "вњ… РЁР°РїРєР° Р·РІС–С‚Сѓ Р±СѓРґРµ С‚Р°РєРѕСЋ:\n\n"
                            + calculationService.buildReportHeader(draft, store));
        } catch (IllegalStateException ex) {
            log.warn("report_save_store_missing userId={} storeKey={}", userId, draft.getLocationKey(), ex);
            messageSender.sendText(chatId, BotMessages.STORE_NOT_FOUND + " " + draft.getLocationKey());
        } catch (Exception ex) {
            log.warn("report_save_failed userId={} storeKey={}", userId, draft.getLocationKey(), ex);
            messageSender.sendText(chatId, BotMessages.REPORT_SAVE_FAILED);
        }
    }
}
