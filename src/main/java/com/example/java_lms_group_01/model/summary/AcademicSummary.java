package com.example.java_lms_group_01.model.summary;


public class AcademicSummary {
    private double cgpa;
    private double sgpa;
    private boolean withheld;

    public AcademicSummary() {
    }

    public AcademicSummary(double cgpa, double sgpa) {
        this.cgpa = cgpa;
        this.sgpa = sgpa;
        this.withheld = false;
    }

    public AcademicSummary(double cgpa, double sgpa, boolean withheld) {
        this.cgpa = cgpa;
        this.sgpa = sgpa;
        this.withheld = withheld;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public double getSgpa() {
        return sgpa;
    }

    public void setSgpa(double sgpa) {
        this.sgpa = sgpa;
    }

    public boolean isWithheld() {
        return withheld;
    }

    public void setWithheld(boolean withheld) {
        this.withheld = withheld;
    }
}
