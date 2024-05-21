package org.dindier.oicraft.util.code;

import lombok.Getter;

@Getter
public enum Language {
    JAVA("Java"), PYTHON("Python"), C("C"), CPP("Cpp");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public static Language fromString(String language) {
        if (language.equalsIgnoreCase("C++"))
            return CPP;
        for (Language l : Language.values()) {
            if (l.getDisplayName().equalsIgnoreCase(language)) {
                return l;
            }
        }
        return null;
    }
}