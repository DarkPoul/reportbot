package esvar.ua.botreport.flow;

import esvar.ua.botreport.session.SessionService;
import esvar.ua.botreport.session.UserSession;
import esvar.ua.botreport.telegram.BotMessages;
import esvar.ua.botreport.telegram.KeyboardFactory;
import esvar.ua.botreport.telegram.MessageSender;
import esvar.ua.botreport.store.StoreCatalog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportFlowService {

    private final SessionService sessionService;
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private final StoreCatalog storeCatalog;
    private final Map<UserSession.Step, StepHandler> handlers;

    public ReportFlowService(SessionService sessionService,
                             MessageSender messageSender,
                             KeyboardFactory keyboardFactory,
                             StoreCatalog storeCatalog,
                             List<StepHandler> stepHandlers) {
        this.sessionService = sessionService;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
        this.storeCatalog = storeCatalog;
        this.handlers = new EnumMap<>(UserSession.Step.class);
        for (StepHandler handler : stepHandlers) {
            for (UserSession.Step step : handler.supportedSteps()) {
                this.handlers.put(step, handler);
            }
        }
    }

    public void start(long userId, String chatId) {
        sessionService.reset(userId);
        log.info("session_started userId={} chatId={}", userId, chatId);
        sendLocationChooser(chatId);
    }

    public void cancel(long userId, String chatId) {
        sessionService.reset(userId);
        log.info("session_cancelled userId={} chatId={}", userId, chatId);
        messageSender.sendAndRemoveKeyboard(chatId, BotMessages.CANCELLED, keyboardFactory.removeKeyboard());
    }

    public void handleText(long userId, String chatId, String text) {
        UserSession session = sessionService.getOrCreate(userId);
        StepHandler handler = handlers.get(session.getStep());
        if (handler == null) {
            log.warn("missing_step_handler step={} userId={}", session.getStep(), userId);
            messageSender.sendText(chatId, BotMessages.UNEXPECTED_ERROR);
            return;
        }

        UserSession.Step currentStep = session.getStep();
        try {
            handler.handle(userId, chatId, text, session);
            if (currentStep != session.getStep()) {
                log.info("step_transition userId={} from={} to={}", userId, currentStep, session.getStep());
            }
        } catch (Exception ex) {
            log.warn("flow_error userId={} step={}", userId, session.getStep(), ex);
            messageSender.sendText(chatId, BotMessages.UNEXPECTED_ERROR);
        }
    }

    private void sendLocationChooser(String chatId) {
        List<String> keys = storeCatalog.getKeys();
        if (keys.isEmpty()) {
            messageSender.sendText(chatId, BotMessages.STORES_NOT_LOADED);
            return;
        }
        messageSender.sendWithKeyboard(chatId, BotMessages.CHOOSE_LOCATION, keyboardFactory.buildLocationKeyboard(keys));
    }
}
