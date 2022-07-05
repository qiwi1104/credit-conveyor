package qiwi.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import qiwi.deal.enums.Status;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties({"id"})
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;
    @OneToOne
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    private Credit credit;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate creationDate;
    @OneToOne
    @JoinColumn(name = "applied_offer_id", referencedColumnName = "application_id")
    private LoanOffer appliedOffer;
    private LocalDate signDate;
    private String sesCode;
    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER)
    private List<ApplicationStatusHistory> applicationStatusHistory;
}
