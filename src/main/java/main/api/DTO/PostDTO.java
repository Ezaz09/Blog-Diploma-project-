package main.api.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.responses.user_response.UserResponse;

@Data
@NoArgsConstructor
public class PostDTO {
    private int id;
    private Long timestamp;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private UserResponse user;
}
