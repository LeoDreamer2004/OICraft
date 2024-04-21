package org.dindier.oicraft.model;

import jakarta.persistence.*;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    public enum Difficulty {
        EASY("easy"), MEDIUM("medium"), HARD("hard");

        private final String displayName;

        Difficulty(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private int timeLimit; // ms
    private int memoryLimit; // KB
    private int submit = 0;
    private int passed = 0;

    public Problem() {
    }

    public Problem(String title, String description, String inputFormat, String outputFormat,
                   Difficulty difficulty, int timeLimit, int memoryLimit) {
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

    public String getDifficulty() {
        return difficulty.displayName;
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
}
