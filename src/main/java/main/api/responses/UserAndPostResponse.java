package main.api.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Post;
import main.model.User;

@Data
@NoArgsConstructor
public class UserAndPostResponse {
    private User user;
    private Post post;
}
