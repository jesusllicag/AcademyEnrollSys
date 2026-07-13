package infrastructure.persistence.postgresql;

import domain.model.Enrollment;
import domain.port.output.EnrollmentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final DatabaseConnection db;

    public EnrollmentRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public boolean save(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_code, course_code) VALUES (?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, enrollment.getStudentCode());
            ps.setString(2, enrollment.getCourseCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar matricula: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Enrollment> findByStudent(String studentCode) {
        String sql = """
                SELECT e.*, c.name as course_name
                FROM enrollments e
                JOIN courses c ON c.code = e.course_code
                WHERE e.student_code = ?
                ORDER BY e.course_code
                """;
        List<Enrollment> enrollments = new ArrayList<>();
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Enrollment e = new Enrollment(
                        rs.getString("student_code"),
                        rs.getString("course_code"),
                        rs.getTimestamp("enrolled_at").toLocalDateTime());
                e.setCourseName(rs.getString("course_name"));
                enrollments.add(e);
            }
            return enrollments;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar matriculas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Enrollment> findByCourse(String courseCode) {
        String sql = """
                SELECT e.*, s.name as student_name, s.lastname as student_lastname
                FROM enrollments e
                JOIN students s ON s.code = e.student_code
                WHERE e.course_code = ?
                ORDER BY s.lastname, s.name
                """;
        List<Enrollment> enrollments = new ArrayList<>();
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Enrollment e = new Enrollment(
                        rs.getString("student_code"),
                        rs.getString("course_code"),
                        rs.getTimestamp("enrolled_at").toLocalDateTime());
                e.setStudentName(rs.getString("student_lastname") + " " + rs.getString("student_name"));
                enrollments.add(e);
            }
            return enrollments;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar matriculas por curso: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByCourse(String courseCode) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar matriculados: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByStudent(String studentCode) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar cursos del alumno: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByStudentAndCourse(String studentCode, String courseCode) {
        String sql = "SELECT 1 FROM enrollments WHERE student_code = ? AND course_code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentCode);
            ps.setString(2, courseCode);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar matricula: " + e.getMessage(), e);
        }
    }
}
