package main.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {
    private String name;
    private String email;
    private String password;
    private int removePhoto;
    private MultipartFile photo;
}
