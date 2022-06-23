package qiwi.conveyor.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import qiwi.conveyor.dto.CreditDTO;
import qiwi.conveyor.dto.LoanApplicationRequestDTO;
import qiwi.conveyor.dto.LoanOfferDTO;
import qiwi.conveyor.dto.ScoringDataDTO;
import qiwi.conveyor.exceptions.InvalidLoanApplicationRequestException;
import qiwi.conveyor.exceptions.InvalidScoringDataException;
import qiwi.conveyor.service.ConveyorService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ConveyorController {
    @Autowired
    private ConveyorService service;

    @Operation(summary = "create loan offers")
    @PostMapping("/conveyor/offers")
    public ResponseEntity<List<LoanOfferDTO>> createOffers(
            @Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO,
            BindingResult result) throws InvalidLoanApplicationRequestException {

        List<String> fields = result.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());

        if (!service.isValidMiddleName(loanApplicationRequestDTO.getMiddleName())) {
            fields.add("middleName");
        }

        if (!fields.isEmpty()) {
            log.trace("Error on fields: {}", fields);

            throw new InvalidLoanApplicationRequestException("Error on fields: "
                    + Arrays.toString(fields.toArray()));
        }

        return ResponseEntity.ok(service.getLoanOffers(loanApplicationRequestDTO, result));
    }

    @Operation(summary = "create credit")
    @PostMapping("/conveyor/calculation")
    public ResponseEntity<CreditDTO> createCredit(
            @Valid @RequestBody ScoringDataDTO scoringDataDTO, BindingResult result)
            throws InvalidScoringDataException {

        List<String> fields = result.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());

        if (!service.isValidMiddleName(scoringDataDTO.getMiddleName())) {
            fields.add("middleName");
        }

        if (result.hasErrors()) {
            log.trace("Error on fields: {}", fields);

            throw new InvalidScoringDataException("Error on fields: "
                    + Arrays.toString(fields.toArray()));
        }

        return ResponseEntity.ok(service.getCredit(scoringDataDTO, result));
    }
}
