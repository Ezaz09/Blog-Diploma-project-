package main.api.responses.post_responses;

import lombok.Data;
import main.api.responses.ErrorResponse;

@Data
public class EditPostByModeratorResponse {
    private boolean result;
    private ErrorResponse errors;
}
