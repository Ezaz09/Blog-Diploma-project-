package main.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewProfileRequest {
    private String name;
    @JsonProperty("e_mail")
    private String email;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captcha_secret;
}
