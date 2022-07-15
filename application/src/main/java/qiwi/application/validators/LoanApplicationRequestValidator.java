package qiwi.application.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import qiwi.application.dto.LoanApplicationRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanApplicationRequestValidator implements Validator {
    private final BigDecimal MIN_AMOUNT = new BigDecimal("10000");
    private final int MIN_TERM = 6;
    private final int MIN_ELIGIBLE_AGE = 20;
    private final int MAX_ELIGIBLE_AGE = 60;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoanApplicationRequestDTO loanApplicationRequest = (LoanApplicationRequestDTO) target;

        if (loanApplicationRequest.getAmount().compareTo(MIN_AMOUNT) < 0) {
            errors.reject("amount");
        }
        if (loanApplicationRequest.getTerm() < MIN_TERM) {
            errors.reject("term");
        }
        if (loanApplicationRequest.getBirthdate().plusYears(MIN_ELIGIBLE_AGE).isAfter(LocalDate.now())
                || !loanApplicationRequest.getBirthdate().plusYears(MAX_ELIGIBLE_AGE).isAfter(LocalDate.now())) {
            errors.reject("birthDate");
        }
    }
}
