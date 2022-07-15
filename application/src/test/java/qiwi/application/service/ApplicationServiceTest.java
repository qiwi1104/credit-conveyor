package qiwi.application.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import qiwi.application.client.DealClient;
import qiwi.application.dto.LoanApplicationRequestDTO;
import qiwi.application.dto.LoanOfferDTO;
import qiwi.application.exceptions.InvalidLoanApplicationRequestException;
import qiwi.application.exceptions.InvalidScoringDataException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ApplicationServiceTest {
    private LoanApplicationRequestDTO loanApplicationRequest;
    private LoanOfferDTO[] expectedLoanOfferResponse;

    private ObjectMapper mapper;

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

        List<LoanOfferDTO> loanOfferDTOS = service.getLoanOffers(loanApplicationRequest, result);

        assertArrayEquals(expectedLoanOfferResponse, loanOfferDTOS.toArray());
    }

    @Test
    void testGetLoanOffersNotPassingPrescoring() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        loanApplicationRequest.setAmount(BigDecimal.ONE);

        List<LoanOfferDTO> loanOfferDTOS = service.getLoanOffers(loanApplicationRequest, result);

        assertArrayEquals(new LoanOfferDTO[0], loanOfferDTOS.toArray());
    }

    @Test
    void testGetLoanOffersWhenNullField() {
        BindingResult result = new BeanPropertyBindingResult(loanApplicationRequest, "loanApplicationRequest");
        result.addError(new FieldError("loanApplicationRequest", "firstName", ""));

        loanApplicationRequest.setFirstName(null);

        assertThrows(InvalidLoanApplicationRequestException.class, () -> {
            List<LoanOfferDTO> loanOfferDTOS = service.getLoanOffers(loanApplicationRequest, result);
        });
    }

    @Test
    void testChooseOffer() {
        expectedLoanOfferResponse[1].setApplicationId(1L);

        assertDoesNotThrow(() -> service.chooseOffer(expectedLoanOfferResponse[1]));
    }

    @Test
    void testChooseOfferWhenNullField() {
        expectedLoanOfferResponse[1].setApplicationId(1L);
        expectedLoanOfferResponse[1].setTerm(null);

        assertThrows(InvalidScoringDataException.class, () -> {
            service.chooseOffer(expectedLoanOfferResponse[1]);
        });
    }

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        loadExpectedResponses();
    }

    private void loadExpectedResponses() throws IOException {
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
