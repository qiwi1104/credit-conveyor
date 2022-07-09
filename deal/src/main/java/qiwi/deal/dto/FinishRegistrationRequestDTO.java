package qiwi.deal.dto;

import lombok.Data;
import qiwi.deal.enums.Gender;
import qiwi.deal.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDTO {
    @NotNull
    private Gender gender;
    @NotNull
    private MaritalStatus maritalStatus;
    @NotNull
    private Integer dependentAmount;
    @NotNull
    private LocalDate passportIssueDate;
    @NotNull
    private String passportIssueBranch;
    @Valid
    @NotNull
    private EmploymentDTO employment;
    @NotNull
    private String account;
}

