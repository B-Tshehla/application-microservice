package com.enfint.application.service;

import com.enfint.application.dto.LoanApplicationRequestDTO;
import com.enfint.application.dto.LoanOfferDTO;
import com.enfint.application.exception.PreScoringFailedException;
import com.enfint.application.fiegnClient.DealClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private DealClient dealClient;
    @InjectMocks
    private ApplicationService underTest;
    private LoanApplicationRequestDTO loanApplicationRequest;


    @BeforeEach
    void setUp() {
        underTest = new ApplicationService(dealClient);
        loanApplicationRequest = LoanApplicationRequestDTO.builder()
                .amount(BigDecimal.valueOf(10_000))
                .term(10)
                .firstName("Boitumelo")
                .lastName("Tshehla")
                .middleName("Tumi")
                .email("boitumelotshehl@gmail.com")
                .birthdate(LocalDate.of(1999, 1, 21))
                .passportSeries("4265")
                .passportNumber("698534")
                .build();
    }
    @Test
    public void shouldAcceptWhenApplicationRequestIsValid(){
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
        //When
        when(dealClient.getLoanOffers(loanApplicationRequest)).thenReturn(listOffers);
        //Then
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).get(0).getTerm()).isEqualTo(10);
        verify(dealClient).getLoanOffers(loanApplicationRequest);
    }

    @Test
    public void shouldAcceptWhenLoanOfferIsValid(){
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
        //When
        doNothing().when(dealClient).updateApplication(loanOffer);
        underTest.selectOffer(loanOffer);
        //Then
        verify(dealClient).updateApplication(loanOffer);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.MIN_VALUE, 0.00, -10_000, 5_000, 9_999, 100})
    public void shouldThrewARefusalExceptionWhenAmountIsNullOrInvalid(double amount) {

        assertThatThrownBy(() -> {
            loanApplicationRequest.setAmount(null);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring failed");
        assertThatThrownBy(() -> {
            loanApplicationRequest.setAmount(BigDecimal.valueOf(amount));
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring failed");
    }


    @ParameterizedTest()
    @ValueSource(ints = {1, 4, 10, 15, 10, 20})
    public void shouldThrowRefusalExceptionWhenAgeLessThan18(int year) {
        loanApplicationRequest.setBirthdate(LocalDate.of(2004, 10, 25));
        LocalDate actual = loanApplicationRequest.getBirthdate().plusYears(year);
        loanApplicationRequest.setBirthdate(actual);
        assertThatThrownBy(() -> underTest.getLoanOfferDTOList(loanApplicationRequest))
                .isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @Test
    public void shouldThrowRefusalExceptionWhenAgeIsNull() {

        loanApplicationRequest.setBirthdate(null);
        assertThatThrownBy(() -> underTest.getLoanOfferDTOList(loanApplicationRequest))
                .isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenNameIsNullOrEmpty(String name) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setLastName(name);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"562", "jam#nmc", "hye1", "官话/官話", "Guānhuà"})
    void shouldThrowRefusalExceptionWhenNameIsInvalid(String name) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setLastName(name);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"#@%^%#$@#$@#.com", "@example.com", "@example.com", "あいうえお@example.com"})
    public void shouldThrowRefusalExceptionWhenEmailIsInvalid(String email) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setEmail(email);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenEmailIsNullOrEmpty(String email) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setEmail(email);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 0, -10_000, 1, 5, 6})
    public void shouldThrewARefusalExceptionWhenTermIsNullOrInvalid(int term) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setTerm(null);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring failed");
        assertThatThrownBy(() -> {
            loanApplicationRequest.setTerm(term);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenPassportSeriesIsNullOrEmpty(String passportSeries) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportSeries(passportSeries);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "twy32", "123456", "tr%$#"})
    public void shouldAcceptWhenPassportSeriesIsInvalid(String passportSeries) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportSeries(passportSeries);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenPassportNumberIsNullOrEmpty(String passportNumber) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportNumber(passportNumber);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "twy32", "1234", "tr%$#tw", "123456789"})
    public void shouldThrowExceptionPassportNumberIsInvalid(String passportNumber) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportNumber(passportNumber);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(PreScoringFailedException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

}