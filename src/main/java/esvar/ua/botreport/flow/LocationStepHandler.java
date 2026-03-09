package esvar.ua.botreport.flow;

import esvar.ua.botreport.session.ReportDraft;
import esvar.ua.botreport.session.UserSession;
import esvar.ua.botreport.store.Store;
import esvar.ua.botreport.store.StoreCatalog;
import esvar.ua.botreport.telegram.BotMessages;
import esvar.ua.botreport.telegram.KeyboardFactory;
import esvar.ua.botreport.telegram.MessageSender;
import esvar.ua.botreport.validation.InputValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LocationStepHandler implements StepHandler {

    private final StoreCatalog storeCatalog;
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private final InputValidator inputValidator;

    public LocationStepHandler(StoreCatalog storeCatalog,
                               MessageSender messageSender,
                               KeyboardFactory keyboardFactory,
                               InputValidator inputValidator) {
        this.storeCatalog = storeCatalog;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
        this.inputValidator = inputValidator;
    }

    @Override
    public List<UserSession.Step> supportedSteps() {
        return List.of(UserSession.Step.WAIT_LOCATION);
    }

    @Override
    public void handle(long userId, String chatId, String text, UserSession session) {
        String normalized = inputValidator.normalizeText(text);
        Store store = storeCatalog.getByKey(normalized);
        if (store == null) {
            log.info("invalid_store_selection userId={}", userId);
            messageSender.sendText(chatId, BotMessages.LOCATION_INVALID);
            messageSender.sendWithKeyboard(chatId, BotMessages.CHOOSE_LOCATION, keyboardFactory.buildLocationKeyboard(storeCatalog.getKeys()));
            return;
        }

        ReportDraft draft = session.getReportDraft();
        draft.setLocationKey(store.key());
        session.setStep(UserSession.Step.WAIT_NAME);
        log.info("store_selected userId={} storeKey={}", userId, store.key());
        messageSender.sendAndRemoveKeyboard(chatId, BotMessages.ASK_NAME, keyboardFactory.removeKeyboard());
    }
}
