package qiwi.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import qiwi.application.client.DealClient;
import qiwi.application.dto.LoanApplicationRequestDTO;
import qiwi.application.dto.LoanOfferDTO;
import qiwi.application.exceptions.InvalidLoanApplicationRequestException;
import qiwi.application.exceptions.InvalidScoringDataException;
import qiwi.application.validators.LoanApplicationRequestValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApplicationService {
    private final int MIN_NAME_LENGTH = 2;
    private final int MAX_NAME_LENGTH = 30;

    @Autowired
    private DealClient client;

    private boolean isValidMiddleName(String middleName) {
        if (middleName != null) {
            return middleName.matches("[A-Za-z]+")
                    && middleName.length() >= MIN_NAME_LENGTH
                    && middleName.length() <= MAX_NAME_LENGTH;
        }

        return true;
    }

    private void isErrorsPresent(LoanApplicationRequestDTO loanApplicationRequestDTO, BindingResult result) {
        List<String> fields = result.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());

        if (!isValidMiddleName(loanApplicationRequestDTO.getMiddleName())) {
            fields.add("middleName");
        }

        if (!fields.isEmpty()) {
            log.trace("Error on fields: {}", fields);

            throw new InvalidLoanApplicationRequestException("Error on fields: "
                    + Arrays.toString(fields.toArray()));
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

    private boolean prescoringPassed(LoanApplicationRequestDTO loanApplicationRequest, BindingResult result) {
        LoanApplicationRequestValidator applicationRequestValidator = new LoanApplicationRequestValidator();
        applicationRequestValidator.validate(loanApplicationRequest, result);

        return !result.hasErrors();
    }

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequest, BindingResult result) {
        log.trace("Received loan application request: {}.", loanApplicationRequest);

        isErrorsPresent(loanApplicationRequest, result);

        if (prescoringPassed(loanApplicationRequest, result)) {
            log.trace("Loan application request has passed pre-scoring.");
            return client.getOffers(loanApplicationRequest);
        } else {
            log.trace("Loan application request has not passed pre-scoring.");
            return new ArrayList<>();
        }

    }

    public void chooseOffer(LoanOfferDTO loanOffer) {
        log.trace("Received loan offer: {}", loanOffer);

        isErrorsPresent(loanOffer);

        client.chooseOffer(loanOffer);
    }
}
