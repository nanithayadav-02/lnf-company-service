package com.technofacts.lnf.company.model.enums;

public enum EventType {

    Birthday("Birthday"),

    Anniversary("Anniversary"),

    Release("Release"),

    MeetingTownHall("Meeting Town Hall");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static EventType valueOfLabel(String label) {
        for (EventType at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
