package main.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name="posts")
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponse{

    @Id
    @NonNull
    private int id;

    @Column(name="time_of_post")
    private String time;

    @ManyToOne
    @JoinColumn(name="user", referencedColumnName="id", insertable=false, updatable=false)
    private UserResponse user;

    private String title;

   // private String announce;

    @Column(name="like_count")
    private int likeCount;

    @Column(name="dislike_count")
    private int dislikeCount;

    @Column(name="comment_count")
    private int commentCount;

    //@Formula("sum(comments)")
    @Column(name="view_count")
    private int viewCount;

}
