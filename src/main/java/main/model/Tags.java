package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tags")
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column(length = 255, nullable = false)
    private String name;

}
