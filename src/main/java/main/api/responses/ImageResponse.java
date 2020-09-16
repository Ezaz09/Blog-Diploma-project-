package main.api.responses;

import lombok.Data;

@Data
public class ImageResponse {
    private boolean result;
    private ErrorResponse errors;
}
