package com.enfint.application.controller;

import com.enfint.application.dto.LoanApplicationRequestDTO;
import com.enfint.application.dto.LoanOfferDTO;
import com.enfint.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO applicationRequest){
        log.info("******************** Getting Loan Offers ********************");
        log.info("applicationRequest {} ",applicationRequest);
        return applicationService.getLoanOfferDTOList(applicationRequest);
    }

    @PutMapping("/offer")
    public void selectOffer(@RequestBody LoanOfferDTO loanOffer){
        log.info("******************** Selected Loan Offer ********************");
        log.info("loanOffer {} ",loanOffer);
        applicationService.selectOffer(loanOffer);
    }
}
