package main.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @JsonProperty("e_mail")
    private String email;

    private String name;

    private String password;

    private String captcha;

    private String captcha_secret;
}
