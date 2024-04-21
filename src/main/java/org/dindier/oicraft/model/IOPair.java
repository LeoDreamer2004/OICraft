package org.dindier.oicraft.model;

/**
 * The input-output pair of a problem (Weak Entity)
 */
public class IOPair {
    private int problemId;
    private int id;
    private String input;
    private String output;
    private Type type;

    public enum Type {
        SAMPLE, TEST
    }

    private int score;

    public IOPair(int problemId, String input, String output, Type type, int score) {
        this.problemId = problemId;
        this.input = input;
        this.output = output;
        this.type = type;
        this.score = score;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
