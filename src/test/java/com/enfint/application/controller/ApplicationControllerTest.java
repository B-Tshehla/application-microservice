package com.enfint.application.controller;

import com.enfint.application.dto.LoanApplicationRequestDTO;
import com.enfint.application.dto.LoanOfferDTO;
import com.enfint.application.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApplicationService applicationService;


    @Test
    void shouldGetListOfLoanOffers() throws Exception {
        //Given
        List<LoanOfferDTO> listOffers = List.of(
                LoanOfferDTO.builder()
                        .applicationId(1L)
                        .requestedAmount(BigDecimal.valueOf(10000))
                        .totalAmount(BigDecimal.valueOf(11132.70))
                        .term(10)
                        .monthlyPayment(BigDecimal.valueOf(1113.27))
                        .rate(BigDecimal.valueOf(24))
                        .isInsuranceEnabled(false)
                        .isSalaryClient(false)
                        .build(),
                LoanOfferDTO.builder()
                        .applicationId(1L)
                        .requestedAmount(BigDecimal.valueOf(10000))
                        .totalAmount(BigDecimal.valueOf(10747.90))
                        .term(10)
                        .monthlyPayment(BigDecimal.valueOf(1074.79))
                        .rate(BigDecimal.valueOf(16))
                        .isInsuranceEnabled(false)
                        .isSalaryClient(true)
                        .build(),
                LoanOfferDTO.builder()
                        .applicationId(1L)
                        .requestedAmount(BigDecimal.valueOf(10000))
                        .totalAmount(BigDecimal.valueOf(10652.90))
                        .term(10)
                        .monthlyPayment(BigDecimal.valueOf(1065.29))
                        .rate(BigDecimal.valueOf(14))
                        .isInsuranceEnabled(true)
                        .isSalaryClient(false)
                        .build(),
                LoanOfferDTO.builder()
                        .applicationId(1L)
                        .requestedAmount(BigDecimal.valueOf(10000))
                        .totalAmount(BigDecimal.valueOf(10277.10))
                        .term(10)
                        .monthlyPayment(BigDecimal.valueOf(1027.71))
                        .rate(BigDecimal.valueOf(6))
                        .isInsuranceEnabled(true)
                        .isSalaryClient(true)
                        .build());
        LoanApplicationRequestDTO loanApplicationRequest = LoanApplicationRequestDTO.builder()
                .email("boitumelotshehla@gmail.com")
                .amount(BigDecimal.valueOf(10000))
                .birthdate(LocalDate.of(1999, 1, 21))
                .firstName("Boitumelo")
                .lastName("Tshehla")
                .middleName("Tumi")
                .term(10)
                .passportNumber("698534")
                .passportSeries("4265")
                .build();
        ObjectMapper mapper = JsonMapper
                .builder()
                .build().registerModule(new JavaTimeModule());
        String requestBody = mapper.writeValueAsString(loanApplicationRequest);
        //When
        when(applicationService.getLoanOfferDTOList(loanApplicationRequest)).thenReturn(listOffers);
        //Given
        mockMvc.perform(post("http://localhost:8083/application/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].rate").value(BigDecimal.valueOf(24).toString()))
                .andDo(print());

    }

    @Test
    void ShouldUpdateSelectedOffer() throws Exception {
        //Given
        LoanOfferDTO loanOffer = LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(10000))
                .totalAmount(BigDecimal.valueOf(10747.90))
                .term(10)
                .monthlyPayment(BigDecimal.valueOf(1074.79))
                .rate(BigDecimal.valueOf(16))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();
        ObjectMapper mapper = JsonMapper.builder().build().registerModule(new JavaTimeModule());
        String requestBody = mapper.writeValueAsString(loanOffer);
        //When
        doNothing().when(applicationService).selectOffer(loanOffer);
        //Then
        mockMvc.perform(put("http://localhost:8083/application/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
        verify(applicationService).selectOffer(loanOffer);
    }
}