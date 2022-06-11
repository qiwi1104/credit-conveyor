package qiwi.conveyor.dto;

import lombok.Data;
import qiwi.conveyor.enums.EmploymentStatus;
import qiwi.conveyor.enums.Position;

import javax.validation.constraints.Min;
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
    @Min(12)
    @NotNull
    private int workExperienceTotal;
    @Min(3)
    @NotNull
    private int workExperienceCurrent;
}
