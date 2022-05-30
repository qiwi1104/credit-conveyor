package qiwi.conveyor.service;

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
public class ConveyorService {
    private final BigDecimal BASE_RATE = new BigDecimal(14).setScale(10, RoundingMode.HALF_UP);
    private final BigDecimal INSURANCE_COST = new BigDecimal(100000).setScale(10, RoundingMode.HALF_UP);

    private boolean isValidLoanApplicationRequest(LoanApplicationRequestDTO loanApplicationRequest) {
        boolean isValid = !loanApplicationRequest.getBirthdate().plusYears(18).isAfter(LocalDate.now());

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
            credit.setRate(credit.getRate().subtract(new BigDecimal(3)));
        }
        if (credit.isSalaryClient()) {
            credit.setRate(credit.getRate().subtract(BigDecimal.ONE));
        }

        switch (scoringData.getEmployment().getPosition()) {
            case MANAGER:
                credit.setRate(credit.getRate().subtract(BigDecimal.ONE));
                break;
            case TOP_MANAGER:
                credit.setRate(credit.getRate().subtract(new BigDecimal(4)));
                break;
        }

        switch (scoringData.getMaritalStatus()) {
            case SINGLE:
                credit.setRate(credit.getRate().add(BigDecimal.ONE));
                break;
            case MARRIED:
                credit.setRate(credit.getRate().subtract(BigDecimal.ONE));
                break;
        }

        if (scoringData.getDependentAmount() > 1) {
            credit.setRate(credit.getRate().add(BigDecimal.ONE));
        }

        switch (scoringData.getGender()) {
            case MALE:
                if (scoringData.getBirthdate().plusYears(30).isAfter(LocalDate.now())
                        || scoringData.getBirthdate().plusYears(55).isBefore(LocalDate.now())) {
                    credit.setRate(credit.getRate().subtract(new BigDecimal(2)));
                }
                break;
            case FEMALE:
                if (scoringData.getBirthdate().plusYears(35).isAfter(LocalDate.now())
                        || scoringData.getBirthdate().plusYears(60).isBefore(LocalDate.now())) {
                    credit.setRate(credit.getRate().subtract(new BigDecimal(2)));
                }
                break;
            case NON_BINARY:
                credit.setRate(credit.getRate().add(BigDecimal.ONE));
                break;
        }
    }

    private boolean isValidScoringData(ScoringDataDTO scoringData) {
        boolean isValid = true;
        EmploymentDTO employment = scoringData.getEmployment();

        if (employment.getEmploymentStatus().equals(UNEMPLOYED)
                || scoringData.getAmount().compareTo(employment.getSalary().multiply(new BigDecimal(20))) == 1
                || scoringData.getBirthdate().plusYears(20).isAfter(LocalDate.now())
                || scoringData.getBirthdate().plusYears(60).isBefore(LocalDate.now())) {
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
        BigDecimal monthlyInterest = credit.getRate()
                .divide(BigDecimal.valueOf(100 * 12), RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = calculateMonthlyPayment(credit.getAmount(), credit.getRate(), credit.getTerm());

        credit.setMonthlyPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));

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
        }

        credit.setPsk(psk.setScale(2, RoundingMode.HALF_UP));
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

        return loanOffer;
    }

    private List<LoanOfferDTO> generateLoanOffers(LoanApplicationRequestDTO loanApplicationRequest) {
        LoanOfferDTO loanOffer1 = generateSingleLoanOffer(loanApplicationRequest,
                false, false, BASE_RATE.setScale(2, RoundingMode.HALF_UP));

        LoanOfferDTO loanOffer2 = generateSingleLoanOffer(loanApplicationRequest,
                false, true, BASE_RATE.subtract(BigDecimal.ONE)
                        .setScale(2, RoundingMode.HALF_UP));

        LoanOfferDTO loanOffer3 = generateSingleLoanOffer(loanApplicationRequest,
                true, false, BASE_RATE.subtract(new BigDecimal(3))
                        .setScale(2, RoundingMode.HALF_UP));

        LoanOfferDTO loanOffer4 = generateSingleLoanOffer(loanApplicationRequest,
                true, true, BASE_RATE.subtract(new BigDecimal(4))
                        .setScale(2, RoundingMode.HALF_UP));

        List<LoanOfferDTO> loanOffers = Arrays.asList(loanOffer1, loanOffer2, loanOffer3, loanOffer4);
        loanOffers.sort(Comparator.comparing(LoanOfferDTO::getRate));
        Collections.reverse(loanOffers);
        return loanOffers;
    }

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanApplicationRequest,
                                            BindingResult result) {
        boolean isValid = !result.hasErrors() && isValidLoanApplicationRequest(loanApplicationRequest);

        return isValid
                ? generateLoanOffers(loanApplicationRequest)
                : List.of(new LoanOfferDTO(), new LoanOfferDTO(), new LoanOfferDTO(), new LoanOfferDTO());
    }

    public CreditDTO getCredit(ScoringDataDTO scoringData, BindingResult result) {
        if (result.hasErrors() || !isValidScoringData(scoringData)) {
            return new CreditDTO();
        }

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
