package qiwi.conveyor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import qiwi.conveyor.dto.CreditDTO;
import qiwi.conveyor.dto.LoanApplicationRequestDTO;
import qiwi.conveyor.dto.LoanOfferDTO;
import qiwi.conveyor.dto.ScoringDataDTO;
import qiwi.conveyor.service.ConveyorService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ConveyorControllerTest {
    private final String JSON_LOAN_APPLICATION_REQUEST = "{\n" +
            "  \"amount\": 800000,\n" +
            "  \"term\": 24,\n" +
            "  \"firstName\": \"firstName\",\n" +
            "  \"lastName\": \"lastName\",\n" +
            "  \"middleName\": \"middleName\",\n" +
            "  \"email\": \"test@gmail.com\",\n" +
            "  \"birthdate\": \"2001-04-11\",\n" +
            "  \"passportSeries\": \"6314\",\n" +
            "  \"passportNumber\": \"128312\"\n" +
            "}";

    private final String JSON_SCORING_DATA_REQUEST = "{\n" +
            "    \"amount\": 800000,\n" +
            "    \"term\": 7,\n" +
            "    \"firstName\": \"firstName\",\n" +
            "    \"lastName\": \"lastName\",\n" +
            "    \"middleName\": \"middleName\",\n" +
            "    \"gender\": \"MALE\",\n" +
            "    \"birthdate\": \"2001-04-11\",\n" +
            "    \"passportSeries\": \"6314\",\n" +
            "    \"passportNumber\": \"128312\",\n" +
            "    \"passportIssueDate\": \"2021-04-24\",\n" +
            "    \"passportIssueBranch\": \"MVD Rossii\",\n" +
            "    \"maritalStatus\": \"SINGLE\",\n" +
            "    \"dependentAmount\": 0,\n" +
            "    \"employment\": {\n" +
            "        \"employmentStatus\": \"EMPLOYED\",\n" +
            "        \"employerINN\": \"123456789012\",\n" +
            "        \"salary\": 40000,\n" +
            "        \"position\": \"GRASSROOTS\",\n" +
            "        \"workExperienceTotal\": \"13\",\n" +
            "        \"workExperienceCurrent\": \"4\"\n" +
            "    },\n" +
            "    \"account\": \"account\",\n" +
            "    \"isInsuranceEnabled\": false,\n" +
            "    \"isSalaryClient\": false\n" +
            "}";

    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConveyorService service;

    @Test
    void testCreateOffers() throws Exception {
        LoanApplicationRequestDTO loanApplicationRequest = mapper.readValue(JSON_LOAN_APPLICATION_REQUEST,
                LoanApplicationRequestDTO.class);

        String jsonResponse = "[\n" +
                "    {\n" +
                "        \"applicationId\": 0,\n" +
                "        \"requestedAmount\": 800000.00,\n" +
                "        \"totalAmount\": 921847.44,\n" +
                "        \"term\": 24,\n" +
                "        \"monthlyPayment\": 38410.31,\n" +
                "        \"rate\": 14.00,\n" +
                "        \"insuranceEnabled\": false,\n" +
                "        \"salaryClient\": false\n" +
                "    },\n" +
                "    {\n" +
                "        \"applicationId\": 0,\n" +
                "        \"requestedAmount\": 800000.00,\n" +
                "        \"totalAmount\": 912803.04,\n" +
                "        \"term\": 24,\n" +
                "        \"monthlyPayment\": 38033.46,\n" +
                "        \"rate\": 13.00,\n" +
                "        \"insuranceEnabled\": false,\n" +
                "        \"salaryClient\": true\n" +
                "    },\n" +
                "    {\n" +
                "        \"applicationId\": 0,\n" +
                "        \"requestedAmount\": 900000.00,\n" +
                "        \"totalAmount\": 1006729.20,\n" +
                "        \"term\": 24,\n" +
                "        \"monthlyPayment\": 41947.05,\n" +
                "        \"rate\": 11.00,\n" +
                "        \"insuranceEnabled\": true,\n" +
                "        \"salaryClient\": false\n" +
                "    },\n" +
                "    {\n" +
                "        \"applicationId\": 0,\n" +
                "        \"requestedAmount\": 900000.00,\n" +
                "        \"totalAmount\": 996730.32,\n" +
                "        \"term\": 24,\n" +
                "        \"monthlyPayment\": 41530.43,\n" +
                "        \"rate\": 10.00,\n" +
                "        \"insuranceEnabled\": true,\n" +
                "        \"salaryClient\": true\n" +
                "    }\n" +
                "]";

        LoanOfferDTO[] expectedLoanOfferResponse = mapper.readValue(jsonResponse, LoanOfferDTO[].class);

        ResponseEntity<LoanOfferDTO[]> response = restTemplate
                .postForEntity(
                        "/conveyor/offers",
                        loanApplicationRequest,
                        LoanOfferDTO[].class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertArrayEquals(expectedLoanOfferResponse, response.getBody());
    }

    @Test
    void testCreateOffersWhenNullField() throws JsonProcessingException {
        LoanApplicationRequestDTO loanApplicationRequest = mapper.readValue(JSON_LOAN_APPLICATION_REQUEST,
                LoanApplicationRequestDTO.class);
        loanApplicationRequest.setFirstName(null);

        LoanOfferDTO[] expectedLoanOfferResponse = new LoanOfferDTO[4];
        for (int i = 0; i < expectedLoanOfferResponse.length; i++) {
            expectedLoanOfferResponse[i] = new LoanOfferDTO();
        }

        ResponseEntity<LoanOfferDTO[]> response = restTemplate
                .postForEntity(
                        "/conveyor/offers",
                        loanApplicationRequest,
                        LoanOfferDTO[].class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertArrayEquals(expectedLoanOfferResponse, response.getBody());
    }

    @Test
    void testCreateCredit() throws JsonProcessingException {
        ScoringDataDTO scoringDataRequest = mapper.readValue(JSON_SCORING_DATA_REQUEST, ScoringDataDTO.class);

        String jsonResponse = "{\n" +
                "    \"amount\": 800000,\n" +
                "    \"term\": 7,\n" +
                "    \"monthlyPayment\": 119291.45,\n" +
                "    \"rate\": 13.0000000000,\n" +
                "    \"psk\": 835040.15,\n" +
                "    \"paymentSchedule\": [\n" +
                "        {\n" +
                "            \"number\": 1,\n" +
                "            \"date\": \"" + LocalDate.now() + "\",\n" +
                "            \"totalPayment\": 119291.45,\n" +
                "            \"interestPayment\": 8666.67,\n" +
                "            \"debtPayment\": 110624.79,\n" +
                "            \"remainingDebt\": 689375.21\n" +
                "        },\n" +
                "        {\n" +
                "            \"number\": 2,\n" +
                "            \"date\": \"" + LocalDate.now().plusMonths(1) + "\",\n" +
                "            \"totalPayment\": 119291.45,\n" +
                "            \"interestPayment\": 7468.23,\n" +
                "            \"debtPayment\": 111823.22,\n" +
                "            \"remainingDebt\": 577551.99\n" +
                "        },\n" +
                "        {\n" +
                "            \"number\": 3,\n" +
                "            \"date\": \"" + LocalDate.now().plusMonths(2) + "\",\n" +
                "            \"totalPayment\": 119291.45,\n" +
                "            \"interestPayment\": 6256.81,\n" +
                "            \"debtPayment\": 113034.64,\n" +
                "            \"remainingDebt\": 464517.35\n" +
                "        },\n" +
                "        {\n" +
                "            \"number\": 4,\n" +
                "            \"date\": \"" + LocalDate.now().plusMonths(3) + "\",\n" +
                "            \"totalPayment\": 119291.45,\n" +
                "            \"interestPayment\": 5032.27,\n" +
                "            \"debtPayment\": 114259.18,\n" +
                "            \"remainingDebt\": 350258.17\n" +
                "        },\n" +
                "        {\n" +
                "            \"number\": 5,\n" +
                "            \"date\": \"" + LocalDate.now().plusMonths(4) + "\",\n" +
                "            \"totalPayment\": 119291.45,\n" +
                "            \"interestPayment\": 3794.46,\n" +
                "            \"debtPayment\": 115496.99,\n" +
                "            \"remainingDebt\": 234761.18\n" +
                "        },\n" +
                "        {\n" +
                "            \"number\": 6,\n" +
                "            \"date\": \"" + LocalDate.now().plusMonths(5) + "\",\n" +
                "            \"totalPayment\": 119291.45,\n" +
                "            \"interestPayment\": 2543.25,\n" +
                "            \"debtPayment\": 116748.21,\n" +
                "            \"remainingDebt\": 118012.98\n" +
                "        },\n" +
                "        {\n" +
                "            \"number\": 7,\n" +
                "            \"date\": \"" + LocalDate.now().plusMonths(6) + "\",\n" +
                "            \"totalPayment\": 119291.45,\n" +
                "            \"interestPayment\": 1278.47,\n" +
                "            \"debtPayment\": 118012.98,\n" +
                "            \"remainingDebt\": 0.00\n" +
                "        }\n" +
                "    ],\n" +
                "    \"insuranceEnabled\": false,\n" +
                "    \"salaryClient\": false\n" +
                "}";

        CreditDTO expectedCreditResponse = mapper.readValue(jsonResponse, CreditDTO.class);

        ResponseEntity<CreditDTO> response = restTemplate
                .postForEntity(
                        "/conveyor/calculation",
                        scoringDataRequest,
                        CreditDTO.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expectedCreditResponse, response.getBody());
    }

    @Test
    void testCreateCreditWhenNullField() throws JsonProcessingException {
        ScoringDataDTO scoringDataRequest = mapper.readValue(JSON_SCORING_DATA_REQUEST, ScoringDataDTO.class);
        scoringDataRequest.setAmount(null);

        CreditDTO expectedCreditResponse = new CreditDTO();

        ResponseEntity<CreditDTO> response = restTemplate
                .postForEntity(
                        "/conveyor/calculation",
                        scoringDataRequest,
                        CreditDTO.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(expectedCreditResponse, response.getBody());
    }

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}