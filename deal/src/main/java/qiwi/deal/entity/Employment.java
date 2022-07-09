package qiwi.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import qiwi.deal.enums.EmploymentStatus;
import qiwi.deal.enums.Position;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "employments")
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties({"id"})
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "employment_status")
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;
    @Column(name = "employer")
    private String employerINN;
    @Column(name = "salary")
    private BigDecimal salary;
    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private Position position;
    @Column(name = "work_experience_total")
    private Integer workExperienceTotal;
    @Column(name = "work_experience_current")
    private Integer workExperienceCurrent;
}
