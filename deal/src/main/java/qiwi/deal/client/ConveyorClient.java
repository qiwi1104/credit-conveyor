package qiwi.deal.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import qiwi.conveyor.dto.CreditDTO;
import qiwi.conveyor.dto.LoanApplicationRequestDTO;
import qiwi.conveyor.dto.LoanOfferDTO;
import qiwi.conveyor.dto.ScoringDataDTO;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface ConveyorClient {
    @PostMapping("/conveyor/offers")
    List<LoanOfferDTO> getOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequest);
    @PostMapping("/conveyor/calculation")
    CreditDTO getCredit(@Valid @RequestBody ScoringDataDTO scoringData);
}
