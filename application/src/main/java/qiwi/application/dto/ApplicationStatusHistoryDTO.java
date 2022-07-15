package qiwi.application.dto;

import lombok.Data;
import lombok.ToString;
import qiwi.application.enums.ChangeType;
import qiwi.application.enums.Status;

import java.time.LocalDateTime;

@Data
@ToString
public class ApplicationStatusHistoryDTO {
    private Status status;
    private LocalDateTime time;
    private ChangeType changeType;
}
