package qiwi.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "loan_offers")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"application_id"})
public class LoanOffer {
    @Id
    @Column(name = "application_id", nullable = false)
    private Long applicationId;
    @Column(name = "requested_amount")
    private BigDecimal requestedAmount;
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    @Column(name = "term")
    private Integer term;
    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "is_insurance_enabled")
    private Boolean isInsuranceEnabled;
    @Column(name = "is_salary_client")
    private Boolean isSalaryClient;
}
