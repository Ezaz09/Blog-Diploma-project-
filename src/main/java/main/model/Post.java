package main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.enums.ModerationStatus;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column(name = "is_active", columnDefinition = "TINYINT", nullable = false)
    private int isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status",  nullable = false)
    private ModerationStatus moderationStatus;

    @ManyToOne
    @JoinColumn(name="moderator_id", referencedColumnName="id", updatable=false)
    private User moderator;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="id", updatable=false)
    private User user;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @OneToMany
    @JoinColumn(name="post_id", referencedColumnName ="id", updatable = false)
    @Where(clause = "value = 1")
    private List<PostVote> likeVotes;

    @OneToMany
    @JoinColumn(name="post_id", referencedColumnName ="id", updatable = false)
    @Where(clause = "value = -1")
    private List<PostVote> dislikeVotes;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @OneToMany
    @JoinColumn(name="post_id", referencedColumnName ="id", updatable = false)
    private List<PostComment> comments;

    @OneToMany
    @JoinColumn(name="post_id", referencedColumnName = "id", updatable = false)
    private List<Tag2Post> tags2Post;

}
