package org.dindier.oicraft.util.code.lang;

public enum Platform {
    WINDOWS, LINUX, MACOS, UNKNOWN;

    /**
     * Detect the platform of the system
     *
     * @return The system platform
     */
    public static Platform detect() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return Platform.WINDOWS;
        } else if (os.contains("mac")) {
            return Platform.MACOS;
        } else if (os.contains("nix") || os.contains("nux")) {
            return Platform.LINUX;
        } else {
            return Platform.UNKNOWN;
        }
    }
}
