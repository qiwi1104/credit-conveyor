package qiwi.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import qiwi.application.enums.Gender;
import qiwi.application.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDTO {
    @NotNull
    @Schema(example = "MALE")
    private Gender gender;
    @NotNull
    @Schema(example = "SINGLE")
    private MaritalStatus maritalStatus;
    @NotNull
    @Schema(example = "0")
    private Integer dependentAmount;
    @NotNull
    @Schema(example = "2021-04-24")
    private LocalDate passportIssueDate;
    @NotNull
    @Schema(example = "MVD Rossii")
    private String passportIssueBranch;
    @Valid
    @NotNull
    private EmploymentDTO employment;
    @NotNull
    @Schema(example = "12345678901234567890")
    private String account;
}

