package com.enfint.application.service;

import com.enfint.application.dto.LoanApplicationRequestDTO;
import com.enfint.application.dto.LoanOfferDTO;
import com.enfint.application.exception.PreScoringFailedException;
import com.enfint.application.fiegnClient.DealClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {
    private final DealClient dealClient;
    public List<LoanOfferDTO>  getLoanOfferDTOList(LoanApplicationRequestDTO loanApplicationRequest){
        PreScoring(loanApplicationRequest);
        log.info("******************** post request to deal ********************");
        return dealClient.getLoanOffers(loanApplicationRequest);
    }
    public void selectOffer(LoanOfferDTO loanOffer){
        log.info("******************** put request to deal ********************");
        dealClient.updateApplication(loanOffer);
    }


    private void PreScoring(LoanApplicationRequestDTO loanApplication) {
        log.info("************ Validating data ***************");
        validateAmount(loanApplication.getAmount());
        validateAge(loanApplication.getBirthdate());
        validateName(loanApplication.getFirstName());
        validateName(loanApplication.getLastName());
        blankMiddleNameCheck(loanApplication.getMiddleName());
        validateEmail(loanApplication.getEmail());
        validateLoanTerm(loanApplication.getTerm());
        passportNumberValidation(loanApplication.getPassportNumber());
        validatePassportSeries(loanApplication.getPassportSeries());
    }

    private void validateName(String name) {
        log.info("Validating name...{}", name);
        if (name == null || name.isEmpty()) {
            throw new PreScoringFailedException("Pre-scoring failed name is null");
        } else if (Pattern.matches("[a-zA-Z]{2,30}", name)) {
            log.info("Valid name passed!");
        } else {
            throw new PreScoringFailedException("Pre-scoring failed name is not valid");
        }
    }

    private void blankMiddleNameCheck(String name) {
        if (name == null || name.isEmpty()) {
            log.info("MiddleName is Empty...");
        } else {
            validateName(name);
        }
    }

    private void validateEmail(String email) {
        log.info("Validating email...");
        if (email == null || email.isEmpty()) {
            throw new PreScoringFailedException("Pre-scoring failed email is empty");
        } else if (Pattern.matches("[\\w\\.]{2,50}@[\\w\\.]{2,20}", email)) {
            log.info("Valid email passed!");
        } else {
            throw new PreScoringFailedException("Pre-scoring failed email is invalid");
        }
    }

    private void validateLoanTerm(Integer term) {
        log.info("Validating term...");
        if (term == null) {
            throw new PreScoringFailedException("Pre-scoring failed term is null");
        } else if (term > 6) {
            log.info("Term is more than 6 months passed!");
        } else {
            throw new PreScoringFailedException("Pre-scoring failed term is less than 6 months");
        }
    }

    private void validatePassportSeries(String passportSeries) {
        log.info("Validating passport series...");
        if (passportSeries == null || passportSeries.isEmpty()) {
            throw new PreScoringFailedException("Pre-scoring failed passport series is null");
        } else if (Pattern.matches("\\d{4}", passportSeries)) {
            log.info("Valid passport series passed!");
        } else {
            throw new PreScoringFailedException("Pre-scoring failed invalid passport series");
        }
    }

    private void passportNumberValidation(String passportNumber) {
        log.info("Validating passport Number...");
        if (passportNumber == null || passportNumber.isEmpty()) {
            throw new PreScoringFailedException("Pre-scoring failed passport number is null");
        } else if (Pattern.matches("\\d{6}", passportNumber)) {
            log.info("Valid passport number passed!");
        } else {
            throw new PreScoringFailedException("Pre-scoring failed passport number is invalid");
        }
    }

    private void validateAge(LocalDate dob) {
        log.info("Validating age...");
        if (dob == null) {
            throw new PreScoringFailedException("Pre-scoring failed age is null");
        } else if (Period.between(dob, LocalDate.now()).getYears() >= 18) {
            log.info("Valid age passed!");
        } else {
            throw new PreScoringFailedException("Pre-scoring failed client younger than 18");
        }
    }

    private void validateAmount(BigDecimal amount) {
        log.info("Validating loan Amount...");
        if (amount == null) {
            throw new PreScoringFailedException("Pre-scoring failed amount is null");
        } else if (amount.compareTo(BigDecimal.valueOf(10_000)) >= 0) {
            log.info("Valid amount passed!");
        } else {
            throw new PreScoringFailedException("Pre-scoring failed amount is less than 10,000.00");
        }
    }
}
