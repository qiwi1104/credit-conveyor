package qiwi.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import qiwi.deal.dto.ErrorMessageDTO;
import qiwi.deal.dto.FinishRegistrationRequestDTO;
import qiwi.deal.dto.LoanApplicationRequestDTO;
import qiwi.deal.dto.LoanOfferDTO;
import qiwi.deal.exceptions.InvalidLoanApplicationRequestException;
import qiwi.deal.service.DealService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class DealController {
    @Autowired
    private DealService service;

    @Operation(summary = "calculate loan offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Error on field(s) is present.",
                    content = {
                    @Content(schema = @Schema(implementation = ErrorMessageDTO.class))
            })
    })
    @PostMapping("/deal/application")
    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(
            @Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequest, BindingResult result) {

        return ResponseEntity.ok(service.getLoanOffers(loanApplicationRequest, result));
    }

    @Operation(summary = "choose one offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Error on field(s) is present.",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorMessageDTO.class))
                    })
    })
    @PutMapping("/deal/offer")
    public void chooseOffer(@RequestBody LoanOfferDTO loanOfferDTO) {
        service.chooseOffer(loanOfferDTO);
    }

    @Operation(summary = "finish registration, complete credit calculation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Error on field(s) is present.",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorMessageDTO.class))
                    })
    })
    @PutMapping("/deal/calculate/{applicationId}")
    public void finishRegistration(@Valid @RequestBody FinishRegistrationRequestDTO finishRegistrationRequest,
                                   BindingResult result,
                                   @PathVariable Long applicationId) {

        service.finishRegistration(finishRegistrationRequest, result, applicationId);
    }
}
