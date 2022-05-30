package qiwi.conveyor.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ConveyorController {
    @Autowired
    private ConveyorService service;

    @PostMapping("/conveyor/offers")
    public List<LoanOfferDTO> createOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO,
                                           BindingResult result) {
        return service.getLoanOffers(loanApplicationRequestDTO, result);
    }

    @PostMapping("/conveyor/calculation")
    public CreditDTO createCredit(@Valid @RequestBody ScoringDataDTO scoringDataDTO, BindingResult result) {
        return service.getCredit(scoringDataDTO, result);
    }
}
