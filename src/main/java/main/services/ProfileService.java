package main.services;

import com.github.cage.GCage;
import lombok.extern.slf4j.Slf4j;
import main.api.requests.*;
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
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

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

    public ResponseEntity<ProfileResponse> editProfile(EditProfileRequestWithPhoto editProfileRequestWithPhoto,
                                                       Principal principal) {
        String userEmail = principal.getName();
        ProfileResponse profileResponse = checkValuesFromRequest(userEmail, editProfileRequestWithPhoto, null, null, null);
        if (!profileResponse.isResult()) {
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

        profileResponse = changeUserProfile(editProfileRequestWithPhoto, userEmail);

        if (profileResponse.isResult()) {
            profileResponse.setErrors(null);
        }

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    public ResponseEntity<ProfileResponse> editProfile(EditProfileRequestWithoutPhoto editProfileRequestWithoutPhoto,
                                                       Principal principal) {
        String userEmail = principal.getName();
        ProfileResponse profileResponse = checkValuesFromRequest(userEmail, null, editProfileRequestWithoutPhoto, null, null);
        if (!profileResponse.isResult()) {
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);
        }

        profileResponse = changeUserProfile(editProfileRequestWithoutPhoto, userEmail);

        if (profileResponse.isResult()) {
            profileResponse.setErrors(null);
        }

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    public ResponseEntity<ProfileResponse> registerNewUser(NewProfileRequest profileRequest) {
        ProfileResponse profileResponse = checkValuesFromRequest(null, null, null, profileRequest, null);
        if (!profileResponse.isResult()) {
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

            String captchaID = UUID.randomUUID().toString();
            captchaResponse.setSecret(captchaID);
            captchaResponse.setImage("data:image/png;base64, " + encodedString);

            CaptchaCode captchaCode = new CaptchaCode();
            captchaCode.setSecretCode(captchaID);
            captchaCode.setCode(secretCode);
            captchaCode.setTime(new Date());

            captchaCodesRepository.save(captchaCode);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            os.flush();
            os.close();
        }
        file.delete();

        return new ResponseEntity<>(captchaResponse, HttpStatus.OK);
    }

    public ResponseEntity<RestorePasswordResponse> restorePassword(RestorePasswordRequest restorePasswordRequest,
                                                                   String appUrl) {
        User userByEmail = userRepository.findByEmail(restorePasswordRequest.getEmail());

        if (userByEmail == null) {
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
        passwordResetEmail.setText("Чтобы сменить пароль, кликните по ссылке: "
                + appUrl + "/login/change-password/" + userByEmail.getCode());

        emailService.sendEmail(passwordResetEmail);

        RestorePasswordResponse restorePasswordResponse = new RestorePasswordResponse();
        restorePasswordResponse.setResult(true);

        return new ResponseEntity<>(restorePasswordResponse, HttpStatus.OK);
    }

    public ResponseEntity<ProfileResponse> changePassword(ChangePasswordRequest changePasswordRequest) {
        ProfileResponse profileResponse = checkValuesFromRequest(null, null, null, null, changePasswordRequest);
        if (!profileResponse.isResult()) {
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

    protected ProfileResponse checkValuesFromRequest(String userEmail,
                                                     EditProfileRequestWithPhoto profileRequest,
                                                     EditProfileRequestWithoutPhoto profileRequestWithoutPhoto,
                                                     NewProfileRequest newProfileRequest,
                                                     ChangePasswordRequest changePasswordRequest) {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        HashMap<String, String> errors = new HashMap<>();
        profileResponse.setErrors(errors);

        if (profileRequest != null) {
            String emailFromRequest = profileRequest.getEmail();
            if (!userEmail.equals(emailFromRequest)) {
                User byEmail = userRepository.findByEmail(emailFromRequest);
                if (byEmail != null) {
                    profileResponse.setResult(false);
                    errors.put("email", "Этот e-mail уже зарегистрирован.");
                }
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
        } else if (profileRequestWithoutPhoto != null) {
            String emailFromRequest = profileRequestWithoutPhoto.getEmail();
            if (!userEmail.equals(emailFromRequest)) {
                User byEmail = userRepository.findByEmail(emailFromRequest);
                if (byEmail != null) {
                    profileResponse.setResult(false);
                    errors.put("email", "Этот e-mail уже зарегистрирован.");
                }
            }

            String nameFromRequest = profileRequestWithoutPhoto.getName();
            if (nameFromRequest.isEmpty()) {
                profileResponse.setResult(false);
                errors.put("name", "Имя указано неверно.");
            }


            String passwordFromRequest = profileRequestWithoutPhoto.getPassword();
            if (passwordFromRequest != null) {
                if (passwordFromRequest.length() < 6) {
                    profileResponse.setResult(false);
                    errors.put("password", "Пароль короче 6-ти символов");
                }
            }

            return profileResponse;
        } else if (newProfileRequest != null) {
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
            String captchaID = newProfileRequest.getCaptcha_secret();
            boolean isPassed = checkCaptchaFromRequest(captcha, captchaID);

            if (!isPassed) {
                profileResponse.setResult(false);
                errors.put("captcha", "Код с картинки введён неверно.");
            }

            return profileResponse;
        } else if (changePasswordRequest != null) {
            String passwordFromRequest = changePasswordRequest.getPassword();
            if (passwordFromRequest != null) {
                if (passwordFromRequest.length() < 6) {
                    profileResponse.setResult(false);
                    errors.put("password", "Пароль короче 6-ти символов.");
                }
            }

            String captcha = changePasswordRequest.getCaptcha();
            String captchaID = changePasswordRequest.getCaptchaSecret();
            boolean isPassed = checkCaptchaFromRequest(captcha, captchaID);

            if (!isPassed) {
                profileResponse.setResult(false);
                errors.put("captcha", "Код с картинки введён неверно.");
            }

            return profileResponse;
        } else {
            profileResponse.setResult(false);
            errors.put("server", "Произошла ошибка на сервере");
            return profileResponse;
        }
    }

    protected boolean checkCaptchaFromRequest(String captchaFromRequest, String captchaID) {
        CaptchaCode captchaCodeBySecretCode = captchaCodesRepository.getCaptchaCodeBySecretCode(captchaID);

        return captchaCodeBySecretCode.getCode().equals(captchaFromRequest);
    }

    protected ProfileResponse changeUserProfile(EditProfileRequestWithPhoto editProfileRequestWithPhoto,
                                                String userEmail) {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        HashMap<String, String> errors = new HashMap<>();
        profileResponse.setErrors(errors);

        User user = userRepository.findByEmail(userEmail);
        user.setName(editProfileRequestWithPhoto.getName());
        user.setEmail(editProfileRequestWithPhoto.getEmail());

        if (editProfileRequestWithPhoto.getPassword() != null) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encode = bCryptPasswordEncoder.encode(editProfileRequestWithPhoto.getPassword());
            user.setPassword(encode);
        }


        if (editProfileRequestWithPhoto.getRemovePhoto() == 0) {
            String pathToPhoto = saveUserPhoto(editProfileRequestWithPhoto.getPhoto());
            if (pathToPhoto == null) {
                profileResponse.setResult(false);
                errors.put("photo", "Произошла ошибка при сохранении фотографии.");
                return profileResponse;
            }
            user.setPhoto(pathToPhoto);
        }

        userRepository.save(user);

        profileResponse.setResult(true);
        return profileResponse;
    }

    protected ProfileResponse changeUserProfile(EditProfileRequestWithoutPhoto editProfileRequestWithoutPhoto,
                                                String userEmail) {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setResult(true);
        HashMap<String, String> errors = new HashMap<>();
        profileResponse.setErrors(errors);

        User user = userRepository.findByEmail(userEmail);
        user.setName(editProfileRequestWithoutPhoto.getName());
        user.setEmail(editProfileRequestWithoutPhoto.getEmail());

        if (editProfileRequestWithoutPhoto.getPassword() != null) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encode = bCryptPasswordEncoder.encode(editProfileRequestWithoutPhoto.getPassword());
            user.setPassword(encode);
        }

        if (editProfileRequestWithoutPhoto.getRemovePhoto() == 1) {
            boolean isPhotoDeleted = deleteUserPhoto(user.getPhoto());
            if (!isPhotoDeleted) {
                profileResponse.setResult(false);
                errors.put("photo", "Произошла ошибка при удалении фотографии.");
                return profileResponse;
            }
            user.setPhoto(null);
        }


        userRepository.save(user);

        profileResponse.setResult(true);
        return profileResponse;
    }

    protected String saveUserPhoto(MultipartFile photo) {
        Object pathToPhoto = imageService.uploadImageOnServer(photo, "usersPhoto");
        if (pathToPhoto.getClass().getName().equals("java.lang.String")) {
            return (String) pathToPhoto;
        } else {
            return null;
        }
    }

    protected boolean deleteUserPhoto(String pathToPhoto) {
        return imageService.deleteUserPhotoFromServer(pathToPhoto);
    }

    protected ProfileResponse saveNewUser(NewProfileRequest profileRequest) {
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
