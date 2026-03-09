package esvar.ua.botreport.flow;

import esvar.ua.botreport.session.UserSession;

import java.util.List;

public interface StepHandler {

    List<UserSession.Step> supportedSteps();

    void handle(long userId, String chatId, String text, UserSession session);
}
