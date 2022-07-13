package qiwi.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
public class LoanApplicationRequestDTO {
    @NotNull
    @Schema(example = "800000")
    private BigDecimal amount;
    @NotNull
    @Schema(example = "24")
    private Integer term;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-zА-Яа-я]+")
    @NotNull
    @Schema(example = "firstName")
    private String firstName;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Za-zА-Яа-я]+")
    @NotNull
    @Schema(example = "lastName")
    private String lastName;
    @Schema(example = "middleName")
    private String middleName;
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    @NotNull
    @Schema(example = "test@gmail.com")
    private String email;
    @NotNull
    @Schema(example = "2001-04-11")
    private LocalDate birthdate;
    @Pattern(regexp = "\\d{4}")
    @NotNull
    @Schema(example = "6314")
    private String passportSeries;
    @Pattern(regexp = "\\d{6}")
    @NotNull
    @Schema(example = "128312")
    private String passportNumber;
}
