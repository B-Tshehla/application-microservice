package com.enfint.application.fiegnClient;

import com.enfint.application.dto.LoanApplicationRequestDTO;
import com.enfint.application.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "conveyorClient",url = "http://localhost:8082/deal" )
public interface DealClient {

    @PostMapping("/application")
    List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequest);

    @PutMapping("/offer")
    void updateApplication(@RequestBody LoanOfferDTO loanOffer);

}
