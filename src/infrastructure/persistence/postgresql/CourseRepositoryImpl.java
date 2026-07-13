package infrastructure.persistence.postgresql;

import domain.model.Course;
import domain.port.output.CourseRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRepositoryImpl implements CourseRepository {

    private final DatabaseConnection db;

    public CourseRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Course save(Course course) {
        String sql = "INSERT INTO courses (code, name, max_students, enrollment_open) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getName());
            ps.setInt(3, course.getMaxStudents());
            ps.setBoolean(4, course.isEnrollmentOpen());
            ps.executeUpdate();
            return course;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar curso: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Course> findByCode(String code) {
        String sql = "SELECT * FROM courses WHERE code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar curso: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Course> findAll() {
        String sql = "SELECT * FROM courses ORDER BY code";
        List<Course> courses = new ArrayList<>();
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                courses.add(mapRow(rs));
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cursos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Course> findAvailable() {
        String sql = """
                SELECT c.* FROM courses c
                WHERE c.enrollment_open = TRUE
                AND (SELECT COUNT(*) FROM enrollments e WHERE e.course_code = c.code) < c.max_students
                ORDER BY c.code
                """;
        List<Course> courses = new ArrayList<>();
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                courses.add(mapRow(rs));
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cursos disponibles: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String code) {
        String sql = "DELETE FROM courses WHERE code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar curso: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Course course) {
        String sql = "UPDATE courses SET name=?, max_students=?, enrollment_open=? WHERE code=?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, course.getName());
            ps.setInt(2, course.getMaxStudents());
            ps.setBoolean(3, course.isEnrollmentOpen());
            ps.setString(4, course.getCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar curso: " + e.getMessage(), e);
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        return new Course(
                rs.getString("code"),
                rs.getString("name"),
                rs.getInt("max_students"),
                rs.getBoolean("enrollment_open"));
    }
}
