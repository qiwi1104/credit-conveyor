//package qiwi.deal.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import feign.Response;
//import feign.codec.ErrorDecoder;
//import org.springframework.stereotype.Component;
//import qiwi.conveyor.dto.ErrorMessageDTO;
//import qiwi.conveyor.exceptions.InvalidLoanApplicationRequestException;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//@Component
//public class ErrorMessageDecoder implements ErrorDecoder {
//    @Override
//    public Exception decode(String s, Response response) {
//        ErrorMessageDTO message;
//
//        try (InputStream bodyIs = response.body()
//                .asInputStream()) {
//            ObjectMapper mapper = new ObjectMapper();
//            message = mapper.readValue(bodyIs, ErrorMessageDTO.class);
//        } catch (IOException e) {
//            return new Exception(e.getMessage());
//        }
//
//        if (response.status() == 400) {
//            return new InvalidLoanApplicationRequestException(
//                    message.getMessage() != null
//                            ? message.getMessage()
//                            : "Bad Request");
//        }
//
//        return new Default().decode(s, response);
//    }
//}
