package qiwi.deal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDTO {
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
