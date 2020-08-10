package main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tag2post")
@AllArgsConstructor
@NoArgsConstructor
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @OneToOne
    @JoinColumn(name="id", referencedColumnName ="tag_id", insertable = false, updatable = false)
    private Tags tagId;
}
