package qiwi.conveyor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreditDTO {
    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
    private List<PaymentScheduleElement> paymentSchedule;
}
