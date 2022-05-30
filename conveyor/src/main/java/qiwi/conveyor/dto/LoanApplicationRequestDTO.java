package qiwi.conveyor.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplicationRequestDTO {
    @DecimalMin("10000")
    private BigDecimal amount;
    @Min(6)
    private int term;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-z]+")
    private String firstName;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-z]+")
    private String lastName;
    private String middleName;
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    private String email;
    private LocalDate birthdate;
    @Pattern(regexp = "\\d{4}")
    private String passportSeries;
    @Pattern(regexp = "\\d{6}")
    private String passportNumber;
}
