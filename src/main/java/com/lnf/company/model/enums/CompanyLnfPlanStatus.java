package com.lnf.company.model.enums;

public enum CompanyLnfPlanStatus {

    ACTIVE("ACTIVE"),

    CANCELLED("CANCELLED");

    private final String label;

    CompanyLnfPlanStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CompanyLnfPlanStatus valueOfLabel(String label) {
        for (CompanyLnfPlanStatus at : values()) {
            if (at.label.equals(label)) {
                return at;
            }
        }
        return null;
    }
}
