package main.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private int id;

    private Long timestamp;

    private UserResponse user;

    private String title;

    // private String announce;

    private int likeCount;

    private int dislikeCount;

    private int commentCount;

    private int viewCount;
}
