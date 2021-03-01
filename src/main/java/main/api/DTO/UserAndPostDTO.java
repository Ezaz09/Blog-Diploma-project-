package main.api.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Post;
import main.model.User;

@Data
@NoArgsConstructor
public class UserAndPostDTO {
    private User user;
    private Post post;
}
