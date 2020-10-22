package main.api.requests;

import lombok.Data;

@Data
public class EditProfileRequestWithoutPhoto {
    private String name;
    private String email;
    private String password;
    private int removePhoto;
    private String photo;
}
