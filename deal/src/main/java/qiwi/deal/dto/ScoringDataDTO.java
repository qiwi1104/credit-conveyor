package qiwi.deal.dto;

import lombok.Data;
import lombok.ToString;
import qiwi.deal.enums.Gender;
import qiwi.deal.enums.MaritalStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
public class ScoringDataDTO {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer term;
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
    private Integer dependentAmount;
    @NotNull
    private EmploymentDTO employment;
    @NotNull
    private String account;
    @NotNull
    private Boolean isInsuranceEnabled;
    @NotNull
    private Boolean isSalaryClient;
}
