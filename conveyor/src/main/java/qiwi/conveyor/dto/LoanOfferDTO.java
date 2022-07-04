package qiwi.conveyor.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDTO {
    private Long applicationId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}
