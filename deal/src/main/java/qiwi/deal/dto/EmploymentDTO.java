package qiwi.deal.dto;

import lombok.Data;
import qiwi.deal.enums.EmploymentStatus;
import qiwi.deal.enums.Position;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class EmploymentDTO {
    @NotNull
    private EmploymentStatus employmentStatus;
    @NotNull
    private String employerINN;
    @NotNull
    private BigDecimal salary;
    @NotNull
    private Position position;
    @NotNull
    private Integer workExperienceTotal;
    @NotNull
    private Integer workExperienceCurrent;
}
