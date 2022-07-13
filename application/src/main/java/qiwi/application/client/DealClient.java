package qiwi.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import qiwi.application.dto.LoanApplicationRequestDTO;
import qiwi.application.dto.LoanOfferDTO;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface DealClient {
    @PostMapping("/deal/application")
    List<LoanOfferDTO> getOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequest);
    @PutMapping("/deal/offer")
    void chooseOffer(@RequestBody LoanOfferDTO loanOffer);
}
