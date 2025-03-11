package com.lnf.company.model.enums;

public enum CompanyPlanStatus {

    ACTIVE("ACTIVE"),

    CANCELLED("CANCELLED");

    private final String label;

    CompanyPlanStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CompanyPlanStatus valueOfLabel(String label) {
        for (CompanyPlanStatus at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
