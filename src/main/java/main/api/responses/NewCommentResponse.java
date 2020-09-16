package main.api.responses;

import lombok.Data;

@Data
public class NewCommentResponse {
    private int id;
    private boolean result;
    private ErrorResponse errors;
}
