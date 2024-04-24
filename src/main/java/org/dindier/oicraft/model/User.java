package org.dindier.oicraft.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/*
 * FIXME: May use springSecurity for user authentication later
 */
@Entity
@Table(name = "User",
        uniqueConstraints = @UniqueConstraint(columnNames = "username")
)
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "username")
    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<Submission> submissions;

    protected User() {
    }

    public enum Role {
        ADMIN, USER
    }

    public enum Grade {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    public User(String name, String password, Role role, Grade grade) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.grade = grade;
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    public String getGradeString() {
        return grade.toString();
    }
}
