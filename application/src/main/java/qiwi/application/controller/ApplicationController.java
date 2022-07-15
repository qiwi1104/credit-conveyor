package qiwi.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import qiwi.application.dto.ErrorMessageDTO;
import qiwi.application.dto.LoanApplicationRequestDTO;
import qiwi.application.dto.LoanOfferDTO;
import qiwi.application.service.ApplicationService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ApplicationController {
    @Autowired
    private ApplicationService service;

    @Operation(summary = "calculate loan offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Error on field(s) is present.",
                    content = {
                            @Content(schema = @Schema(implementation = ErrorMessageDTO.class))
                    })
    })
    @PostMapping("/application/application")
    public ResponseEntity<List<LoanOfferDTO>> getLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequest,
                                                            BindingResult result) {
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
    @PutMapping("/application/offer")
    public void chooseOffer(@RequestBody LoanOfferDTO loanOffer) {
        service.chooseOffer(loanOffer);
    }
}
