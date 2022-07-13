package qiwi.application.controller;

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
import qiwi.application.client.DealClient;
import qiwi.application.dto.ErrorMessageDTO;
import qiwi.application.dto.LoanApplicationRequestDTO;
import qiwi.application.dto.LoanOfferDTO;
import qiwi.application.service.ApplicationService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ApplicationControllerTest {
    private LoanApplicationRequestDTO loanApplicationRequest;
    private LoanOfferDTO[] expectedLoanOfferResponse;

    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationService service;
    @MockBean
    private DealClient client;

    @Test
    void testGetLoanOffers() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(client.getOffers(loanApplicationRequest))
                .thenReturn(Arrays.asList(expectedLoanOfferResponse));

        ResponseEntity<LoanOfferDTO[]> response = restTemplate
                .postForEntity(
                        "/application/application",
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
                        "/application/application",
                        loanApplicationRequest,
                        ErrorMessageDTO.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(expectedLoanOfferResponse, response.getBody());
    }

    @Test
    void testChooseOffer() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        expectedLoanOfferResponse[1].setApplicationId(1L);

        HttpEntity<LoanOfferDTO> requestUpdate = new HttpEntity<>(expectedLoanOfferResponse[1]);
        ResponseEntity<LoanOfferDTO> response = restTemplate.exchange(
                "/application/offer", HttpMethod.PUT, requestUpdate, LoanOfferDTO.class);

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
                "/application/offer", HttpMethod.PUT, requestUpdate, ErrorMessageDTO.class);

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
    }

    private <T> T loadExpectedResponse(String resource, Class<T> tClass) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resource).getFile());
        return mapper.readValue(file, tClass);
    }
}
