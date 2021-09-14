package Challonge.Rest;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RequestResponse {
    HttpStatus status;

    @JsonRawValue
    String message;

    public RequestResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public RequestResponse(HttpStatus status) {
        this.status = status;
    }
}
