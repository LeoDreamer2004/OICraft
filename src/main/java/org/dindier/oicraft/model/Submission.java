package org.dindier.oicraft.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.dindier.oicraft.util.code.lang.Language;

import java.util.List;

@Entity
@Data
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "submission", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Checkpoint> checkpoints;

    private boolean aiAdviceRequested;

    private String adviceAI;

    protected Submission() {
    }

    @Enumerated(EnumType.STRING)
    private Language language;

    @Getter
    public enum Result {
        WAITING("waiting"), PASSED("passed"), FAILED("failed");

        private final String displayName;

        Result(String displayName) {
            this.displayName = displayName;
        }

    }

    @Enumerated(EnumType.STRING)
    private Result result;
    private int score;

    public Submission(User user, Problem problem, String code, Language language) {
        this.user = user;
        this.problem = problem;
        this.code = code;
        this.language = language;
        this.result = Result.WAITING;
        this.aiAdviceRequested = false;
    }

    public int getProblemId() {
        return problem.getId();
    }

    public String getLanguageDisplayName() {
        return language.getDisplayName();
    }
}
