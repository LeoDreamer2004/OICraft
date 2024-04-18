package org.dindier.oicraft.util;

public class User {
    private int id;
    private String name;
    private String password;
    private Role role;
    private Grade grade;

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
