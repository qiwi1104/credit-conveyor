package qiwi.conveyor.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplicationRequestDTO {
    @DecimalMin("10000")
    @NotNull
    private BigDecimal amount;
    @Min(6)
    @NotNull
    private int term;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-z]+")
    @NotNull
    private String firstName;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-z]+")
    @NotNull
    private String lastName;
    private String middleName;
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    @NotNull
    private String email;
    @NotNull
    private LocalDate birthdate;
    @Pattern(regexp = "\\d{4}")
    @NotNull
    private String passportSeries;
    @Pattern(regexp = "\\d{6}")
    @NotNull
    private String passportNumber;
}
