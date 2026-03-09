package esvar.ua.botreport.flow;

import esvar.ua.botreport.session.UserSession;
import esvar.ua.botreport.telegram.BotMessages;
import esvar.ua.botreport.telegram.KeyboardFactory;
import esvar.ua.botreport.telegram.MessageSender;
import esvar.ua.botreport.validation.InputValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CashInRegisterStepHandler implements StepHandler {

    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private final InputValidator inputValidator;

    public CashInRegisterStepHandler(MessageSender messageSender,
                                     KeyboardFactory keyboardFactory,
                                     InputValidator inputValidator) {
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
        this.inputValidator = inputValidator;
    }

    @Override
    public List<UserSession.Step> supportedSteps() {
        return List.of(UserSession.Step.WAIT_CASH);
    }

    @Override
    public void handle(long userId, String chatId, String text, UserSession session) {
        var cashInRegister = inputValidator.parseMoney(text);
        if (cashInRegister.isEmpty()) {
            log.info("validation_error userId={} step={}", userId, session.getStep());
            messageSender.sendText(chatId, BotMessages.INVALID_MONEY);
            return;
        }
        session.getReportDraft().setCashInRegister(cashInRegister.get());
        session.setStep(UserSession.Step.WAIT_PAYMENT_MENU);
        messageSender.sendWithKeyboard(chatId, BotMessages.PAYMENT_MENU, keyboardFactory.buildPaymentKeyboard());
    }
}

