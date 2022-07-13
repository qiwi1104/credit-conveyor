package qiwi.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import qiwi.deal.enums.EmploymentStatus;
import qiwi.deal.enums.Position;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class EmploymentDTO {
    @NotNull
    @Schema(example = "EMPLOYED")
    private EmploymentStatus employmentStatus;
    @NotNull
    @Schema(example = "123456789012")
    private String employerINN;
    @NotNull
    @Schema(example = "40000")
    private BigDecimal salary;
    @NotNull
    @Schema(example = "GRASSROOTS")
    private Position position;
    @NotNull
    @Schema(example = "13")
    private Integer workExperienceTotal;
    @NotNull
    @Schema(example = "4")
    private Integer workExperienceCurrent;
}
