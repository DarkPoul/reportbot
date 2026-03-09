package esvar.ua.botreport.flow;

import esvar.ua.botreport.session.UserSession;
import esvar.ua.botreport.telegram.BotMessages;
import esvar.ua.botreport.telegram.MessageSender;
import esvar.ua.botreport.validation.InputValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NameStepHandler implements StepHandler {

    private final MessageSender messageSender;
    private final InputValidator inputValidator;

    public NameStepHandler(MessageSender messageSender, InputValidator inputValidator) {
        this.messageSender = messageSender;
        this.inputValidator = inputValidator;
    }

    @Override
    public List<UserSession.Step> supportedSteps() {
        return List.of(UserSession.Step.WAIT_NAME);
    }

    @Override
    public void handle(long userId, String chatId, String text, UserSession session) {
        String normalized = inputValidator.normalizeText(text);
        if (normalized.length() < 5) {
            log.info("validation_error userId={} step={}", userId, session.getStep());
            messageSender.sendText(chatId, BotMessages.ASK_NAME_RETRY);
            return;
        }
        session.getReportDraft().setEmployeeName(normalized);
        session.setStep(UserSession.Step.WAIT_TURNOVER);
        messageSender.sendText(chatId, BotMessages.ASK_TURNOVER);
    }
}
