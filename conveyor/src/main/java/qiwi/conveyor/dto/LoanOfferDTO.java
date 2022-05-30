package qiwi.conveyor.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanOfferDTO {
    private long applicationId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
}