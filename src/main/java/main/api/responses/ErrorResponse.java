package main.api.responses;

import lombok.Data;

import java.util.HashMap;

@Data
public class ErrorResponse {
    private HashMap<String, String> errors;
}
