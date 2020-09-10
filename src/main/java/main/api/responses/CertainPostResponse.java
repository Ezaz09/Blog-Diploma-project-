package main.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertainPostResponse {

    private int id;

    private Long timestamp;

    private UserResponse user;

    private String title;

    private String text;

    private int likeCount;

    private int dislikeCount;

    private int viewCount;

    private List<CommentResponse> comments;

    private List<String> tags;
}
