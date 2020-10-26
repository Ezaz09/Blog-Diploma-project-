package main.controller;

import main.api.requests.ChangePasswordRequest;
import main.api.requests.LoginRequest;
import main.api.requests.NewProfileRequest;
import main.api.requests.RestorePasswordRequest;
import main.api.responses.CaptchaResponse;
import main.api.responses.LoginResponse;
import main.api.responses.LogoutResponse;
import main.api.responses.RestorePasswordResponse;
import main.api.responses.user_response.ProfileResponse;
import main.api.responses.user_response.UserLoginResponse;
import main.model.repositories.UserRepository;
import main.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ProfileService profileService;



    @Autowired
    public ApiAuthController(AuthenticationManager authenticationManager,
                             UserRepository userRepository,
                             ProfileService profileService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.profileService = profileService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();


        return ResponseEntity.ok(getLoginResponse(user.getUsername()));
    }

    @GetMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(Principal principal) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/");

        if (principal == null) {
            LogoutResponse logoutResponse = new LogoutResponse();
            logoutResponse.setResult(false);
            return new ResponseEntity<>(logoutResponse, headers, HttpStatus.FOUND);
        }

        SecurityContextHolder
                .getContext()
                .getAuthentication()
                .setAuthenticated(false);

        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setResult(true);

        return new ResponseEntity<>(logoutResponse, headers, HttpStatus.OK);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() throws IOException {
        return profileService.generateCaptcha();
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(getLoginResponse(principal.getName()));
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User currentUser = userRepository.findByEmail(email);

        if (currentUser == null) {
            throw new UsernameNotFoundException(email);
        }

        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setEmail(currentUser.getEmail());
        userResponse.setPhoto(currentUser.getPhoto());
        userResponse.setName(currentUser.getName());
        userResponse.setModeration(currentUser.getIsModerator() == 1);
        userResponse.setId(currentUser.getId());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userResponse);

        return loginResponse;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ProfileResponse> register(@RequestBody NewProfileRequest profileRequest) {
        return profileService.registerNewUser(profileRequest);
    }

    @PostMapping(path = "/restore")
    public ResponseEntity<RestorePasswordResponse> restorePassword(@RequestBody RestorePasswordRequest restorePasswordRequest,
                                                                   HttpServletRequest request) {
        String appUrl;
        if(request.getServerName().equals("localhost")) {
            appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        } else {
            appUrl = request.getScheme() + "://" + request.getServerName();
        }

        return profileService.restorePassword(restorePasswordRequest, appUrl);
    }

    @PostMapping(path = "/password")
    public ResponseEntity<ProfileResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return profileService.changePassword(changePasswordRequest);
    }

}
