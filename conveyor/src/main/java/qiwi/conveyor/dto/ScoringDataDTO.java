package qiwi.conveyor.dto;

import lombok.Data;
import qiwi.conveyor.enums.Gender;
import qiwi.conveyor.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDTO {
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
    private Gender gender;
    private LocalDate birthdate;
    @Pattern(regexp = "\\d{4}")
    private String passportSeries;
    @Pattern(regexp = "\\d{6}")
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;
    private int dependentAmount;
    @Valid
    private EmploymentDTO employment;
    private String account;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
}
