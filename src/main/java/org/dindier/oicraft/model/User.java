package org.dindier.oicraft.model;


import jakarta.persistence.*;

/*
 * FIXME: May use springSecurity for user authentication later
 */
@Entity
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

    protected User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public enum Role {
        ADMIN, USER
    }

    public enum Grade {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        ADVANCED("Advanced"),
        EXPERT("Expert");

        private final String displayName;

        Grade(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Grade fromString(String text) {
            for (Grade g : Grade.values()) {
                if (g.displayName.equalsIgnoreCase(text)) {
                    return g;
                }
            }
            return null;
        }
    }

    public User(String name, String password, Role role, Grade grade) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.grade = grade;
    }
}
