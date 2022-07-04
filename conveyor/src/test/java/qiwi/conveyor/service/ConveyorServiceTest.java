package qiwi.conveyor.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindingResult;
import qiwi.conveyor.dto.*;
import qiwi.conveyor.enums.EmploymentStatus;
import qiwi.conveyor.exceptions.InvalidLoanApplicationRequestException;
import qiwi.conveyor.exceptions.InvalidScoringDataException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConveyorServiceTest {
    private LoanApplicationRequestDTO loanApplicationRequest;
    private ScoringDataDTO scoringDataRequest;

    private LoanOfferDTO[] expectedLoanOfferResponse;
    private CreditDTO expectedCreditResponse;

    private ObjectMapper mapper;

    private final ConveyorService service = new ConveyorService();

    @Test
    void testLoanOffers() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        LoanOfferDTO[] response = service.getLoanOffers(loanApplicationRequest, bindingResult).toArray(LoanOfferDTO[]::new);

        assertArrayEquals(expectedLoanOfferResponse, response);
    }

    @Test
    void testLoanOffersWithUnacceptableAmount() {
        loanApplicationRequest.setAmount(new BigDecimal("9000"));
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    @Test
    void testLoanOffersWithUnacceptableTerm() {
        loanApplicationRequest.setTerm(3);
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    @Test
    void testLoanOffersWithUnacceptableFirstName() {
        loanApplicationRequest.setFirstName("a");
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    @Test
    void testLoanOffersWithUnacceptableMiddleName() {
        if (loanApplicationRequest.getMiddleName() != null) {
            loanApplicationRequest.setMiddleName("a");
            assertThrows(InvalidLoanApplicationRequestException.class,
                    () -> testLoanOffersWithUnacceptableData(loanApplicationRequest));
        }
    }

    @Test
    void testLoanOffersWithUnacceptableLastName() {
        loanApplicationRequest.setLastName("a");
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    @Test
    void testLoanOffersWithUnacceptableEmail() {
        loanApplicationRequest.setEmail("a");
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    @Test
    void testLoanOffersWithUnacceptableBirthdate() {
        loanApplicationRequest.setBirthdate(LocalDate.now());
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    @Test
    void testLoanOffersWithUnacceptablePassportSeries() {
        loanApplicationRequest.setPassportSeries("a");
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    @Test
    void testLoanOffersWithUnacceptablePassportNumber() {
        loanApplicationRequest.setPassportNumber("a");
        testLoanOffersWithUnacceptableData(loanApplicationRequest);
    }

    private void testLoanOffersWithUnacceptableData(LoanApplicationRequestDTO loanApplicationRequest) {
        LoanOfferDTO[] expectedLoanOfferResponse = new LoanOfferDTO[0];

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        LoanOfferDTO[] response = service.getLoanOffers(loanApplicationRequest, bindingResult)
                .toArray(LoanOfferDTO[]::new);

        assertArrayEquals(expectedLoanOfferResponse, response);
    }

    @Test
    void testCredit() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        CreditDTO response = service.getCredit(scoringDataRequest, bindingResult);

        assertEquals(expectedCreditResponse, response);
    }

    @Test
    void testCreditWithUnacceptableAmount() {
        scoringDataRequest.setAmount(new BigDecimal("9000"));
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableTerm() {
        scoringDataRequest.setTerm(3);
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableFirstName() {
        scoringDataRequest.setFirstName("a");
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableMiddleName() {
        if (scoringDataRequest.getMiddleName() != null) {
            scoringDataRequest.setMiddleName("a");
            assertThrows(InvalidScoringDataException.class,
                    () -> testCreditWithUnacceptableData(scoringDataRequest));
        }
    }

    @Test
    void testCreditWithUnacceptableLastName() {
        scoringDataRequest.setLastName("a");
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableBirthdate() {
        scoringDataRequest.setBirthdate(LocalDate.now());
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptablePassportSeries() {
        scoringDataRequest.setPassportSeries("1");
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptablePassportNumber() {
        scoringDataRequest.setPassportNumber("1");
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableEmploymentStatus() {
        EmploymentDTO employment = scoringDataRequest.getEmployment();
        employment.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);

        scoringDataRequest.setEmployment(employment);
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableSalary() {
        EmploymentDTO employment = scoringDataRequest.getEmployment();
        employment.setSalary(new BigDecimal("10"));

        scoringDataRequest.setEmployment(employment);
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableWorkExperienceTotal() {
        EmploymentDTO employment = scoringDataRequest.getEmployment();
        employment.setWorkExperienceTotal(1);

        scoringDataRequest.setEmployment(employment);
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    @Test
    void testCreditWithUnacceptableWorkExperienceCurrent() {
        EmploymentDTO employment = scoringDataRequest.getEmployment();
        employment.setWorkExperienceTotal(1);

        scoringDataRequest.setEmployment(employment);
        testCreditWithUnacceptableData(scoringDataRequest);
    }

    private void testCreditWithUnacceptableData(ScoringDataDTO scoringDataRequest) {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        CreditDTO response = service.getCredit(scoringDataRequest, bindingResult);

        assertEquals(new CreditDTO(), response);
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