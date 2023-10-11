package com.technofacts.lnf.company.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.technofacts.lnf.company.model.SalaryComponent;
import com.technofacts.lnf.company.model.SalaryDeduction;

import java.util.Map;

public class SalaryComponentConfiguration {
    private Map<String, SalaryComponent> salaryComponents;
    private Map<String, SalaryDeduction> salaryDeductions;

    @JsonProperty("salary_components")
    public Map<String, SalaryComponent> getSalaryComponents() {
        return salaryComponents;
    }

    public void setSalaryComponents(Map<String, SalaryComponent> salaryComponents) {
        this.salaryComponents = salaryComponents;
    }

    @JsonProperty("salary_deductions")
    public Map<String, SalaryDeduction> getSalaryDeductions() {
        return salaryDeductions;
    }

    public void setSalaryDeductions(Map<String, SalaryDeduction> salaryDeductions) {
        this.salaryDeductions = salaryDeductions;
    }
}