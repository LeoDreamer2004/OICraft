package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * The problem model
 */
@Entity
@Data
public class Problem implements Comparable<Problem> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private User author;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<IOPair> ioPairs;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Submission> submissions;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Post> posts;

    public enum Difficulty {
        EASY, MEDIUM, HARD;

        public static Difficulty fromString(String string) {
            for (Difficulty difficulty : Difficulty.values()) {
                if (difficulty.toString().equalsIgnoreCase(string)) {
                    return difficulty;
                }
            }
            return null;
        }
    }

    private int timeLimit; // ms
    private int memoryLimit; // KB
    private int submit = 0;
    private int passed = 0;

    protected Problem() {
    }

    public Problem(User author, String title, String description, String inputFormat,
                   String outputFormat,
                   Difficulty difficulty, int timeLimit, int memoryLimit) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
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

    public boolean isEasy() {
        return difficulty == Difficulty.EASY;
    }

    public boolean isMedium() {
        return difficulty == Difficulty.MEDIUM;
    }

    public boolean isHard() {
        return difficulty == Difficulty.HARD;
    }

    public String getPassRateString() {
        if (submit == 0) return "NaN";
        return String.format("%.2f%%", 100.0 * passed / submit);
    }

    @Override
    public int compareTo(Problem o) {
        return Integer.compare(this.id, o.id);
    }
}
