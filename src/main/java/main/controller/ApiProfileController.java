package main.controller;

import main.api.requests.EditProfileRequestWithPhoto;
import main.api.requests.EditProfileRequestWithoutPhoto;
import main.api.responses.user_response.ProfileResponse;
import main.services.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ProfileResponse> editProfile(@RequestBody EditProfileRequestWithoutPhoto editProfileRequest,
                                                       Principal principal)
    {
        ProfileResponse profileResponse = checkAuthentication(principal);

        if(!profileResponse.isResult())
        {
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

        return profileService.editProfile(editProfileRequest, principal);
    }

    @PostMapping(path = "/my",  consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProfileResponse> editProfileWithPhoto(@ModelAttribute EditProfileRequestWithPhoto editProfileRequestWithPhoto,
                                                                Principal principal)
    {
        ProfileResponse profileResponse = checkAuthentication(principal);

        if(!profileResponse.isResult())
        {
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

        return profileService.editProfile(editProfileRequestWithPhoto, principal);
    }

    private ProfileResponse checkAuthentication(Principal principal)
    {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);

        if (principal == null)
        {
            profileResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>();
            errors.put("authorize", "Пользователь не авторизован");

            profileResponse.setErrors(errors);
        }

        return profileResponse;
    }

}
