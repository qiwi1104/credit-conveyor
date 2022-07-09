package qiwi.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import qiwi.deal.dto.FinishRegistrationRequestDTO;
import qiwi.deal.dto.LoanApplicationRequestDTO;
import qiwi.deal.dto.LoanOfferDTO;
import qiwi.deal.service.DealService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class DealController {
    @Autowired
    private DealService service;

    @Operation(summary = "calculate loan offers")
    @PostMapping("/deal/application")
    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(
            @Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequest, BindingResult result) {

        return ResponseEntity.ok(service.getLoanOffers(loanApplicationRequest, result));
    }

    @Operation(summary = "choose one offer")
    @PutMapping("/deal/offer")
    public void chooseOffer(@RequestBody LoanOfferDTO loanOfferDTO) {
        service.chooseOffer(loanOfferDTO);
    }

    @Operation(summary = "finish registration, complete credit calculation")
    @PutMapping("/deal/calculate/{applicationId}")
    public void finishRegistration(@Valid @RequestBody FinishRegistrationRequestDTO finishRegistrationRequest,
                                   BindingResult result,
                                   @PathVariable Long applicationId) {

        service.finishRegistration(finishRegistrationRequest, result, applicationId);
    }
}
