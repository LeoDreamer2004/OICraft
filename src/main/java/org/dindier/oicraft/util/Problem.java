package org.dindier.oicraft.util;

public class Problem {
    private int id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private Difficulty difficulty;
    // time limit in milliseconds
    private int timeLimit;
    // memory limit in kb
    private int memoryLimit;

    public enum Difficulty {
        EASY("Easy"),
        MEDIUM("Medium"),
        HARD("Hard");

        private final String displayName;

        Difficulty(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Difficulty fromString(String text) {
            for (Difficulty b:Difficulty.values()){
                if (b.displayName.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public Problem(int id, String title, String description, String inputFormat, String outputFormat,
                   Difficulty difficulty, int timeLimit, int memoryLimit) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }
}
