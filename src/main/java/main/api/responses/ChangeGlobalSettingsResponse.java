package main.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeGlobalSettingsResponse {
    private boolean result;
    private HashMap<String, String> errors;
}
