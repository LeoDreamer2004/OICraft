package org.dindier.oicraft.model;


/*
* FIXME: May use springSecurity for user authentication later
*/
public class User {
    private int id;
    private String name;
    private String password;
    private Role role;
    private Grade grade;

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
        EXPORT("Export");

        private final String displayName;

        Grade(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Grade fromString(String text) {
            for (Grade g: Grade.values()){
                if (g.displayName.equalsIgnoreCase(text)){
                    return g;
                }
            }
            return null;
        }
    }

    public User(int id, String name, String password, Role role, Grade grade) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.grade = grade;
    }
}
