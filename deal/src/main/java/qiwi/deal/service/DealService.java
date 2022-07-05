package qiwi.deal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import qiwi.conveyor.dto.*;
import qiwi.conveyor.exceptions.InvalidLoanApplicationRequestException;
import qiwi.conveyor.exceptions.InvalidScoringDataException;
import qiwi.deal.client.ConveyorClient;
import qiwi.deal.dto.*;
import qiwi.deal.entity.*;
import qiwi.deal.entity.PaymentScheduleElement;
import qiwi.deal.enums.Status;
import qiwi.deal.exceptions.InvalidFinishRegistrationRequestException;
import qiwi.deal.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static qiwi.deal.enums.ChangeType.AUTOMATIC;
import static qiwi.deal.enums.CreditStatus.CALCULATED;
import static qiwi.deal.enums.Status.*;

@Service
@Slf4j
public class DealService {
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
    @Autowired
    private ConveyorClient conveyorProxy;

    private <T> void isErrorsPresent(T dtoRequest,
                                     BindingResult result) {
        List<String> fields = result.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());

        if (!fields.isEmpty()) {
            log.trace("Error on fields: {}", fields);

            String message = "Error on fields: " + Arrays.toString(fields.toArray());

            if (dtoRequest instanceof LoanApplicationRequestDTO) {
                throw new InvalidLoanApplicationRequestException(message);
            }
            if (dtoRequest instanceof FinishRegistrationRequestDTO) {
                throw new InvalidFinishRegistrationRequestException(message);
            }
        }
    }

    private void isErrorsPresent(LoanOfferDTO loanOffer) {
        List<String> fields = new ArrayList<>();

        if (loanOffer.getApplicationId() == null) {
            fields.add("applicationId");
        }
        if (loanOffer.getRequestedAmount() == null) {
            fields.add("requestAmount");
        }
        if (loanOffer.getTotalAmount() == null) {
            fields.add("totalAmount");
        }
        if (loanOffer.getTerm() == null) {
            fields.add("term");
        }
        if (loanOffer.getMonthlyPayment() == null) {
            fields.add("monthlyPayment");
        }
        if (loanOffer.getRate() == null) {
            fields.add("rate");
        }
        if (loanOffer.getIsInsuranceEnabled() == null) {
            fields.add("isInsuranceEnabled");
        }
        if (loanOffer.getIsSalaryClient() == null) {
            fields.add("isSalaryClient");
        }
        if (!fields.isEmpty()) {
            log.trace("Error on fields: {}", fields);
            String message = "Error on fields: " + Arrays.toString(fields.toArray());

            throw new InvalidScoringDataException(message);
        }
    }

    private void updateApplicationStatus(Application application, Status status) {
        ApplicationStatusHistory applicationStatus = ApplicationStatusHistory.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(AUTOMATIC)
                .application(application)
                .build();

        saveApplicationStatus(applicationStatus);

        application.setStatus(status);
        application.getApplicationStatusHistory().add(applicationStatus);

        saveApplication(application);

        log.trace("Application status history updated: {}", application.getApplicationStatusHistory());
        log.trace("Application updated: {}", application);
    }

    private void updateClient(Client client, ScoringDataDTO scoringData) {
        Passport passport = client.getPassport();
        passport.setIssueDate(scoringData.getPassportIssueDate());
        passport.setIssueBranch(scoringData.getPassportIssueBranch());
        savePassport(passport);

        log.trace("Passport updated: {}", passport);

        client.setGender(scoringData.getGender());
        client.setMaritalStatus(scoringData.getMaritalStatus());
        client.setDependentAmount(scoringData.getDependentAmount());

        client.setAccount(scoringData.getAccount());

        Employment employment = createEmployment(scoringData.getEmployment());
        client.setEmployment(employment);

        saveClient(client);

        log.trace("Client updated: {}", client);
    }

    private void setIdToLoanOffers(List<LoanOfferDTO> loanOffers, Long id) {
        loanOffers.forEach(loanOffer -> loanOffer.setApplicationId(id));

        log.trace("Loan offers ids updated: {}", loanOffers);
    }

    private void setCreditToPaymentScheduleElements(
            List<qiwi.deal.entity.PaymentScheduleElement> paymentScheduleElements,
            Credit credit) {

        for (qiwi.deal.entity.PaymentScheduleElement paymentScheduleElement : paymentScheduleElements) {
            paymentScheduleElement.setCredit(credit);
            savePaymentElement(paymentScheduleElement);
        }
    }

    private void setAppliedOffer(Application application, LoanOffer loanOffer) {
        application.setAppliedOffer(loanOffer);

        log.trace("Applied offer set: {}", loanOffer);

        saveApplication(application);

        log.trace("Application updated: {}", application);
    }

    private Application createApplication(Client client) {
        Application application = Application.builder()
                .client(client)
                .creationDate(LocalDate.now())
                .applicationStatusHistory(new ArrayList<>())
                .build();

        saveApplication(application);

        log.trace("Application created: {}", application);

        return application;
    }

    private LoanOffer createLoanOffer(LoanOfferDTO loanOfferDTO) {
        LoanOffer loanOffer = LoanOffer.builder()
                .applicationId(loanOfferDTO.getApplicationId())
                .requestedAmount(loanOfferDTO.getRequestedAmount())
                .totalAmount(loanOfferDTO.getTotalAmount())
                .term(loanOfferDTO.getTerm())
                .monthlyPayment(loanOfferDTO.getMonthlyPayment())
                .rate(loanOfferDTO.getRate())
                .isInsuranceEnabled(loanOfferDTO.getIsInsuranceEnabled())
                .isSalaryClient(loanOfferDTO.getIsSalaryClient())
                .build();

        saveLoanOffer(loanOffer);

        return loanOffer;
    }

    private Passport createPassport(LoanApplicationRequestDTO loanApplicationRequest) {
        Passport passport = new Passport();
        passport.setSeries(loanApplicationRequest.getPassportSeries());
        passport.setNumber(loanApplicationRequest.getPassportNumber());

        savePassport(passport);

        return passport;
    }

    private Employment createEmployment(EmploymentDTO employmentDTO) {
        Employment employment = new Employment();

        employment.setEmploymentStatus(employmentDTO.getEmploymentStatus());
        employment.setEmployerINN(employmentDTO.getEmployerINN());
        employment.setSalary(employmentDTO.getSalary());
        employment.setPosition(employmentDTO.getPosition());
        employment.setWorkExperienceTotal(employmentDTO.getWorkExperienceTotal());
        employment.setWorkExperienceCurrent(employmentDTO.getWorkExperienceCurrent());

        saveEmployment(employment);

        return employment;
    }

    private Client createClient(LoanApplicationRequestDTO loanApplicationRequest) {
        Passport passport = createPassport(loanApplicationRequest);

        Client client = Client.builder()
                .lastName(loanApplicationRequest.getLastName())
                .firstName(loanApplicationRequest.getFirstName())
                .middleName(loanApplicationRequest.getMiddleName())
                .birthDate(loanApplicationRequest.getBirthdate())
                .email(loanApplicationRequest.getEmail())
                .passport(passport)
                .build();

        saveClient(client);

        log.trace("Client created: {}", client);

        return client;
    }

    private ScoringDataDTO createScoringData(Client client, LoanOffer appliedOffer, FinishRegistrationRequestDTO finishRegistrationRequest) {
        ScoringDataDTO scoringData = new ScoringDataDTO();
        scoringData.setAmount(appliedOffer.getRequestedAmount());
        scoringData.setTerm(appliedOffer.getTerm());

        scoringData.setFirstName(client.getFirstName());
        scoringData.setLastName(client.getLastName());
        scoringData.setMiddleName(client.getMiddleName());
        scoringData.setGender(finishRegistrationRequest.getGender());
        scoringData.setBirthdate(client.getBirthDate());

        scoringData.setPassportSeries(client.getPassport().getSeries());
        scoringData.setPassportNumber(client.getPassport().getNumber());
        scoringData.setPassportIssueDate(finishRegistrationRequest.getPassportIssueDate());
        scoringData.setPassportIssueBranch(finishRegistrationRequest.getPassportIssueBranch());

        scoringData.setMaritalStatus(finishRegistrationRequest.getMaritalStatus());
        scoringData.setDependentAmount(finishRegistrationRequest.getDependentAmount());
        scoringData.setEmployment(finishRegistrationRequest.getEmployment());
        scoringData.setAccount(finishRegistrationRequest.getAccount());
        scoringData.setIsInsuranceEnabled(appliedOffer.getIsInsuranceEnabled());
        scoringData.setIsSalaryClient(appliedOffer.getIsSalaryClient());

        scoringData.setEmployment(finishRegistrationRequest.getEmployment());

        return scoringData;
    }

    private List<PaymentScheduleElement> createPaymentSchedule(List<qiwi.conveyor.dto.PaymentScheduleElement> paymentScheduleElementsDTO) {
        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();

        for (qiwi.conveyor.dto.PaymentScheduleElement paymentScheduleElementDTO : paymentScheduleElementsDTO) {
            PaymentScheduleElement paymentScheduleElement = PaymentScheduleElement.builder()
                    .number(paymentScheduleElementDTO.getNumber())
                    .date(paymentScheduleElementDTO.getDate())
                    .totalPayment(paymentScheduleElementDTO.getTotalPayment())
                    .interestPayment(paymentScheduleElementDTO.getInterestPayment())
                    .debtPayment(paymentScheduleElementDTO.getDebtPayment())
                    .remainingDebt(paymentScheduleElementDTO.getRemainingDebt())
                    .build();

            paymentScheduleElements.add(paymentScheduleElement);
        }

        return paymentScheduleElements;
    }

    private Credit createCredit(CreditDTO creditDTO) {
        Credit credit = Credit.builder()
                .amount(creditDTO.getAmount())
                .term(creditDTO.getTerm())
                .monthlyPayment(creditDTO.getMonthlyPayment())
                .rate(creditDTO.getRate())
                .psk(creditDTO.getPsk())
                .isInsuranceEnabled(creditDTO.getIsInsuranceEnabled())
                .isSalaryClient(creditDTO.getIsSalaryClient())
                .creditStatus(CALCULATED)
                .build();

        saveCredit(credit);

        List<PaymentScheduleElement> paymentScheduleElements = createPaymentSchedule(
                creditDTO.getPaymentSchedule());

        setCreditToPaymentScheduleElements(paymentScheduleElements, credit);
        credit.setPaymentSchedule(paymentScheduleElements);

        log.trace("Payment schedule created: {}", paymentScheduleElements);

        saveCredit(credit);

        log.trace("Credit created: {}", credit);

        return credit;
    }

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

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequest,
                                            BindingResult result) {
        isErrorsPresent(loanApplicationRequest, result);

        log.trace("Loan application request received: {}", loanApplicationRequest);

        List<LoanOfferDTO> loanOffers = conveyorProxy.getOffers(loanApplicationRequest);
        log.trace("Loan offers received: {}", loanOffers);

        Client client = createClient(loanApplicationRequest);

        Application application = createApplication(client);
        updateApplicationStatus(application, PREAPPROVAL);

        setIdToLoanOffers(loanOffers, application.getId());

        return loanOffers;
    }

    public void chooseOffer(LoanOfferDTO loanOfferDTO) {
        isErrorsPresent(loanOfferDTO);

        Application application = getApplicationById(loanOfferDTO.getApplicationId());

        log.trace("Application retrieved by id = {}: {}",
                loanOfferDTO.getApplicationId(), application);

        updateApplicationStatus(application, APPROVED);

        LoanOffer loanOffer = createLoanOffer(loanOfferDTO);
        setAppliedOffer(application, loanOffer);
    }

    public void finishRegistration(FinishRegistrationRequestDTO finishRegistrationRequest,
                                   BindingResult result, Long id) {
        isErrorsPresent(finishRegistrationRequest, result);

        Application application = getApplicationById(id);

        log.trace("Application retrieved by id = {}: {}", id, application);

        ScoringDataDTO scoringData = createScoringData(
                application.getClient(), application.getAppliedOffer(), finishRegistrationRequest);

        CreditDTO creditDTO = conveyorProxy.getCredit(scoringData);

        log.trace("CreditDTO retrieved: {}", creditDTO);

        Credit credit = createCredit(creditDTO);

        application.setCredit(credit);

        updateApplicationStatus(application, CC_APPROVED);

        updateClient(application.getClient(), scoringData);
    }
}
