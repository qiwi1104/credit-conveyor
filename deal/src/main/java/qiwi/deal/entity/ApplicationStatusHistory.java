package qiwi.deal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import qiwi.deal.enums.ChangeType;
import qiwi.deal.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications_status_history")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "application")
@JsonIgnoreProperties({"application_id"})
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "change_type")
    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
}
