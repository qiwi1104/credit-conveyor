package qiwi.conveyor.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import qiwi.conveyor.dto.*;
import qiwi.conveyor.handler.Response;
import qiwi.conveyor.service.ConveyorService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ConveyorControllerTest {
    private LoanApplicationRequestDTO loanApplicationRequest;
    private ScoringDataDTO scoringDataRequest;

    private LoanOfferDTO[] expectedLoanOfferResponse;
    private CreditDTO expectedCreditResponse;

    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConveyorService service;

    @Test
    void testCreateOffers() {
        ResponseEntity<LoanOfferDTO[]> response = restTemplate
                .postForEntity(
                        "/conveyor/offers",
                        loanApplicationRequest,
                        LoanOfferDTO[].class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertArrayEquals(expectedLoanOfferResponse, response.getBody());
    }

    @Test
    void testCreateOffersWhenNullField() {
        loanApplicationRequest.setFirstName(null);

        Response expectedLoanOfferResponse = new Response("Error on fields: "
                + Arrays.toString(List.of("firstName").toArray()));

        ResponseEntity<Response> response = restTemplate
                .postForEntity(
                        "/conveyor/offers",
                        loanApplicationRequest,
                        Response.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(expectedLoanOfferResponse, response.getBody());
    }

    @Test
    void testCreateCredit() {
        ResponseEntity<CreditDTO> response = restTemplate
                .postForEntity(
                        "/conveyor/calculation",
                        scoringDataRequest,
                        CreditDTO.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expectedCreditResponse, response.getBody());
    }

    @Test
    void testCreateCreditWhenNullField() {
        scoringDataRequest.setAmount(null);

        Response expectedCreditResponse = new Response("Error on fields: "
                + Arrays.toString(List.of("amount").toArray()));

        ResponseEntity<Response> response = restTemplate
                .postForEntity(
                        "/conveyor/calculation",
                        scoringDataRequest,
                        Response.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(expectedCreditResponse, response.getBody());
    }

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("loan_application_request.json").getFile());
        loanApplicationRequest = mapper.readValue(file, LoanApplicationRequestDTO.class);

        file = new File(classLoader.getResource("scoring_data_request.json").getFile());
        scoringDataRequest = mapper.readValue(file, ScoringDataDTO.class);

        file = new File(classLoader.getResource("loan_offers_response.json").getFile());
        expectedLoanOfferResponse = mapper.readValue(file, LoanOfferDTO[].class);

        file = new File(classLoader.getResource("credit_response.json").getFile());
        expectedCreditResponse = mapper.readValue(file, CreditDTO.class);

        for (PaymentScheduleElement payment : expectedCreditResponse.getPaymentSchedule()) {
            payment.setDate(LocalDate.now().plusMonths(payment.getNumber() - 1));
        }
    }
}