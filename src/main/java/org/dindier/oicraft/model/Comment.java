package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * The comment under a post
 */
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
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Post post;

    public Comment(User author, Post post, String content) {
        this.content = content;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.author = author;
        this.post = post;
    }

    public Comment() {
    }
}
