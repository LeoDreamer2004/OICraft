package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date create_time;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Post post;

    public Comment(int id, String content, Date create_time, User user, Post post) {
        this.id = id;
        this.content = content;
        this.create_time = create_time;
        this.user = user;
        this.post = post;
    }

    public Comment() {
    }
}
