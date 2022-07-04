package qiwi.conveyor.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import qiwi.conveyor.dto.CreditDTO;
import qiwi.conveyor.dto.LoanApplicationRequestDTO;
import qiwi.conveyor.dto.LoanOfferDTO;
import qiwi.conveyor.dto.ScoringDataDTO;
import qiwi.conveyor.service.ConveyorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class ConveyorController {
    @Autowired
    private ConveyorService service;

    @Operation(summary = "create loan offers")
    @PostMapping("/conveyor/offers")
    public ResponseEntity<List<LoanOfferDTO>> createOffers(
            @Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO,
            BindingResult result) {

        return ResponseEntity.ok(service.getLoanOffers(loanApplicationRequestDTO, result));
    }

    @Operation(summary = "create credit")
    @PostMapping("/conveyor/calculation")
    public ResponseEntity<CreditDTO> createCredit(
            @Valid @RequestBody ScoringDataDTO scoringDataDTO, BindingResult result) {

        return ResponseEntity.ok(service.getCredit(scoringDataDTO, result));
    }
}
