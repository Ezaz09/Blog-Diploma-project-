package main.controller;

import main.api.requests.EditProfileRequest;
import main.api.responses.user_response.ProfileResponse;
import main.services.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/api/profile")
public class ApiProfileController {

    private final ProfileService profileService;

    public ApiProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping(path = "/my")
    public ResponseEntity<ProfileResponse> editProfile(@ModelAttribute EditProfileRequest editProfileRequest,
                                                       Principal principal)
    {
        if (principal == null){
            ProfileResponse profileResponse = new ProfileResponse();
            profileResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>();
            errors.put("authorize", "Пользователь не авторизован");

            profileResponse.setErrors(errors);
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

       return profileService.editProfile(editProfileRequest, principal);
    }
}
