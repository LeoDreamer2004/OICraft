package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * The input-output pair of a problem (Weak Entity)
 */
@Entity
@Data
public class IOPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String input;
    private String output;
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
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
}
