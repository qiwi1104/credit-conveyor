package qiwi.application.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import qiwi.application.dto.EmploymentDTO;
import qiwi.application.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import static qiwi.application.enums.EmploymentStatus.UNEMPLOYED;

public class ScoringDataValidator implements Validator {
    private final BigDecimal MIN_AMOUNT = new BigDecimal("10000");
    private final int MIN_TERM = 6;
    private final int MIN_EXPERIENCE_TOTAL = 12;
    private final int MIN_EXPERIENCE_CURRENT = 3;
    private final int MIN_ELIGIBLE_AGE = 20;
    private final int MAX_ELIGIBLE_AGE = 60;
    private final BigDecimal MAX_ALLOWED_TIMES_AMOUNT_GREATER_THAN_SALARY = new BigDecimal("20");

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ScoringDataDTO scoringData = (ScoringDataDTO) target;

        if (scoringData.getAmount().compareTo(MIN_AMOUNT) < 0) {
            errors.reject("amount");
        }
        if (scoringData.getTerm() < MIN_TERM) {
            errors.reject("term");
        }

        EmploymentDTO employment = scoringData.getEmployment();

        if (employment.getWorkExperienceTotal() < MIN_EXPERIENCE_TOTAL) {
            errors.reject("workExperienceTotal");
        }
        if (employment.getWorkExperienceCurrent() < MIN_EXPERIENCE_CURRENT) {
            errors.reject("workExperienceCurrent");
        }

        if (employment.getEmploymentStatus().equals(UNEMPLOYED)) {
            errors.reject("employmentStatus");
        }
        if (scoringData.getAmount().compareTo(
                employment.getSalary().multiply(MAX_ALLOWED_TIMES_AMOUNT_GREATER_THAN_SALARY)) > 0) {
            errors.reject("salary");
        }
        if (scoringData.getBirthdate().plusYears(MIN_ELIGIBLE_AGE).isAfter(LocalDate.now())
                || !scoringData.getBirthdate().plusYears(MAX_ELIGIBLE_AGE).isAfter(LocalDate.now())) {
            errors.reject("birthDate");
        }
    }
}
