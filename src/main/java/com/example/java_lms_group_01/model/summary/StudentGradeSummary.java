package com.example.java_lms_group_01.model.summary;

import com.example.java_lms_group_01.model.Grade;

import java.util.List;


public class StudentGradeSummary {
    private List<Grade> grades;
    private double cgpa;
    private double sgpa;
    private boolean withheld;

    public StudentGradeSummary() {
    }

    public StudentGradeSummary(List<Grade> grades, double cgpa, double sgpa) {
        this.grades = grades;
        this.cgpa = cgpa;
        this.sgpa = sgpa;
        this.withheld = false;
    }

    public StudentGradeSummary(List<Grade> grades, double cgpa, double sgpa, boolean withheld) {
        this.grades = grades;
        this.cgpa = cgpa;
        this.sgpa = sgpa;
        this.withheld = withheld;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
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
