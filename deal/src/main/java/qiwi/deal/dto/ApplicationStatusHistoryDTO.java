package qiwi.deal.dto;

import lombok.Data;
import lombok.ToString;
import qiwi.deal.enums.ChangeType;
import qiwi.deal.enums.Status;

import java.time.LocalDateTime;

@Data
@ToString
public class ApplicationStatusHistoryDTO {
    private Status status;
    private LocalDateTime time;
    private ChangeType changeType;
}
