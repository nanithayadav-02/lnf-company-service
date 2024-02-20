package com.technofacts.lnf.company.model.enums;

public enum TaskStatus {

    TODO("TODO"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String label;

    private TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TaskStatus valueOfLabel(String label) {
        for (TaskStatus at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
