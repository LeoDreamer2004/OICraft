package org.dindier.oicraft.util.code.lang;

import lombok.Getter;

/**
 * The language of the code
 */
@Getter
public enum Language {
    JAVA("Java"), PYTHON("Python"), C("C"), CPP("C++");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public static Language fromString(String language) {
        if (language.equalsIgnoreCase("Cpp"))
            return CPP;
        for (Language l : Language.values()) {
            if (l.getDisplayName().equalsIgnoreCase(language)) {
                return l;
            }
        }
        return null;
    }
}