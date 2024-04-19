package org.dindier.oicraft.model;

/**
 * The input-output pair of a problem (Weak Entity)
 */
public class IOPair {
    private int problemId;
    private int id;
    private String input;
    private String output;
    private int type;
    public static final int SAMPLE = 0;
    public static final int TEST = 1;
    private int score;

    public IOPair(int problemId, String input, String output, int type, int score) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
