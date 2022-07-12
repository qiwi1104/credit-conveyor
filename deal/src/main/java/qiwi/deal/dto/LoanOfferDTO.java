package qiwi.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDTO {
    @Schema(example = "1")
    private Long applicationId;
    @Schema(example = "800000")
    private BigDecimal requestedAmount;
    @Schema(example = "912803.04")
    private BigDecimal totalAmount;
    @Schema(example = "24")
    private Integer term;
    @Schema(example = "38033.46")
    private BigDecimal monthlyPayment;
    @Schema(example = "13.00")
    private BigDecimal rate;
    @Schema(example = "false")
    private Boolean isInsuranceEnabled;
    @Schema(example = "true")
    private Boolean isSalaryClient;
}
