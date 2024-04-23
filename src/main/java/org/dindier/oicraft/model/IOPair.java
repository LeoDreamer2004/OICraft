package org.dindier.oicraft.model;

import jakarta.persistence.*;

/**
 * The input-output pair of a problem (Weak Entity)
 */
@Entity
public class IOPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String input;
    private String output;
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    public enum Type {
        SAMPLE, TEST
    }

    private int score;

    protected IOPair() {
    }

    public IOPair(String input, String output, Type type, int score) {
        this.input = input;
        this.output = output;
        this.type = type;
        this.score = score;
    }

    public int getProblemId() {
        return problem.getId();
    }

    public int getId() {
        return id;
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
