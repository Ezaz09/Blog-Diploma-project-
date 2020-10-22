package main.api.responses;

import lombok.Data;
import java.util.HashMap;

@Data
public class NewCommentResponse {
    private int id;
    private boolean result;
    private HashMap<String, String> errors;
}
