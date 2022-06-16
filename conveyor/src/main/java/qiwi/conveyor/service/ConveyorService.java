package qiwi.conveyor.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import qiwi.conveyor.dto.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static qiwi.conveyor.enums.EmploymentStatus.UNEMPLOYED;

@Service
@Log4j2
public class ConveyorService {
    private final BigDecimal BASE_RATE = new BigDecimal(14).setScale(10, RoundingMode.HALF_UP);
    private final BigDecimal INSURANCE_COST = new BigDecimal(100000).setScale(10, RoundingMode.HALF_UP);

    private final BigDecimal CHANGE_IF_INSURANCE_ENABLED = new BigDecimal("3");
    private final BigDecimal CHANGE_IF_SALARY_CLIENT = BigDecimal.ONE;
    private final BigDecimal CHANGE_IF_MANAGER = BigDecimal.ONE;
    private final BigDecimal CHANGE_IF_TOP_MANAGER = new BigDecimal("4");
    private final BigDecimal CHANGE_IF_SINGLE = BigDecimal.ONE;
    private final BigDecimal CHANGE_IF_MARRIED = BigDecimal.ONE;
    private final BigDecimal CHANGE_IF_MANY_DEPENDENTS = BigDecimal.ONE;
    private final BigDecimal CHANGE_IF_GROWN_ENOUGH = new BigDecimal("2");
    private final BigDecimal CHANGE_IF_NON_BINARY = BigDecimal.ONE;

    private final int MAX_DEPENDENT_AMOUNT_WITHOUT_RATE_INCREASE = 1;
    private final int MIN_MALE_AGE_ELIGIBLE_FOR_RATE_DECREASE = 30;
    private final int MAX_MALE_AGE_ELIGIBLE_FOR_RATE_DECREASE = 55;
    private final int MIN_FEMALE_AGE_ELIGIBLE_FOR_RATE_DECREASE = 35;
    private final int MAX_FEMALE_AGE_ELIGIBLE_FOR_RATE_DECREASE = 60;
    private final int MIN_ELIGIBLE_AGE = 20;
    private final int MAX_ELIGIBLE_AGE = 60;
    private final BigDecimal MAX_ALLOWED_TIMES_AMOUNT_GREATER_THAN_SALARY = new BigDecimal("20");

