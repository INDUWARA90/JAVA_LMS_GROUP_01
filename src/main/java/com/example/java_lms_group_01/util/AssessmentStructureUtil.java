package com.example.java_lms_group_01.util;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public final class AssessmentStructureUtil {

    private AssessmentStructureUtil() {
    }

    public static MarkBreakdown calculateMarkBreakdown(
            Connection connection,
            String courseCode,
            Double quiz1Marks, Double quiz2Marks, Double quiz3Marks,
            Double assignmentMarks, Double projectMarks, Double midTermMarks,
            Double finalTheoryMarks, Double finalPracticalMarks) throws SQLException {

        Map<String, Double> weightMap = loadWeights(connection, courseCode);

        // -------- CA PART --------
        double quizContribution = calculateTopTwoQuizContribution(
                quiz1Marks, quiz2Marks, quiz3Marks,
                getWeight(weightMap, "quiz_1"),
                getWeight(weightMap, "quiz_2"),
                getWeight(weightMap, "quiz_3")
        );

        double assignmentContribution = calculateWeightedMark(
                assignmentMarks, getWeight(weightMap, "assessment")
        );

        double projectContribution = calculateWeightedMark(
                projectMarks, getWeight(weightMap, "project")
        );

        double midTermContribution = calculateWeightedMark(
                midTermMarks, getWeight(weightMap, "mid_term")
        );

        double totalCaMarks = quizContribution
                + assignmentContribution
                + projectContribution
                + midTermContribution;

        // -------- END PART --------
        double totalEndMarks;

        double endExamWeight = getWeight(weightMap, "end_exam");

        if (endExamWeight > 0) {
            Double averageEndMarks = calculateAverage(finalTheoryMarks, finalPracticalMarks);
            totalEndMarks = calculateWeightedMark(averageEndMarks, endExamWeight);
        } else {
            double theoryContribution = calculateWeightedMark(
                    finalTheoryMarks, getWeight(weightMap, "final_theory")
            );

            double practicalContribution = calculateWeightedMark(
                    finalPracticalMarks, getWeight(weightMap, "final_practical")
            );

            totalEndMarks = theoryContribution + practicalContribution;
        }

        return new MarkBreakdown(
                totalCaMarks,
                totalEndMarks,
                totalCaMarks + totalEndMarks,
                calculateCaMaximum(weightMap),
                calculateEndMaximum(weightMap)
        );
    }

    // -------- LOAD WEIGHTS FROM --------
    private static Map<String, Double> loadWeights(Connection connection, String courseCode) throws SQLException {

        Map<String, Double> weightMap = new HashMap<>();

        String sqlQuery = "SELECT component, weight FROM assessment_structure WHERE courseCode = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, courseCode);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String componentName = resultSet.getString("component")
                    .toLowerCase()
                    .replace(" ", "_");

            double weightValue = resultSet.getDouble("weight");

            weightMap.put(componentName, weightValue);
        }

        return weightMap;
    }

    // -------- HELPER METHODS --------
    private static double calculateWeightedMark(Double marks, double weight) {
        if (marks == null) return 0.0;
        return (marks * weight) / 100.0;
    }

    private static double getWeight(Map<String, Double> weightMap, String componentName) {
        return weightMap.getOrDefault(componentName, 0.0);
    }

    private static Double calculateAverage(Double value1, Double value2) {
        if (value1 == null && value2 == null) return null;
        if (value1 == null) return value2;
        if (value2 == null) return value1;
        return (value1 + value2) / 2.0;
    }

    // -------- TOP TWO QUIZ LOGIC --------
    private static double calculateTopTwoQuizContribution(
            Double quiz1Marks, Double quiz2Marks, Double quiz3Marks,
            double quiz1Weight, double quiz2Weight, double quiz3Weight) {

        double quiz1Value = (quiz1Marks == null) ? 0 : (quiz1Marks * quiz1Weight / 100);
        double quiz2Value = (quiz2Marks == null) ? 0 : (quiz2Marks * quiz2Weight / 100);
        double quiz3Value = (quiz3Marks == null) ? 0 : (quiz3Marks * quiz3Weight / 100);

        double lowestQuizValue = Math.min(quiz1Value, Math.min(quiz2Value, quiz3Value));

        return quiz1Value + quiz2Value + quiz3Value - lowestQuizValue;
    }

    // -------- MAXIMUM CALCULATIONS --------
    private static double calculateCaMaximum(Map<String, Double> weightMap) {

        double quiz1Weight = getWeight(weightMap, "quiz_1");
        double quiz2Weight = getWeight(weightMap, "quiz_2");
        double quiz3Weight = getWeight(weightMap, "quiz_3");

        double lowestQuizWeight = Math.min(quiz1Weight, Math.min(quiz2Weight, quiz3Weight));

        double totalQuizWeight = quiz1Weight + quiz2Weight + quiz3Weight - lowestQuizWeight;

        return totalQuizWeight
                + getWeight(weightMap, "assessment")
                + getWeight(weightMap, "project")
                + getWeight(weightMap, "mid_term");
    }

    private static double calculateEndMaximum(Map<String, Double> weightMap) {

        double endExamWeight = getWeight(weightMap, "end_exam");

        if (endExamWeight > 0) {
            return endExamWeight;
        }

        return getWeight(weightMap, "final_theory")
                + getWeight(weightMap, "final_practical");
    }
}