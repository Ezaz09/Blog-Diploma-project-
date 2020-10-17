package main.api.responses.user_response;

import lombok.Data;

import java.util.HashMap;

@Data
public class ProfileResponse {
    private boolean result;
    private HashMap<String, String> errors;
}
