package com.technofacts.lnf.company.model.enums;

public enum ThemeType {

    PRIMARY("Primary"),
    SECONDARY("Secondary"),
    SUCCESS("Success"),
    ERROR("Error"),
    WARNING("Warning"),
    INFO("Info");

    private final String label;

    private ThemeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ThemeType valueOfLabel(String label) {
        for (ThemeType at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
