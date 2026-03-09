package esvar.ua.botreport.report;

import esvar.ua.botreport.session.ReportDraft;
import esvar.ua.botreport.session.UserSession;
import esvar.ua.botreport.sheets.GoogleSheetsReportRepository;
import esvar.ua.botreport.store.Store;
import esvar.ua.botreport.store.StoreCatalog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportPersistenceService {

    private final GoogleSheetsReportRepository reportRepository;
    private final StoreCatalog storeCatalog;
    private final ReportCalculationService calculationService;

    public ReportPersistenceService(GoogleSheetsReportRepository reportRepository,
                                    StoreCatalog storeCatalog,
                                    ReportCalculationService calculationService) {
        this.reportRepository = reportRepository;
        this.storeCatalog = storeCatalog;
        this.calculationService = calculationService;
    }

    public void saveReport(UserSession session) throws Exception {
        ReportDraft draft = session.getReportDraft();
        Store store = storeCatalog.getByKey(draft.getLocationKey());
        if (store == null) {
            throw new IllegalStateException("Store not found for key=" + draft.getLocationKey());
        }

        ReportSaveRequest request = new ReportSaveRequest(
                calculationService.today(),
                buildKey(store),
                valueOrEmpty(draft.getEmployeeName()),
                calculationService.safeInt(draft.getBuyers()),
                calculationService.safeInt(draft.getVisitorsWithoutPurchase()),
                calculationService.conversionPercent(draft.getBuyers(), draft.getVisitorsWithoutPurchase()),
                calculationService.safeInt(draft.getNewClients()),
                calculationService.safeMoney(draft.getTurnover()),
                calculationService.safeMoney(draft.getPaymentData().getCash()),
                calculationService.safeMoney(draft.getPaymentData().getCashFiscal()),
                calculationService.safeMoney(draft.getPaymentData().getCard()),
                calculationService.safeMoney(draft.getPaymentData().getOnlineCash()),
                calculationService.safeMoney(draft.getPaymentData().getOnlineCard()),
                calculationService.safeMoney(draft.getDeliveryAmount()),
                calculationService.safeMoney(draft.getCollectionAmount()),
                calculationService.safeMoney(draft.getCashInRegister()),
                calculationService.safeMoney(draft.getExpenses()),
                valueOrEmpty(draft.getNextEmployeeName())
        );

        reportRepository.saveReport(request);
        log.info("report_saved storeKey={}", draft.getLocationKey());
    }

    private String buildKey(Store store) {
        if (!store.name().isBlank() && !store.address().isBlank()) {
            return store.name() + " | " + store.address();
        }
        if (!store.name().isBlank()) {
            return store.name();
        }
        return store.address();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
