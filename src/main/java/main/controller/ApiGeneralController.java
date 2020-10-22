package main.controller;

import main.api.requests.ChangeGlobalSettingsRequest;
import main.api.responses.ChangeGlobalSettingsResponse;
import main.api.responses.LogoutResponse;
import main.api.responses.ResponseInit;
import main.api.responses.SettingsResponse;
import main.services.GlobalSettingsService;
import main.services.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;


@RestController
public class ApiGeneralController {
    private GlobalSettingsService globalSettingsService;

    public ApiGeneralController(GlobalSettingsService globalSettingsService) {
        this.globalSettingsService = globalSettingsService;
    }

    @GetMapping(path = "/api/init")
    public ResponseEntity<ResponseInit> init() {
        ResponseInit responseInit = ResponseInit.builder()
                .title("DevPub")
                .subtitle("Рассказы разработчиков")
                .phone("8 800 555 35 35")
                .email("DevStories@gmail.com")
                .copyright("Донцов Владимир")
                .copyrightFrom("2020").build();
        return new ResponseEntity<>(responseInit, HttpStatus.OK);
    }

    @GetMapping(path = "/api/settings")
    public ResponseEntity<SettingsResponse> getGlobalSettings() {
        return globalSettingsService.getGlobalSettings();
    }

    @PutMapping(path = "/api/settings")
    public ResponseEntity<ChangeGlobalSettingsResponse> changeGlobalSettings(@RequestBody ChangeGlobalSettingsRequest changeGlobalSettingsRequest,
                                                                             Principal principal) {
        if (principal == null) {
            ChangeGlobalSettingsResponse changeGlobalSettingsResponse = new ChangeGlobalSettingsResponse();
            changeGlobalSettingsResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("логин", "Пользователь не авторизован!");
            return new ResponseEntity<>(changeGlobalSettingsResponse, HttpStatus.FOUND);
        }

        return globalSettingsService.changeGlobalSettings(changeGlobalSettingsRequest, principal.getName());
    }
}
