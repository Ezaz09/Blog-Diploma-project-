package main.api.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EditProfileRequestWithPhoto {
    private String name;
    private String email;
    private String password;
    private int removePhoto;
    private MultipartFile photo;
}
