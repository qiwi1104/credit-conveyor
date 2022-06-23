package qiwi.conveyor.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import qiwi.conveyor.exceptions.InvalidLoanApplicationRequestException;
import qiwi.conveyor.exceptions.InvalidScoringDataException;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidLoanApplicationRequestException.class)
    public ResponseEntity<Response> handleInvalidLoanApplicationRequestException(
            InvalidLoanApplicationRequestException e) {
        return new ResponseEntity<>(
                new Response(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidScoringDataException.class)
    public ResponseEntity<Response> handleInvalidScoringDataRequestException(InvalidScoringDataException e) {
        return new ResponseEntity<>(new Response(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
