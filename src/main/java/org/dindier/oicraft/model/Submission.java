package org.dindier.oicraft.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id")
    private Problem problem;
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "submission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Checkpoint> checkpoints;

    protected Submission() {
    }

    public enum Language {
        JAVA("java"), PYTHON("python"), C("c"), CPP("cpp");

        private final String displayName;

        Language(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Enumerated(EnumType.STRING)
    private Language language;

    public enum Status {
        WAITING("waiting"), PASSED("passed"), FAILED("failed");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Enumerated(EnumType.STRING)
    private Status status;
    private int score;

    public Submission(Problem problem, String code, Language language) {
        this.problem = problem;
        this.code = code;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public int getProblemId() {
        return problem.getId();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language.getDisplayName();
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getStatus() {
        return status.getDisplayName();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatusEnum() {
        return status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User userId) {
        this.user = userId;
    }

    public Problem getProblem() {
        return problem;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }
}
