package com.technofacts.lnf.company.model.enums;

public enum AddressType {

    //@formatter:off
    Primary("Primary"),
    Secondary("Secondary"),
    Other("Other");
    //@formatter:on

    private final String label;

    AddressType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AddressType valueOfLabel(String label) {
        for (AddressType at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
