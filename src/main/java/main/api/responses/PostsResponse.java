package main.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Users;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponse {

    private int id;

    private LocalDate time;

    private int user;

    private String title;

    private String announce;

    private int likeCount;

    private int dislikeCount;

    private int commentCount;

    private int viewCount;

}
