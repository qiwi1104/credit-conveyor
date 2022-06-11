package qiwi.conveyor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
import java.util.Objects;

@RestController
public class ConveyorController {
    @Autowired
    private ConveyorService service;

    @PostMapping("/conveyor/offers")
    public ResponseEntity<List<LoanOfferDTO>> createOffers(
            @Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO, BindingResult result) {

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                if (Objects.equals(error.getCode(), "NotNull")) {
                    return new ResponseEntity<>(service.getLoanOffers(loanApplicationRequestDTO, result),
                            HttpStatus.BAD_REQUEST);
                }
            }
        }

        return new ResponseEntity<>(service.getLoanOffers(loanApplicationRequestDTO, result), HttpStatus.OK);
    }

    @PostMapping("/conveyor/calculation")
    public ResponseEntity<CreditDTO> createCredit(
            @Valid @RequestBody ScoringDataDTO scoringDataDTO, BindingResult result) {

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                if (Objects.equals(error.getCode(), "NotNull")) {
                    return new ResponseEntity<>(service.getCredit(scoringDataDTO, result),
                            HttpStatus.BAD_REQUEST);
                }
            }
        }

        return new ResponseEntity<>(service.getCredit(scoringDataDTO, result), HttpStatus.OK);
    }
}
