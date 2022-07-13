package qiwi.deal.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import qiwi.deal.dto.CreditDTO;
import qiwi.deal.dto.ErrorMessageDTO;
import qiwi.deal.dto.LoanApplicationRequestDTO;
import qiwi.deal.dto.LoanOfferDTO;
import qiwi.deal.client.ConveyorClient;
import qiwi.deal.dto.FinishRegistrationRequestDTO;
import qiwi.deal.service.DataAccessService;
import qiwi.deal.service.DealService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DealControllerTest {
    private LoanApplicationRequestDTO loanApplicationRequest;
    private LoanOfferDTO[] expectedLoanOfferResponse;
    private CreditDTO expectedCreditResponse;
    private FinishRegistrationRequestDTO finishRegistrationRequest;

    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DealService service;
    @Autowired
    private DataAccessService dataAccessService;
    @MockBean
    private ConveyorClient client;

    @Test
    @Order(1)
    void testGetLoanOffers() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(client.getOffers(loanApplicationRequest))
                .thenReturn(Arrays.asList(expectedLoanOfferResponse));

        ResponseEntity<LoanOfferDTO[]> response = restTemplate
                .postForEntity(
                        "/deal/application",
                        loanApplicationRequest,
                        LoanOfferDTO[].class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertArrayEquals(expectedLoanOfferResponse, response.getBody());
    }

    @Test
    void testGetLoanOffersWhenNullField() {
        when(client.getOffers(loanApplicationRequest))
                .thenReturn(new ArrayList<>());

        loanApplicationRequest.setFirstName(null);

        ErrorMessageDTO expectedLoanOfferResponse = new ErrorMessageDTO("Error on fields: "
                + Arrays.toString(List.of("firstName").toArray()));

        ResponseEntity<ErrorMessageDTO> response = restTemplate
                .postForEntity(
                        "/deal/application",
                        loanApplicationRequest,
                        ErrorMessageDTO.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(expectedLoanOfferResponse, response.getBody());
    }

    @Test
    @Order(2)
    void testChooseOffer() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        expectedLoanOfferResponse[1].setApplicationId(1L);

        HttpEntity<LoanOfferDTO> requestUpdate = new HttpEntity<>(expectedLoanOfferResponse[1]);
        ResponseEntity<LoanOfferDTO> response = restTemplate.exchange(
                "/deal/offer", HttpMethod.PUT, requestUpdate, LoanOfferDTO.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void testChooseOfferWhenNullField() {
        expectedLoanOfferResponse[1].setApplicationId(1L);
        expectedLoanOfferResponse[1].setTerm(null);

        ErrorMessageDTO expectedResponse = new ErrorMessageDTO("Error on fields: "
                + Arrays.toString(List.of("term").toArray()));

        HttpEntity<LoanOfferDTO> requestUpdate = new HttpEntity<>(expectedLoanOfferResponse[1]);
        ResponseEntity<ErrorMessageDTO> response = restTemplate.exchange(
                "/deal/offer", HttpMethod.PUT, requestUpdate, ErrorMessageDTO.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(response.getBody().getMessage(), expectedResponse.getMessage());
    }

    @Test
    @Order(3)
    void testFinishRegistration() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(client.getCredit(any()))
                .thenReturn(expectedCreditResponse);

        HttpEntity<FinishRegistrationRequestDTO> requestUpdate = new HttpEntity<>(finishRegistrationRequest);
        ResponseEntity<FinishRegistrationRequestDTO> response = restTemplate.exchange(
                "/deal/calculate/1", HttpMethod.PUT, requestUpdate, FinishRegistrationRequestDTO.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void testFinishRegistrationWhenNullField() {
        finishRegistrationRequest.setGender(null);

        ErrorMessageDTO expectedResponse = new ErrorMessageDTO("Error on fields: "
                + Arrays.toString(List.of("gender").toArray()));

        HttpEntity<FinishRegistrationRequestDTO> requestUpdate = new HttpEntity<>(finishRegistrationRequest);
        ResponseEntity<ErrorMessageDTO> response = restTemplate.exchange(
                "/deal/calculate/1", HttpMethod.PUT, requestUpdate, ErrorMessageDTO.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(response.getBody().getMessage(), expectedResponse.getMessage());
    }

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        loanApplicationRequest = loadExpectedResponse("get-loan-offers/loan_application_request.json",
                LoanApplicationRequestDTO.class);
        expectedLoanOfferResponse = loadExpectedResponse("get-loan-offers/loan_offers_response.json",
                LoanOfferDTO[].class);
        expectedCreditResponse = loadExpectedResponse("finish-registration/credit_response.json",
                CreditDTO.class);
        for (qiwi.deal.dto.PaymentScheduleElement payment : expectedCreditResponse.getPaymentSchedule()) {
            payment.setDate(LocalDate.now().plusMonths(payment.getNumber() - 1));
        }

        finishRegistrationRequest = loadExpectedResponse("finish-registration/finish_registration_request.json",
                FinishRegistrationRequestDTO.class);
    }

    private <T> T loadExpectedResponse(String resource, Class<T> tClass) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resource).getFile());
        return mapper.readValue(file, tClass);
    }
}
