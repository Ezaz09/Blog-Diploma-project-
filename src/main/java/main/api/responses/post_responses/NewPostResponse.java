package main.api.responses.post_responses;

import lombok.Data;

import java.util.HashMap;

@Data
public class NewPostResponse {
    private boolean result;
    private HashMap<String, String> errors;
}
