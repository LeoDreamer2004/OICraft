package org.dindier.oicraft.model;

import jakarta.persistence.*;

@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int problemId;
    private String code;

    private int userId;

    protected Submission() {
    }

    public enum Language {
        JAVA("java"), PYTHON("python"), C("c");

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

    public Submission(int problemId, String code, Language language) {
        this.problemId = problemId;
        this.code = code;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
