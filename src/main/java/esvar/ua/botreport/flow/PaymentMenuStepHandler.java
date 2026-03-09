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
public class PaymentMenuStepHandler implements StepHandler {

    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private final InputValidator inputValidator;

    public PaymentMenuStepHandler(MessageSender messageSender,
                                  KeyboardFactory keyboardFactory,
                                  InputValidator inputValidator) {
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
        this.inputValidator = inputValidator;
    }

    @Override
    public List<UserSession.Step> supportedSteps() {
        return List.of(
                UserSession.Step.WAIT_PAYMENT_MENU,
                UserSession.Step.WAIT_PAY_CASH,
                UserSession.Step.WAIT_PAY_CASH_F,
                UserSession.Step.WAIT_PAY_CARD,
                UserSession.Step.WAIT_PAY_ONLINE_CARD,
                UserSession.Step.WAIT_PAY_ONLINE_CASH
        );
    }

    @Override
    public void handle(long userId, String chatId, String text, UserSession session) {
        switch (session.getStep()) {
            case WAIT_PAYMENT_MENU -> handleMenuSelection(userId, chatId, text, session);
            case WAIT_PAY_CASH -> savePayment(userId, chatId, text, session, UserSession.Step.WAIT_PAY_CASH);
            case WAIT_PAY_CASH_F -> savePayment(userId, chatId, text, session, UserSession.Step.WAIT_PAY_CASH_F);
            case WAIT_PAY_CARD -> savePayment(userId, chatId, text, session, UserSession.Step.WAIT_PAY_CARD);
            case WAIT_PAY_ONLINE_CARD -> savePayment(userId, chatId, text, session, UserSession.Step.WAIT_PAY_ONLINE_CARD);
            case WAIT_PAY_ONLINE_CASH -> savePayment(userId, chatId, text, session, UserSession.Step.WAIT_PAY_ONLINE_CASH);
            default -> throw new IllegalStateException("Unsupported payment step " + session.getStep());
        }
    }

    private void handleMenuSelection(long userId, String chatId, String text, UserSession session) {
        String normalized = inputValidator.normalizeText(text);
        switch (normalized) {
            case BotMessages.PAYMENT_CASH -> {
                session.setStep(UserSession.Step.WAIT_PAY_CASH);
                messageSender.sendText(chatId, BotMessages.ASK_PAYMENT_CASH);
            }
            case BotMessages.PAYMENT_CASH_FISCAL -> {
                session.setStep(UserSession.Step.WAIT_PAY_CASH_F);
                messageSender.sendText(chatId, BotMessages.ASK_PAYMENT_CASH_FISCAL);
            }
            case BotMessages.PAYMENT_CARD -> {
                session.setStep(UserSession.Step.WAIT_PAY_CARD);
                messageSender.sendText(chatId, BotMessages.ASK_PAYMENT_CARD);
            }
            case BotMessages.PAYMENT_ONLINE_CARD -> {
                session.setStep(UserSession.Step.WAIT_PAY_ONLINE_CARD);
                messageSender.sendText(chatId, BotMessages.ASK_PAYMENT_ONLINE_CARD);
            }
            case BotMessages.PAYMENT_ONLINE_CASH -> {
                session.setStep(UserSession.Step.WAIT_PAY_ONLINE_CASH);
                messageSender.sendText(chatId, BotMessages.ASK_PAYMENT_ONLINE_CASH);
            }
            case BotMessages.PAYMENT_CONTINUE -> {
                session.setStep(UserSession.Step.WAIT_TAXI);
                messageSender.sendAndRemoveKeyboard(chatId, BotMessages.ASK_TAXI, keyboardFactory.removeKeyboard());
            }
            default -> {
                log.info("validation_error userId={} step={}", userId, session.getStep());
                messageSender.sendText(chatId, BotMessages.PAYMENT_MENU_INVALID);
                messageSender.sendWithKeyboard(chatId, BotMessages.PAYMENT_MENU, keyboardFactory.buildPaymentKeyboard());
            }
        }
    }

    private void savePayment(long userId, String chatId, String text, UserSession session, UserSession.Step currentStep) {
        var amount = inputValidator.parseMoney(text);
        if (amount.isEmpty()) {
            log.info("validation_error userId={} step={}", userId, currentStep);
            messageSender.sendText(chatId, BotMessages.INVALID_MONEY_EXAMPLE);
            return;
        }

        switch (currentStep) {
            case WAIT_PAY_CASH -> session.getReportDraft().getPaymentData().setCash(amount.get());
            case WAIT_PAY_CASH_F -> session.getReportDraft().getPaymentData().setCashFiscal(amount.get());
            case WAIT_PAY_CARD -> session.getReportDraft().getPaymentData().setCard(amount.get());
            case WAIT_PAY_ONLINE_CARD -> session.getReportDraft().getPaymentData().setOnlineCard(amount.get());
            case WAIT_PAY_ONLINE_CASH -> session.getReportDraft().getPaymentData().setOnlineCash(amount.get());
            default -> throw new IllegalStateException("Unsupported payment step " + currentStep);
        }

        session.setStep(UserSession.Step.WAIT_PAYMENT_MENU);
        messageSender.sendText(chatId, BotMessages.PAYMENT_SAVED);
        messageSender.sendWithKeyboard(chatId, BotMessages.PAYMENT_MENU, keyboardFactory.buildPaymentKeyboard());
    }
}
