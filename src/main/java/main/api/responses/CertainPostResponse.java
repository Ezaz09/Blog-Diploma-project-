package main.api.responses;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertainPostResponse {

    private int id;

    private String time;

    private UserResponse user;

    private String title;

    // private String announce;

    private int likeCount;

    private int dislikeCount;

    private int commentCount;

    private int viewCount;

    private List<CommentsResponse> comments;
}
