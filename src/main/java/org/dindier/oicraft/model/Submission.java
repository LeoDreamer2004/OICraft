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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "submission", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Checkpoint> checkpoints;

    private String adviceAI;

    protected Submission() {
    }

    @Getter
    public enum Language {
        JAVA("Java"), PYTHON("Python"), C("C"), CPP("C++");

        private final String displayName;

        Language(String displayName) {
            this.displayName = displayName;
        }

        public static Language fromString(String language) {
            for (Language l : Language.values()) {
                if (l.getDisplayName().equalsIgnoreCase(language)) {
                    return l;
                }
            }
            return null;
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

    public Submission(User user, Problem problem, String code, Language language) {
        this.user = user;
        this.problem = problem;
        this.code = code;
        this.language = language;
        this.status = Status.WAITING;
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
