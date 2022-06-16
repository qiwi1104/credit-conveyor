package qiwi.conveyor.dto;

import lombok.Data;
import lombok.ToString;
import qiwi.conveyor.enums.Gender;
import qiwi.conveyor.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
public class ScoringDataDTO {
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
    @NotNull
    private Gender gender;
    @NotNull
    private LocalDate birthdate;
    @Pattern(regexp = "\\d{4}")
    @NotNull
    private String passportSeries;
    @Pattern(regexp = "\\d{6}")
    @NotNull
    private String passportNumber;
    @NotNull
    private LocalDate passportIssueDate;
    @NotNull
    private String passportIssueBranch;
    @NotNull
    private MaritalStatus maritalStatus;
    @NotNull
    private int dependentAmount;
    @Valid
    @NotNull
    private EmploymentDTO employment;
    @NotNull
    private String account;
    @NotNull
    private boolean isInsuranceEnabled;
    @NotNull
    private boolean isSalaryClient;
}
