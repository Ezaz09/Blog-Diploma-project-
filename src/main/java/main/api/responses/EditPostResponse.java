package main.api.responses;

import lombok.Data;

@Data
public class EditPostResponse {
    private boolean result;
    private ErrorResponse errors;
}
