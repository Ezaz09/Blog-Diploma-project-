package main.services;

import com.github.cage.GCage;
import lombok.extern.slf4j.Slf4j;
import main.api.requests.ChangePasswordRequest;
import main.api.requests.EditProfileRequest;
import main.api.requests.NewProfileRequest;
import main.api.requests.RestorePasswordRequest;
import main.api.responses.CaptchaResponse;
import main.api.responses.RestorePasswordResponse;
import main.api.responses.user_response.ProfileResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repositories.CaptchaCodesRepository;
import main.model.repositories.UserRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final CaptchaCodesRepository captchaCodesRepository;
    private final EmailService emailService;
    private final ImageService imageService;

    @Autowired
    public ProfileService(UserRepository userRepository,
                          CaptchaCodesRepository captchaCodesRepository,
                          EmailService emailService,
                          ImageService imageService) {
        this.userRepository = userRepository;
        this.captchaCodesRepository = captchaCodesRepository;
        this.emailService = emailService;
        this.imageService = imageService;
    }

    public ResponseEntity<ProfileResponse> editProfile(EditProfileRequest editProfileRequest,
                                                       Principal principal) {
        ProfileResponse profileResponse = checkValuesFromRequest(editProfileRequest, null, null);
        if(!profileResponse.isResult())
        {
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
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
        ProfileResponse profileResponse = checkValuesFromRequest(null, profileRequest, null);
        if(!profileResponse.isResult())
        {
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

        profileResponse = saveNewUser(profileRequest);

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    public ResponseEntity<CaptchaResponse> generateCaptcha() throws IOException {
        GCage gCage = new GCage();
        OutputStream os = new FileOutputStream("captcha.jpg", false);
        File file = new File("captcha.jpg");
        CaptchaResponse captchaResponse = new CaptchaResponse();

        try {
            String secretCode = gCage.getTokenGenerator().next();

            BufferedImage originalImage = gCage.drawImage(secretCode);
            BufferedImage resizedImage = new BufferedImage(100, 35, 5);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, 100, 35, null);
            g.dispose();
            ImageIO.write(resizedImage, "jpg", os);

            byte[] fileContent = FileUtils.readFileToByteArray(file);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            captchaResponse.setSecret(secretCode);
            captchaResponse.setImage("data:image/png;base64, " + encodedString);

            CaptchaCode captchaCode = new CaptchaCode();
            captchaCode.setSecretCode(secretCode);
            captchaCode.setCode(encodedString);
            captchaCode.setTime(new Date());

            captchaCodesRepository.save(captchaCode);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally {
            os.flush();
            os.close();
        }
        file.delete();

        return new ResponseEntity<>(captchaResponse, HttpStatus.OK);
    }

    public ResponseEntity<RestorePasswordResponse> restorePassword(RestorePasswordRequest restorePasswordRequest,
                                                                   String appUrl)
    {
        User userByEmail = userRepository.findByEmail(restorePasswordRequest.getEmail());

        if(userByEmail == null)
        {
            RestorePasswordResponse restorePasswordResponse = new RestorePasswordResponse();
            restorePasswordResponse.setResult(false);
            return new ResponseEntity<>(restorePasswordResponse, HttpStatus.OK);
        }

        String resetCode = UUID.randomUUID().toString();
        userByEmail.setCode(resetCode);

        userRepository.save(userByEmail);

        SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
        passwordResetEmail.setTo(restorePasswordRequest.getEmail());
        passwordResetEmail.setSubject("Запрос на восстановление пароля");
        passwordResetEmail.setText("Чтобы сменить пароль, кликните по ссылке:\\n "
                + appUrl + "/login/change-password/" + userByEmail.getCode());

        emailService.sendEmail(passwordResetEmail);

        RestorePasswordResponse restorePasswordResponse = new RestorePasswordResponse();
        restorePasswordResponse.setResult(true);

        return new ResponseEntity<>(restorePasswordResponse, HttpStatus.OK);
    }

    public ResponseEntity<ProfileResponse> changePassword(ChangePasswordRequest changePasswordRequest)
    {
        ProfileResponse profileResponse = checkValuesFromRequest(null, null, changePasswordRequest);
        if(!profileResponse.isResult())
        {
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

        String resetCode = changePasswordRequest.getCode();
        User userByResetCode = userRepository.findByResetCode(resetCode);

        if (userByResetCode == null) {
            profileResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("code", "код восстановления пароля неверный или устарел." +
                    "<a href=\n" +
                    "\t\t\t\t\\\"/auth/restore\\\">Запросить ссылку снова</a>\"");
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(changePasswordRequest.getPassword());
        userByResetCode.setPassword(encode);

        userRepository.save(userByResetCode);

        profileResponse.setResult(true);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    protected  boolean checkCaptcha(String captcha)
    {

        return true;
    }

    protected ProfileResponse checkValuesFromRequest(EditProfileRequest profileRequest,
                                                     NewProfileRequest newProfileRequest,
                                                     ChangePasswordRequest changePasswordRequest)
    {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        HashMap<String, String> errors = new HashMap<>();
        profileResponse.setErrors(errors);

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
                    errors.put("password", "Пароль короче 6-ти символов.");
                }
            }

            String captcha = newProfileRequest.getCaptcha();
            CaptchaCode captchaCodeBySecretCode = captchaCodesRepository.getCaptchaCodeBySecretCode(captcha);
            if(captchaCodeBySecretCode == null) {
                profileResponse.setResult(false);
                errors.put("captcha", "Код с картинки введён неверно.");
            }

            return profileResponse;
        }
        else if( changePasswordRequest != null )
        {
            String passwordFromRequest = changePasswordRequest.getPassword();
            if (passwordFromRequest != null) {
                if (passwordFromRequest.length() < 6) {
                    profileResponse.setResult(false);
                    errors.put("password", "Пароль короче 6-ти символов.");
                }
            }

            String captcha = changePasswordRequest.getCaptcha();
            CaptchaCode captchaCodeBySecretCode = captchaCodesRepository.getCaptchaCodeBySecretCode(captcha);
            if(captchaCodeBySecretCode == null) {
                profileResponse.setResult(false);
                errors.put("captcha", "Код с картинки введён неверно.");
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
        profileResponse.setErrors(errors);

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
        Object pathToPhoto = imageService.UploadImageOnServer(photo, "usersPhoto");
        if (pathToPhoto.getClass().getName().equals("java.lang.String"))
        {
            return (String) pathToPhoto;
        }
        else
        {
            return null;
        }
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
        newUser.setRegTime(LocalDate.now());

        userRepository.save(newUser);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);

        return profileResponse;
    }

}
