package qiwi.conveyor.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import qiwi.conveyor.dto.ErrorMessageDTO;
import qiwi.conveyor.exceptions.InvalidLoanApplicationRequestException;
import qiwi.conveyor.exceptions.InvalidScoringDataException;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidLoanApplicationRequestException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidLoanApplicationRequestException(
            InvalidLoanApplicationRequestException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessageDTO(e.getMessage()));
    }

    @ExceptionHandler(InvalidScoringDataException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidScoringDataRequestException(InvalidScoringDataException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessageDTO(e.getMessage()));
    }
}
