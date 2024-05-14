package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date create_time;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Comment> comments;

    public Post(int id, String title, String content, Date create_time, Problem problem, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.create_time = create_time;
        this.problem = problem;
        this.user = user;
    }

    public Post() {
    }
}
