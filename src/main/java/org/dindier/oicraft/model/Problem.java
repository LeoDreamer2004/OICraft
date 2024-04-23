package org.dindier.oicraft.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;

    private int authorId;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<IOPair> ioPairs;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    public enum Difficulty {
        EASY, MEDIUM, HARD;
    }

    private int timeLimit; // ms
    private int memoryLimit; // KB
    private int submit = 0;
    private int passed = 0;

    protected Problem() {
    }

    public Problem(int authorId, String title, String description, String inputFormat, String outputFormat,
                   Difficulty difficulty, int timeLimit, int memoryLimit) {
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public int getSubmit() {
        return submit;
    }

    public void setSubmit(int submit) {
        this.submit = submit;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getIdString() {
        return String.format("P%04d", id);
    }

    public String getTimeLimitString() {
        if (timeLimit < 1000) {
            return timeLimit + "ms";
        } else {
            return String.format("%.2fs", timeLimit / 1000.0);
        }
    }

    public String getMemoryLimitString() {
        if (memoryLimit < 1024) {
            return memoryLimit + "KB";
        } else {
            return String.format("%.2fMB", memoryLimit / 1024.0);
        }
    }

    public List<IOPair> getIoPairs() {
        return ioPairs;
    }

    public boolean isEasy() {
        return difficulty == Difficulty.EASY;
    }

    public boolean isMedium() {
        return difficulty == Difficulty.MEDIUM;
    }

    public boolean isHard() {
        return difficulty == Difficulty.HARD;
    }
}
