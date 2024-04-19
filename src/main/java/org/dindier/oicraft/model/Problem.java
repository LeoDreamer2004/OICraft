package org.dindier.oicraft.model;

import java.util.ArrayList;
import java.util.List;

public class Problem {
    private int id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String difficulty;
    public static final String EASY = "easy";
    public static final String MEDIUM = "medium";
    public static final String HARD = "hard";
    private int timeLimit; // time limit in milliseconds
    private int memoryLimit; // memory limit in kb
    private int submit = 0;
    private int passed = 0;

    public Problem(String title, String description, String inputFormat, String outputFormat,
                   String difficulty, int timeLimit, int memoryLimit) {
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
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
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
}
