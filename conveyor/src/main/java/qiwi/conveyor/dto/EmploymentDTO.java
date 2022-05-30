package qiwi.conveyor.dto;

import lombok.Data;
import qiwi.conveyor.enums.EmploymentStatus;
import qiwi.conveyor.enums.Position;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class EmploymentDTO {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    @Min(12)
    private int workExperienceTotal;
    @Min(3)
    private int workExperienceCurrent;
}