    private boolean isValidLoanApplicationRequest(LoanApplicationRequestDTO loanApplicationRequest) {
        boolean isValid = !loanApplicationRequest.getBirthdate().plusYears(MIN_ELIGIBLE_AGE).isAfter(LocalDate.now());

        if (isValid) {
            if (loanApplicationRequest.getMiddleName() != null) {
                if (!loanApplicationRequest.getMiddleName().matches("[A-Za-z]+")
                        || loanApplicationRequest.getMiddleName().length() < 2
                        || loanApplicationRequest.getMiddleName().length() > 30) {
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        BigDecimal monthlyInterest = rate
                .divide(BigDecimal.valueOf(100 * 12), RoundingMode.HALF_UP)
                .setScale(10, RoundingMode.HALF_UP);
        BigDecimal temp = monthlyInterest
                .divide(BigDecimal.ONE
                        .subtract(monthlyInterest
                                .add(BigDecimal.ONE)
                                .pow(-term, new MathContext(10))), RoundingMode.HALF_UP)
                .setScale(10, RoundingMode.HALF_UP);

        return amount
                .multiply(temp)
                .setScale(10, RoundingMode.HALF_UP);
    }

    private void calculateRate(CreditDTO credit, ScoringDataDTO scoringData) {
        credit.setRate(BASE_RATE);

        if (credit.isInsuranceEnabled()) {
            credit.setRate(credit.getRate().subtract(CHANGE_IF_INSURANCE_ENABLED));
        }
        if (credit.isSalaryClient()) {
            credit.setRate(credit.getRate().subtract(CHANGE_IF_SALARY_CLIENT));
        }

        switch (scoringData.getEmployment().getPosition()) {
            case MANAGER:
                credit.setRate(credit.getRate().subtract(CHANGE_IF_MANAGER));
                break;
            case TOP_MANAGER:
                credit.setRate(credit.getRate().subtract(CHANGE_IF_TOP_MANAGER));
                break;
        }

        switch (scoringData.getMaritalStatus()) {
            case SINGLE:
                credit.setRate(credit.getRate().add(CHANGE_IF_SINGLE));
                break;
            case MARRIED:
                credit.setRate(credit.getRate().subtract(CHANGE_IF_MARRIED));
                break;
        }

        if (scoringData.getDependentAmount() > MAX_DEPENDENT_AMOUNT_WITHOUT_RATE_INCREASE) {
            credit.setRate(credit.getRate().add(CHANGE_IF_MANY_DEPENDENTS));
        }

        switch (scoringData.getGender()) {
            case MALE:
                if (scoringData.getBirthdate()
                        .plusYears(MIN_MALE_AGE_ELIGIBLE_FOR_RATE_DECREASE)
                        .isAfter(LocalDate.now())
                        || scoringData.getBirthdate()
                        .plusYears(MAX_MALE_AGE_ELIGIBLE_FOR_RATE_DECREASE)
                        .isBefore(LocalDate.now())) {
                    credit.setRate(credit.getRate().subtract(CHANGE_IF_GROWN_ENOUGH));
                }
                break;
            case FEMALE:
                if (scoringData.getBirthdate()
                        .plusYears(MIN_FEMALE_AGE_ELIGIBLE_FOR_RATE_DECREASE)
                        .isAfter(LocalDate.now())
                        || scoringData.getBirthdate()
                        .plusYears(MAX_FEMALE_AGE_ELIGIBLE_FOR_RATE_DECREASE)
                        .isBefore(LocalDate.now())) {
                    credit.setRate(credit.getRate().subtract(CHANGE_IF_GROWN_ENOUGH));
                }
                break;
            case NON_BINARY:
                credit.setRate(credit.getRate().add(CHANGE_IF_NON_BINARY));
                break;
        }

        log.trace("Credit rate={}", credit.getRate());
    }

    private boolean isValidScoringData(ScoringDataDTO scoringData) {
        boolean isValid = true;
        EmploymentDTO employment = scoringData.getEmployment();

        if (employment.getEmploymentStatus().equals(UNEMPLOYED)
                || scoringData.getAmount().compareTo(
                employment.getSalary().multiply(MAX_ALLOWED_TIMES_AMOUNT_GREATER_THAN_SALARY)) == 1
                || scoringData.getBirthdate().plusYears(MIN_ELIGIBLE_AGE).isAfter(LocalDate.now())
                || scoringData.getBirthdate().plusYears(MAX_ELIGIBLE_AGE).isBefore(LocalDate.now())) {
            isValid = false;
        }

        if (isValid) {
            if (scoringData.getMiddleName() != null) {
                if (!scoringData.getMiddleName().matches("[A-Za-z]+")
                        || scoringData.getMiddleName().length() < 2
                        || scoringData.getMiddleName().length() > 30) {
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    private void calculatePayments(CreditDTO credit) {
        log.trace("Calculating payment schedule.");

        BigDecimal monthlyInterest = credit.getRate()
                .divide(BigDecimal.valueOf(100 * 12), RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = calculateMonthlyPayment(credit.getAmount(), credit.getRate(), credit.getTerm());

        credit.setMonthlyPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));

        log.trace("Monthly payment={}", credit.getMonthlyPayment());

        BigDecimal remainingDebt = credit.getAmount();
        BigDecimal psk = BigDecimal.ZERO;

        credit.setPaymentSchedule(new ArrayList<>());

        for (int i = 0; i < credit.getTerm(); i++) {
            BigDecimal interestPayment = remainingDebt.multiply(monthlyInterest);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);

            remainingDebt = remainingDebt.subtract(debtPayment);

            PaymentScheduleElement payment = new PaymentScheduleElement();

            payment.setNumber(i + 1);
            payment.setDate(LocalDate.now().plusMonths(i));
            payment.setTotalPayment(interestPayment.add(debtPayment).setScale(2, RoundingMode.HALF_UP));
            payment.setInterestPayment(interestPayment.setScale(2, RoundingMode.HALF_UP));
            payment.setDebtPayment(debtPayment.setScale(2, RoundingMode.HALF_UP));
            payment.setRemainingDebt(remainingDebt.setScale(2, RoundingMode.HALF_UP));

            credit.getPaymentSchedule().add(payment);

            psk = psk.add(payment.getTotalPayment());

            log.trace(payment);
        }

        credit.setPsk(psk.setScale(2, RoundingMode.HALF_UP));

        log.trace("psk={}", credit.getPsk());
    }

    private LoanOfferDTO generateSingleLoanOffer(LoanApplicationRequestDTO loanApplicationRequest,
                                                 boolean isInsuranceEnabled, boolean isSalaryClient, BigDecimal rate) {
        LoanOfferDTO loanOffer = new LoanOfferDTO();

        if (isInsuranceEnabled) {
            loanOffer.setRequestedAmount(
                    loanApplicationRequest.getAmount()
                            .add(INSURANCE_COST)
                            .setScale(2, RoundingMode.HALF_UP));
        } else {
            loanOffer.setRequestedAmount(loanApplicationRequest.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        loanOffer.setTerm(loanApplicationRequest.getTerm());
        loanOffer.setInsuranceEnabled(isInsuranceEnabled);
        loanOffer.setSalaryClient(isSalaryClient);
        loanOffer.setRate(rate.setScale(2, RoundingMode.HALF_UP));
        loanOffer.setMonthlyPayment(calculateMonthlyPayment(
                loanOffer.getRequestedAmount(),
                loanOffer.getRate().setScale(10, RoundingMode.HALF_UP),
                loanOffer.getTerm())
                .setScale(2, RoundingMode.HALF_UP));
        loanOffer.setTotalAmount(loanOffer.getMonthlyPayment()
                .multiply(new BigDecimal(loanOffer.getTerm()))
                .setScale(2, RoundingMode.HALF_UP));

        log.trace("Loan offer generated: {}.", loanOffer);
        return loanOffer;
    }

    private List<LoanOfferDTO> generateLoanOffers(LoanApplicationRequestDTO loanApplicationRequest) {
        LoanOfferDTO loanOffer1 = generateSingleLoanOffer(loanApplicationRequest,
                false, false, BASE_RATE.setScale(2, RoundingMode.HALF_UP));

        LoanOfferDTO loanOffer2 = generateSingleLoanOffer(loanApplicationRequest,
                false, true, BASE_RATE.subtract(CHANGE_IF_SALARY_CLIENT)
                        .setScale(2, RoundingMode.HALF_UP));

        LoanOfferDTO loanOffer3 = generateSingleLoanOffer(loanApplicationRequest,
                true, false, BASE_RATE.subtract(CHANGE_IF_INSURANCE_ENABLED)
                        .setScale(2, RoundingMode.HALF_UP));

        LoanOfferDTO loanOffer4 = generateSingleLoanOffer(loanApplicationRequest,
                true, true, BASE_RATE.subtract(
                                CHANGE_IF_INSURANCE_ENABLED.add(CHANGE_IF_SALARY_CLIENT))
                        .setScale(2, RoundingMode.HALF_UP));

        List<LoanOfferDTO> loanOffers = Arrays.asList(loanOffer1, loanOffer2, loanOffer3, loanOffer4);
        loanOffers.sort(Comparator.comparing(LoanOfferDTO::getRate));
        Collections.reverse(loanOffers);
        return loanOffers;
    }

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequest,
                                            BindingResult result) {
        log.trace("Received loan application request: {}.", loanApplicationRequest);
        boolean isValid = !result.hasErrors() && isValidLoanApplicationRequest(loanApplicationRequest);

        if (isValid) {
            log.trace("Loan application request is valid.");
            return generateLoanOffers(loanApplicationRequest);
        } else {
            log.trace("Loan application request is not valid.");
            return List.of(new LoanOfferDTO(), new LoanOfferDTO(), new LoanOfferDTO(), new LoanOfferDTO());
        }
    }

    public CreditDTO getCredit(ScoringDataDTO scoringData, BindingResult result) {
        log.trace("Received scoring data: {}.", scoringData);
        if (result.hasErrors() || !isValidScoringData(scoringData)) {
            log.trace("Scoring data is not valid.");
            return new CreditDTO();
        }

        log.trace("Scoring data is valid.");

        CreditDTO credit = new CreditDTO();

        credit.setAmount(scoringData.getAmount());
        credit.setTerm(scoringData.getTerm());
        credit.setInsuranceEnabled(scoringData.isInsuranceEnabled());
        credit.setSalaryClient(scoringData.isSalaryClient());

        calculateRate(credit, scoringData);
        calculatePayments(credit);

        return credit;
    }
}
