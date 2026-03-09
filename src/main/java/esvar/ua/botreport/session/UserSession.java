package esvar.ua.botreport.session;

public class UserSession {

    public enum Step {
        WAIT_LOCATION,
        WAIT_NAME,
        WAIT_TURNOVER,
        WAIT_CHECKS,
        WAIT_NEW_CLIENTS,
        WAIT_NO_CLIENTS,
        WAIT_CASH,
        WAIT_PAYMENT_MENU,
        WAIT_PAY_CASH,
        WAIT_PAY_CASH_F,
        WAIT_PAY_CARD,
        WAIT_PAY_ONLINE_CARD,
        WAIT_PAY_ONLINE_CASH,
        WAIT_TAXI,
        WAIT_COLLECTION,
        WAIT_CASH_EXPENSES,
        WAIT_NEXT_NAME,
        READY
    }

    private Step step = Step.WAIT_LOCATION;
    private final ReportDraft reportDraft = new ReportDraft();

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public ReportDraft getReportDraft() {
        return reportDraft;
    }
}
