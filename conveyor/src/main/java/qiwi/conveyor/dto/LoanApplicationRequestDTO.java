package qiwi.conveyor.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
public class LoanApplicationRequestDTO {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer term;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-zА-Яа-я]+")
    @NotNull
    private String firstName;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-zА-Яа-я]+")
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
