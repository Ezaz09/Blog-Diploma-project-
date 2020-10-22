package main.api.responses.post_responses;

import lombok.Data;

import java.util.HashMap;

@Data
public class EditPostResponse {
    private boolean result;
    private HashMap<String, String> errors;
}
