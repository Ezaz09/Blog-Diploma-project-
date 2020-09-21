package main.api.responses.user_response;

import lombok.Data;
import main.api.responses.ErrorResponse;

@Data
public class ProfileResponse {
    private boolean result;
    private ErrorResponse errors;


}
