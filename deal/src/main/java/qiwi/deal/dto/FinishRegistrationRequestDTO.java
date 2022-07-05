package qiwi.deal.dto;

import lombok.Data;
import qiwi.conveyor.dto.EmploymentDTO;
import qiwi.conveyor.enums.Gender;
import qiwi.conveyor.enums.MaritalStatus;

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

