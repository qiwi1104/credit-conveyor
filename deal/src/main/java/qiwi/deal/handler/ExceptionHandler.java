package qiwi.deal.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import qiwi.conveyor.dto.ErrorMessageDTO;
import qiwi.conveyor.exceptions.InvalidLoanApplicationRequestException;
import qiwi.conveyor.exceptions.InvalidScoringDataException;
import qiwi.deal.exceptions.InvalidFinishRegistrationRequestException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidLoanApplicationRequestException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidLoanApplicationRequestException(
            InvalidLoanApplicationRequestException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessageDTO(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidScoringDataException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidScoringDataRequestException(
            InvalidScoringDataException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessageDTO(e.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidFinishRegistrationRequestException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidFinishRegistrationRequestException(
            InvalidFinishRegistrationRequestException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessageDTO(e.getMessage()));
    }
}
