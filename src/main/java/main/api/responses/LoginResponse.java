package main.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.api.responses.user_response.UserLoginResponse;

@Data
public class LoginResponse {
    private boolean result;
    @JsonProperty("user")
    private UserLoginResponse userLoginResponse;

}
