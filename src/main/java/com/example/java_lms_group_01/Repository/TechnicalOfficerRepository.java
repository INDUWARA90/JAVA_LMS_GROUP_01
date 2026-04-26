package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Attendance;
import com.example.java_lms_group_01.model.ExamAttendance;
import com.example.java_lms_group_01.model.Medical;
import com.example.java_lms_group_01.model.request.AttendanceRequest;
import com.example.java_lms_group_01.model.request.ExamAttendanceRequest;
import com.example.java_lms_group_01.model.request.MedicalRequest;

import java.sql.SQLException;
import java.util.List;

public class TechnicalOfficerRepository {

    private final AttendanceRepository attendanceRepository = new AttendanceRepository();
    private final MedicalRepository medicalRepository = new MedicalRepository();
    private final NoticeRepository noticeRepository = new NoticeRepository();

    public void addExamAttendance(ExamAttendanceRequest request) throws SQLException {
        attendanceRepository.addExamAttendance(request);
    }

    public void updateExamAttendance(int id, ExamAttendanceRequest request) throws SQLException {
        attendanceRepository.updateExamAttendance(id, request);
    }

    public void deleteExamAttendance(int id) throws SQLException {
        attendanceRepository.deleteExamAttendance(id);
    }

    public List<ExamAttendance> findExamAttendance(String keyword) throws SQLException {
        return attendanceRepository.findExamAttendance(keyword);
    }

    public void addAttendance(AttendanceRequest request) throws SQLException {
        attendanceRepository.addAttendance(request);
    }

    public void updateAttendance(int id, AttendanceRequest request) throws SQLException {
        attendanceRepository.updateAttendance(id, request);
    }

    public void deleteAttendance(int id) throws SQLException {
        attendanceRepository.deleteAttendance(id);
    }

    public List<Attendance> findAttendance(String keyword) throws SQLException {
        return attendanceRepository.findAttendance(keyword);
    }

    public void addMedical(MedicalRequest request) throws SQLException {
        medicalRepository.addMedical(request);
    }

    public void updateMedical(int id, MedicalRequest request) throws SQLException {
        medicalRepository.updateMedical(id, request);
    }

    public void deleteMedical(int medicalId, int attendanceId) throws SQLException {
        medicalRepository.deleteMedical(medicalId, attendanceId);
    }

    public List<Medical> findMedical(String keyword) throws SQLException {
        return medicalRepository.findMedical(keyword);
    }

    public int countAttendance() throws SQLException {
        return attendanceRepository.countAttendance();
    }

    public int countMedical() throws SQLException {
        return medicalRepository.countMedical();
    }

    public int countNotices() throws SQLException {
        return noticeRepository.countAll();
    }
}
