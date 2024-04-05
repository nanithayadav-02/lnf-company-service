package com.technofacts.lnf.company.model.enums;

public enum ComponentStatus {

    Active("Active"),

    InActive("InActive");

    private final String label;

    ComponentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ComponentStatus valueOfLabel(String label) {
        for (ComponentStatus at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
