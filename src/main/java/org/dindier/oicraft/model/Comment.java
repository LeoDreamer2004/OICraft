package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Post post;

    public Comment(User user, Post post, String content) {
        this.content = content;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.user = user;
        this.post = post;
    }

    public Comment() {
    }
}
