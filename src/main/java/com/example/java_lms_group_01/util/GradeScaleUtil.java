package com.example.java_lms_group_01.util;

public final class GradeScaleUtil {

    private static final double COMPONENT_MINIMUM_RATIO = 0.40;

    private GradeScaleUtil() {
    }

    public static String toLetterGrade(double marks) {
        if (marks >= 80) return "A+";
        else if (marks >= 75) return "A";
        else if (marks >= 70) return "A-";
        else if (marks >= 65) return "B+";
        else if (marks >= 60) return "B";
        else if (marks >= 55) return "B-";
        else if (marks >= 50) return "C+";
        else if (marks >= 45) return "C";
        else if (marks >= 40) return "C-";
        else if (marks >= 35) return "D";
        else return "E";
    }

    public static double toGradePoint(double marks) {
        if (marks >= 75) return 4.0;
        else if (marks >= 70) return 3.7;
        else if (marks >= 65) return 3.3;
        else if (marks >= 60) return 3.0;
        else if (marks >= 55) return 2.7;
        else if (marks >= 50) return 2.3;
        else if (marks >= 45) return 2.0;
        else if (marks >= 40) return 1.7;
        else if (marks >= 35) return 1.3;
        else return 0.0;
    }

    public static double minimumRequiredMark(double maximum) {
        if (maximum <= 0) {
            return 0.0;
        }
        return maximum * COMPONENT_MINIMUM_RATIO;
    }

    public static boolean meetsComponentRequirement(double marks, double maximum) {
        if (maximum <= 0) {
            return true;
        }
        return marks >= maximum * COMPONENT_MINIMUM_RATIO;
    }

    public static boolean meetsCaRequirement(MarkBreakdown breakdown) {
        return meetsComponentRequirement(
                breakdown.getCaMarks(),
                breakdown.getCaMaximum()
        );
    }

    public static boolean meetsEndRequirement(MarkBreakdown breakdown) {
        return meetsComponentRequirement(
                breakdown.getEndMarks(),
                breakdown.getEndMaximum()
        );
    }

    public static GradeResult evaluatePublishedGrade(
            MarkBreakdown breakdown,
            boolean attendanceEligible,
            String examAttendanceStatus,
            boolean approvedExamMedical) {

        if (!attendanceEligible) {
            return new GradeResult("E", 0.0);
        }

        boolean hasEndComponent = breakdown.getEndMaximum() > 0;
        boolean caPassed = meetsCaRequirement(breakdown);
        boolean endPassed = meetsEndRequirement(breakdown);

        String safeExamStatus = examAttendanceStatus == null ? "" : examAttendanceStatus.trim().toLowerCase();
        boolean examPresent = "present".equals(safeExamStatus);
        boolean examAbsent = "absent".equals(safeExamStatus);

        if (hasEndComponent && !examPresent) {
            if (examAbsent && approvedExamMedical) {
                return new GradeResult("MC", null);
            }
            return new GradeResult("E", 0.0);
        }

        if (!caPassed && !endPassed) {
            return new GradeResult("E", 0.0);
        }

        if (!caPassed) {
            return new GradeResult("EC", null);
        }

        if (!endPassed) {
            return new GradeResult("EE", null);
        }

        double totalMarks = breakdown.getTotalMarks();

        return new GradeResult(
                toLetterGrade(totalMarks),
                toGradePoint(totalMarks)
        );
    }

    public static boolean isEnglishCourse(String courseCode) {
        return courseCode != null &&
                courseCode.trim().toUpperCase().startsWith("ENG");
    }
}