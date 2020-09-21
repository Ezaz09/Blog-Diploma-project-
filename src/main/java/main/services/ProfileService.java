package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.EditProfileRequest;
import main.api.requests.NewProfileRequest;
import main.api.responses.ErrorResponse;
import main.api.responses.user_response.ProfileResponse;
import main.model.User;
import main.model.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;

@Slf4j
@Service
public class ProfileService {
    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<ProfileResponse> editProfile(EditProfileRequest editProfileRequest,
                                                       Principal principal) {
        ProfileResponse profileResponse = checkValuesFromRequest(editProfileRequest, null);
        if(!profileResponse.isResult())
        {
            return new ResponseEntity<>(profileResponse, HttpStatus.BAD_REQUEST);
        }

        profileResponse = changeUserProfile(editProfileRequest, principal);

        if(profileResponse.isResult())
        {
            profileResponse.setErrors(null);
        }

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    public ResponseEntity<ProfileResponse> registerNewUser(NewProfileRequest profileRequest)
    {
        ProfileResponse profileResponse = checkValuesFromRequest(null, profileRequest);
        if(!profileResponse.isResult())
        {
            return new ResponseEntity<>(profileResponse, HttpStatus.BAD_REQUEST);
        }

        profileResponse = saveNewUser(profileRequest);

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    protected ProfileResponse checkValuesFromRequest(EditProfileRequest profileRequest,
                                                     NewProfileRequest newProfileRequest)
    {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        HashMap<String, String> errors = new HashMap<>();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(errors);
        profileResponse.setErrors(errorResponse);

        if(profileRequest != null) {
            String emailFromRequest = profileRequest.getEmail();
            User byEmail = userRepository.findByEmail(emailFromRequest);
            if (byEmail != null) {
                profileResponse.setResult(false);
                errors.put("email", "Этот e-mail уже зарегистрирован.");
            }

            String nameFromRequest = profileRequest.getName();
            if (nameFromRequest.isEmpty()) {
                profileResponse.setResult(false);
                errors.put("name", "Имя указано неверно.");
            }


            String passwordFromRequest = profileRequest.getPassword();
            if (passwordFromRequest != null) {
                if (passwordFromRequest.length() < 6) {
                    profileResponse.setResult(false);
                    errors.put("password", "Пароль короче 6-ти символов");
                }
            }

            MultipartFile photoFromRequest = profileRequest.getPhoto();
            if (photoFromRequest != null) {
                long size = photoFromRequest.getSize();
                if (size > 5_242_880) {
                    profileResponse.setResult(false);
                    errors.put("photo", "Фото слишком большое, нужно не более 5 Мб.");
                }
            }

            return profileResponse;
        }
        else if( newProfileRequest != null )
        {
            String emailFromRequest = newProfileRequest.getEmail();
            User byEmail = userRepository.findByEmail(emailFromRequest);
            if (byEmail != null) {
                profileResponse.setResult(false);
                errors.put("email", "Этот e-mail уже зарегистрирован.");
            }

            String nameFromRequest = newProfileRequest.getName();
            if (nameFromRequest.isEmpty()) {
                profileResponse.setResult(false);
                errors.put("name", "Имя указано неверно.");
            }


            String passwordFromRequest = newProfileRequest.getPassword();
            if (passwordFromRequest != null) {
                if (passwordFromRequest.length() < 6) {
                    profileResponse.setResult(false);
                    errors.put("password", "Пароль короче 6-ти символов");
                }
            }

            return profileResponse;
        }
        else
        {
            profileResponse.setResult(false);
            errors.put("server", "Произошла ошибка на сервере");
            return profileResponse;
        }
    }

    protected ProfileResponse changeUserProfile(EditProfileRequest editProfileRequest,
                                                Principal principal)
    {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        HashMap<String, String> errors = new HashMap<>();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(errors);
        profileResponse.setErrors(errorResponse);

        User user = userRepository.findByEmail(principal.getName());
        user.setName(editProfileRequest.getName());
        user.setEmail(editProfileRequest.getEmail());

        if(editProfileRequest.getPassword() != null)
        {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encode = bCryptPasswordEncoder.encode(editProfileRequest.getPassword());
            user.setPassword(encode);
        }


        if(editProfileRequest.getPhoto() != null)
        {
            if(editProfileRequest.getRemovePhoto() == 0)
            {
                String pathToPhoto = saveUserPhoto(editProfileRequest.getPhoto());
                if(pathToPhoto == null)
                {
                    profileResponse.setResult(false);
                    errors.put("photo", "Произошла ошибка при сохранении фотографии.");
                    return profileResponse;
                }
                user.setPhoto(pathToPhoto);
            }
            else
            {
                boolean isPhotoDeleted = deleteUserPhoto(user.getEmail());
                if(!isPhotoDeleted)
                {
                    profileResponse.setResult(false);
                    errors.put("photo", "Произошла ошибка при удалении фотографии.");
                    return profileResponse;
                }
                user.setPhoto(null);
            }

        }



        userRepository.save(user);

        profileResponse.setResult(true);
        return profileResponse;
    }

    protected String saveUserPhoto(MultipartFile photo)
    {
        return null;
    }

    protected boolean deleteUserPhoto(String email)
    {
        return false;
    }

    protected ProfileResponse saveNewUser(NewProfileRequest profileRequest)
    {
        User newUser = new User();
        newUser.setEmail(profileRequest.getEmail());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(profileRequest.getPassword());
        newUser.setPassword(encode);

        newUser.setName(profileRequest.getName());
        newUser.setCode(profileRequest.getCaptcha());
        newUser.setRegTime(LocalDate.now());

        userRepository.save(newUser);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);

        return profileResponse;
    }
}
