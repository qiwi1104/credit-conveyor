package qiwi.deal.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import qiwi.conveyor.enums.Gender;
import qiwi.conveyor.enums.MaritalStatus;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties({"id"})
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "email")
    private String email;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
    @Column(name = "dependent_amount")
    private Integer dependentAmount;
    @OneToOne
    @JoinColumn(name = "passport_id", referencedColumnName = "id")
    private Passport passport;
    @OneToOne
    @JoinColumn(name = "employment_id", referencedColumnName = "id")
    private Employment employment;
    @Column(name = "account")
    private String account;
}
