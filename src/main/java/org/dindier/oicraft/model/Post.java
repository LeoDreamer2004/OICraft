package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The post model
 */
@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Comment> comments;

    @Transient
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Post(String title, String content, Problem problem, User author) {
        this.title = title;
        this.content = content;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.problem = problem;
        this.author = author;
        this.comments = new ArrayList<>();
    }

    protected Post() {
    }

    public String getTimeStampString() {
        return sdf.format(createTime);
    }
}
