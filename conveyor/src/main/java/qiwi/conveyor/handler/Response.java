package qiwi.conveyor.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;

        Response response = (Response) o;

        return message.equals(response.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
}
