package main.api.responses;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CertainPostResponse {
    @Id
    @NonNull
    private int id;

    private String time;

    @ManyToOne
    @JoinColumn(name="id", referencedColumnName="id", insertable=false, updatable=false)
    private UserResponse user;

    private String title;

    // private String announce;

    @Column(name="like_count")
    private int likeCount;

    @Column(name="dislike_count")
    private int dislikeCount;

    @Column(name="comment_count")
    private int commentCount;

    @Column(name="view_count")
    private int viewCount;

    @OneToMany
    @JoinColumn(name="id", referencedColumnName ="id", insertable = false, updatable = false)
    private List<CommentsResponse> comments;
}
