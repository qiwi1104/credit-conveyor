package qiwi.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDTO {
    @Schema(example = "Error on fields: [term]")
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorMessageDTO)) return false;

        ErrorMessageDTO response = (ErrorMessageDTO) o;

        return message.equals(response.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
}
