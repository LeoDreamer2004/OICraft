package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Entity
@Data
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

    @Getter
    public enum Language {
        JAVA("java"), PYTHON("python"), C("c"), CPP("cpp");

        private final String displayName;

        Language(String displayName) {
            this.displayName = displayName;
        }
    }

    @Enumerated(EnumType.STRING)
    private Language language;

    @Getter
    public enum Status {
        WAITING("waiting"), PASSED("passed"), FAILED("failed");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
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

    public int getProblemId() {
        return problem.getId();
    }

    public String getLanguageDisplayName() {
        return language.getDisplayName();
    }

    public String getStatusString() {
        return status.getDisplayName();
    }
}
