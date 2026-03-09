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
public class ChecksStepHandler implements StepHandler {

    private final MessageSender messageSender;
    private final InputValidator inputValidator;

    public ChecksStepHandler(MessageSender messageSender, InputValidator inputValidator) {
        this.messageSender = messageSender;
        this.inputValidator = inputValidator;
    }

    @Override
    public List<UserSession.Step> supportedSteps() {
        return List.of(UserSession.Step.WAIT_CHECKS);
    }

    @Override
    public void handle(long userId, String chatId, String text, UserSession session) {
        var buyers = inputValidator.parsePositiveInt(text);
        if (buyers.isEmpty()) {
            log.info("validation_error userId={} step={}", userId, session.getStep());
            messageSender.sendText(chatId, BotMessages.ASK_CHECKS);
            return;
        }
        session.getReportDraft().setBuyers(buyers.get());
        session.setStep(UserSession.Step.WAIT_NEW_CLIENTS);
        messageSender.sendText(chatId, BotMessages.ASK_NEW_CLIENTS);
    }
}
