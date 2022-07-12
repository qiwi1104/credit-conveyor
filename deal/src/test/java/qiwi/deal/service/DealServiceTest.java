package qiwi.deal.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import qiwi.deal.client.ConveyorClient;
import qiwi.deal.dto.CreditDTO;
import qiwi.deal.dto.FinishRegistrationRequestDTO;
import qiwi.deal.dto.LoanApplicationRequestDTO;
import qiwi.deal.dto.LoanOfferDTO;
import qiwi.deal.entity.*;
import qiwi.deal.enums.Status;
import qiwi.deal.exceptions.InvalidFinishRegistrationRequestException;
import qiwi.deal.exceptions.InvalidLoanApplicationRequestException;
import qiwi.deal.exceptions.InvalidScoringDataException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static qiwi.deal.enums.CreditStatus.CALCULATED;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DealServiceTest {
    private LoanApplicationRequestDTO loanApplicationRequest;

    private LoanOfferDTO[] expectedLoanOfferResponse;
    private CreditDTO expectedCreditResponse;

    private Application expectedApplicationOnGetLoanOffersStep;
    private Application expectedApplicationOnChooseOfferStep;
    private Application expectedApplicationOnFinishRegistrationStep;
    private FinishRegistrationRequestDTO finishRegistrationRequest;

    private ObjectMapper mapper;
    @Autowired
    private DealService service;
    @Autowired
    private DataAccessService dataAccessService;
    @MockBean
    private ConveyorClient client;

    private void assertOffersHaveApplicationId(List<LoanOfferDTO> loanOffers, Application application) {
        for (LoanOfferDTO loanOffer : loanOffers) {
            assertEquals(application.getApplicationId(), loanOffer.getApplicationId());
        }
    }

    private void assertPassportEquals(Passport expectedPassport, Passport actualPassport) {
        assertEquals(expectedPassport.getId(), actualPassport.getId());
        assertEquals(expectedPassport.getSeries(), actualPassport.getSeries());
        assertEquals(expectedPassport.getNumber(), actualPassport.getNumber());
        assertEquals(expectedPassport.getIssueDate(), actualPassport.getIssueDate());
        assertEquals(expectedPassport.getIssueBranch(), actualPassport.getIssueBranch());
    }

    private void assertEmploymentEquals(Employment expectedEmployment, Employment actualEmployment) {
        if (expectedEmployment != null && actualEmployment != null) {
            assertEquals(expectedEmployment.getId(), actualEmployment.getId());
            assertEquals(expectedEmployment.getEmploymentStatus(), actualEmployment.getEmploymentStatus());
            assertEquals(expectedEmployment.getEmployerINN(), actualEmployment.getEmployerINN());
            assertEquals(expectedEmployment.getSalary(), actualEmployment.getSalary());
            assertEquals(expectedEmployment.getPosition(), actualEmployment.getPosition());
            assertEquals(expectedEmployment.getWorkExperienceTotal(), actualEmployment.getWorkExperienceTotal());
            assertEquals(expectedEmployment.getWorkExperienceCurrent(), actualEmployment.getWorkExperienceCurrent());
        } else {
            assertNull(actualEmployment);
        }
    }

    private void assertClientEquals(Client expectedClient, Client actualClient) {
        assertEquals(expectedClient.getId(), actualClient.getId());
        assertEquals(expectedClient.getLastName(), actualClient.getLastName());
        assertEquals(expectedClient.getFirstName(), actualClient.getFirstName());
        assertEquals(expectedClient.getMiddleName(), actualClient.getMiddleName());
        assertEquals(expectedClient.getBirthDate(), actualClient.getBirthDate());
        assertEquals(expectedClient.getEmail(), actualClient.getEmail());
        assertEquals(expectedClient.getGender(), actualClient.getGender());
        assertEquals(expectedClient.getMaritalStatus(), actualClient.getMaritalStatus());
        assertEquals(expectedClient.getAccount(), actualClient.getAccount());

        assertPassportEquals(expectedClient.getPassport(),
                actualClient.getPassport());
        assertEmploymentEquals(expectedClient.getEmployment(),
                actualClient.getEmployment());
    }

    private void assertCreditEquals(Credit expectedCredit, Credit actualCredit) {
        if (expectedCredit != null && actualCredit != null) {

        } else {
            assertNull(actualCredit);
        }
    }

    private void assertAppliedOfferEquals(LoanOffer expectedOffer, LoanOffer actualOffer) {
        if (expectedOffer != null && actualOffer != null) {
            assertEquals(expectedOffer.getApplicationId(), actualOffer.getApplicationId());
            assertEquals(expectedOffer.getRequestedAmount(), actualOffer.getRequestedAmount());
            assertEquals(expectedOffer.getTotalAmount(), actualOffer.getTotalAmount());
            assertEquals(expectedOffer.getTerm(), actualOffer.getTerm());
            assertEquals(expectedOffer.getMonthlyPayment(), actualOffer.getMonthlyPayment());
            assertEquals(expectedOffer.getRate(), actualOffer.getRate());
            assertEquals(expectedOffer.getIsInsuranceEnabled(), actualOffer.getIsInsuranceEnabled());
            assertEquals(expectedOffer.getIsSalaryClient(), actualOffer.getIsSalaryClient());
        } else {
            assertNull(actualOffer);
        }
    }

    private void assertApplicationEquals(Application expectedApplication, Application actualApplication) {
        assertClientEquals(expectedApplication.getClient(), actualApplication.getClient());
        assertAppliedOfferEquals(expectedApplication.getAppliedOffer(), actualApplication.getAppliedOffer());
        assertCreditEquals(expectedApplication.getCredit(), actualApplication.getCredit());
        assertEquals(expectedApplication.getStatus(), actualApplication.getStatus());
    }

    @Test
    @Order(1)
    void testGetLoanOffers() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(client.getOffers(loanApplicationRequest))
                .thenReturn(Arrays.asList(expectedLoanOfferResponse));

        List<LoanOfferDTO> loanOfferDTOS = service.getLoanOffers(loanApplicationRequest, result);

        assertArrayEquals(expectedLoanOfferResponse, loanOfferDTOS.toArray());

        Application actualApplication = dataAccessService.getApplicationById(1L);

        assertOffersHaveApplicationId(loanOfferDTOS, actualApplication);
        assertApplicationEquals(expectedApplicationOnGetLoanOffersStep, actualApplication);
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
    @Order(2)
    void testChooseOffer() {
        expectedLoanOfferResponse[1].setApplicationId(1L);
        service.chooseOffer(expectedLoanOfferResponse[1]);
        Application actualApplication = dataAccessService.getApplicationById(1L);

        assertEquals(actualApplication.getStatus(), Status.APPROVED);
        assertAppliedOfferEquals(expectedApplicationOnChooseOfferStep.getAppliedOffer(),
                actualApplication.getAppliedOffer());
    }

    @Test
    void testChooseOfferWhenNullField() {
        expectedLoanOfferResponse[1].setApplicationId(1L);
        expectedLoanOfferResponse[1].setTerm(null);

        assertThrows(InvalidScoringDataException.class, () -> {
            service.chooseOffer(expectedLoanOfferResponse[1]);
        });
    }

    @Test
    @Order(3)
    void testFinishRegistration() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(client.getCredit(any()))
                .thenReturn(expectedCreditResponse);

        service.finishRegistration(finishRegistrationRequest, result, 1L);

        Application actualApplication = dataAccessService.getApplicationById(1L);

        assertApplicationEquals(expectedApplicationOnFinishRegistrationStep, actualApplication);
    }

    @Test
    void testFinishRegistrationWhenNullField() {
        BindingResult result = new BeanPropertyBindingResult(finishRegistrationRequest, "finishRegistrationRequest");
        result.addError(new FieldError("finishRegistrationRequest", "gender", ""));

        finishRegistrationRequest.setGender(null);

        assertThrows(InvalidFinishRegistrationRequestException.class, () -> {
            service.finishRegistration(finishRegistrationRequest, result, 1L);
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
        expectedCreditResponse = loadExpectedResponse("finish-registration/credit_response.json",
                CreditDTO.class);
        for (qiwi.deal.dto.PaymentScheduleElement payment : expectedCreditResponse.getPaymentSchedule()) {
            payment.setDate(LocalDate.now().plusMonths(payment.getNumber() - 1));
        }

        finishRegistrationRequest = loadExpectedResponse("finish-registration/finish_registration_request.json",
                FinishRegistrationRequestDTO.class);

        setExpectedApplicationOnGetLoanOffersStep();
        setExpectedApplicationOnChooseOfferStep();
        setExpectedApplicationOnFinishRegistrationStep();
    }

    private <T> T loadExpectedResponse(String resource, Class<T> tClass) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resource).getFile());
        return mapper.readValue(file, tClass);
    }

    private void setExpectedApplicationOnGetLoanOffersStep() throws IOException {
        expectedApplicationOnGetLoanOffersStep = loadExpectedResponse("get-loan-offers/application.json",
                Application.class);
        setApplicationFields(expectedApplicationOnGetLoanOffersStep);
    }

    private void setExpectedApplicationOnChooseOfferStep() throws IOException {
        expectedApplicationOnChooseOfferStep = loadExpectedResponse("choose-offer/application.json",
                Application.class);
        setApplicationFields(expectedApplicationOnChooseOfferStep);
        expectedApplicationOnChooseOfferStep.getAppliedOffer().setApplicationId(1L);
    }

    private void setExpectedApplicationOnFinishRegistrationStep() throws IOException {
        expectedApplicationOnFinishRegistrationStep = loadExpectedResponse("finish-registration/application.json",
                Application.class);
        setApplicationFields(expectedApplicationOnFinishRegistrationStep);

        expectedApplicationOnFinishRegistrationStep.getAppliedOffer().setApplicationId(1L);

        expectedApplicationOnFinishRegistrationStep.getCredit().setId(1L);
        expectedApplicationOnFinishRegistrationStep.getCredit().setCreditStatus(CALCULATED);
        expectedApplicationOnFinishRegistrationStep.getClient().getEmployment().setId(1L);
    }

    private void setApplicationFields(Application application) {
        application.setApplicationId(1L);
        application.setCreationDate(LocalDate.now());
        application.getClient().setId(1L);
        application.getClient().getPassport().setId(1L);
    }
}
