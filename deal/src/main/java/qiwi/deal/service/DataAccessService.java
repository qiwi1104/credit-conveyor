package qiwi.deal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qiwi.deal.entity.*;
import qiwi.deal.repository.*;

@Service
public class DataAccessService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PassportRepository passportRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ApplicationsStatusHistoryRepository applicationsStatusHistoryRepository;
    @Autowired
    private LoanOffersRepository loanOffersRepository;
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private PaymentsRepository paymentsRepository;
    @Autowired
    private EmploymentRepository employmentRepository;

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    public void saveApplication(Application application) {
        applicationRepository.save(application);
    }

    public void saveApplicationStatus(ApplicationStatusHistory applicationStatus) {
        applicationsStatusHistoryRepository.save(applicationStatus);
    }

    public void saveLoanOffer(LoanOffer loanOffer) {
        loanOffersRepository.save(loanOffer);
    }

    public void saveCredit(Credit credit) {
        creditRepository.save(credit);
    }

    public void savePassport(Passport passport) {
        passportRepository.save(passport);
    }

    public void savePaymentElement(qiwi.deal.entity.PaymentScheduleElement paymentScheduleElement) {
        paymentsRepository.save(paymentScheduleElement);
    }

    public void saveEmployment(Employment employment) {
        employmentRepository.save(employment);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id).get();
    }
}
