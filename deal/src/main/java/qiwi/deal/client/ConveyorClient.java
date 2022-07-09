package qiwi.deal.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import qiwi.deal.dto.CreditDTO;
import qiwi.deal.dto.LoanApplicationRequestDTO;
import qiwi.deal.dto.LoanOfferDTO;
import qiwi.deal.dto.ScoringDataDTO;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface ConveyorClient {
    @PostMapping("/conveyor/offers")
    List<LoanOfferDTO> getOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequest);
    @PostMapping("/conveyor/calculation")
    CreditDTO getCredit(@Valid @RequestBody ScoringDataDTO scoringData);
}
