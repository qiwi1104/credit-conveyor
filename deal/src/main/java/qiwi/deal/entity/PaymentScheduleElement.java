package qiwi.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "credit")
@JsonIgnoreProperties({"id", "credit_id"})
public class PaymentScheduleElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "number")
    private Integer number;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "total_payment")
    private BigDecimal totalPayment;
    @Column(name = "interest_payment")
    private BigDecimal interestPayment;
    @Column(name = "debt_payment")
    private BigDecimal debtPayment;
    @Column(name = "remaining_debt")
    private BigDecimal remainingDebt;
    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;
}
