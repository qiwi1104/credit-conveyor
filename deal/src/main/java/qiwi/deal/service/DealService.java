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
import qiwi.deal.dto.FinishRegistrationRequestDTO;
import qiwi.deal.entity.PaymentScheduleElement;
import qiwi.deal.entity.*;
import qiwi.deal.enums.Status;
import qiwi.deal.exceptions.InvalidFinishRegistrationRequestException;
import qiwi.deal.mapper.CreditMapper;
import qiwi.deal.mapper.EmploymentMapper;
import qiwi.deal.mapper.LoanOfferMapper;
import qiwi.deal.mapper.PaymentScheduleElementMapper;

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
    private DataAccessService dataAccessService;
    @Autowired
    private ConveyorClient conveyorProxy;
    @Autowired
    private LoanOfferMapper loanOfferMapper;
    @Autowired
    private EmploymentMapper employmentMapper;
    @Autowired
    private PaymentScheduleElementMapper paymentScheduleElementMapper;
    @Autowired
    private CreditMapper creditMapper;


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

        dataAccessService.saveApplicationStatus(applicationStatus);

        application.setStatus(status);
        application.getApplicationStatusHistory().add(applicationStatus);

        dataAccessService.saveApplication(application);

        log.trace("Application status history updated: {}", application.getApplicationStatusHistory());
        log.trace("Application updated: {}", application);
    }

    private void updateClient(Client client, ScoringDataDTO scoringData) {
        Passport passport = client.getPassport();
        passport.setIssueDate(scoringData.getPassportIssueDate());
        passport.setIssueBranch(scoringData.getPassportIssueBranch());
        dataAccessService.savePassport(passport);

        log.trace("Passport updated: {}", passport);

        client.setGender(scoringData.getGender());
        client.setMaritalStatus(scoringData.getMaritalStatus());
        client.setDependentAmount(scoringData.getDependentAmount());

        client.setAccount(scoringData.getAccount());

        Employment employment = createEmployment(scoringData.getEmployment());
        client.setEmployment(employment);

        dataAccessService.saveClient(client);

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
            dataAccessService.savePaymentElement(paymentScheduleElement);
        }
    }

    private void setAppliedOffer(Application application, LoanOffer loanOffer) {
        application.setAppliedOffer(loanOffer);

        log.trace("Applied offer set: {}", loanOffer);

        dataAccessService.saveApplication(application);

        log.trace("Application updated: {}", application);
    }

    private Application createApplication(Client client) {
        Application application = Application.builder()
                .client(client)
                .creationDate(LocalDate.now())
                .applicationStatusHistory(new ArrayList<>())
                .build();

        dataAccessService.saveApplication(application);

        log.trace("Application created: {}", application);

        return application;
    }

    private LoanOffer createLoanOffer(LoanOfferDTO loanOfferDTO) {
        LoanOffer loanOffer = loanOfferMapper.mapToEntity(loanOfferDTO);

        dataAccessService.saveLoanOffer(loanOffer);

        return loanOffer;
    }

    private Passport createPassport(LoanApplicationRequestDTO loanApplicationRequest) {
        Passport passport = new Passport();
        passport.setSeries(loanApplicationRequest.getPassportSeries());
        passport.setNumber(loanApplicationRequest.getPassportNumber());

        dataAccessService.savePassport(passport);

        return passport;
    }

    private Employment createEmployment(EmploymentDTO employmentDTO) {
        Employment employment = employmentMapper.mapToEntity(employmentDTO);

        dataAccessService.saveEmployment(employment);

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

        dataAccessService.saveClient(client);

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
            PaymentScheduleElement paymentScheduleElement = paymentScheduleElementMapper
                    .mapToEntity(paymentScheduleElementDTO);

            paymentScheduleElements.add(paymentScheduleElement);
        }

        return paymentScheduleElements;
    }

    private Credit createCredit(CreditDTO creditDTO) {
        Credit credit = creditMapper.mapToEntity(creditDTO);
        credit.setCreditStatus(CALCULATED);

        dataAccessService.saveCredit(credit);

        List<PaymentScheduleElement> paymentScheduleElements = createPaymentSchedule(
                creditDTO.getPaymentSchedule());

        setCreditToPaymentScheduleElements(paymentScheduleElements, credit);
        credit.setPaymentSchedule(paymentScheduleElements);

        log.trace("Payment schedule created: {}", paymentScheduleElements);

        dataAccessService.saveCredit(credit);

        log.trace("Credit created: {}", credit);

        return credit;
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

        setIdToLoanOffers(loanOffers, application.getApplicationId());

        return loanOffers;
    }

    public void chooseOffer(LoanOfferDTO loanOfferDTO) {
        isErrorsPresent(loanOfferDTO);

        Application application = dataAccessService.getApplicationById(loanOfferDTO.getApplicationId());

        log.trace("Application retrieved by id = {}: {}",
                loanOfferDTO.getApplicationId(), application);

        updateApplicationStatus(application, APPROVED);

        LoanOffer loanOffer = createLoanOffer(loanOfferDTO);
        setAppliedOffer(application, loanOffer);
    }

    public void finishRegistration(FinishRegistrationRequestDTO finishRegistrationRequest,
                                   BindingResult result, Long id) {
        isErrorsPresent(finishRegistrationRequest, result);

        Application application = dataAccessService.getApplicationById(id);

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
