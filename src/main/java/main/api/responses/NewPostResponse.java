package main.api.responses;

import lombok.Data;

@Data
public class NewPostResponse {
    private boolean result;
    private ErrorResponse errors;
}
