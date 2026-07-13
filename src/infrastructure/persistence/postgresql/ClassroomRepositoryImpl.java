package infrastructure.persistence.postgresql;

import domain.model.Classroom;
import domain.port.output.ClassroomRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassroomRepositoryImpl implements ClassroomRepository {

    private final DatabaseConnection db;

    public ClassroomRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Classroom save(Classroom classroom) {
        String sql = "INSERT INTO classrooms (code, course_code, professor_code) VALUES (?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, classroom.getCode());
            ps.setString(2, classroom.getCourseCode());
            ps.setString(3, classroom.getProfessorCode());
            ps.executeUpdate();
            return classroom;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar aula: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Classroom> findByCode(String code) {
        String sql = """
                SELECT cl.*, p.name as prof_name, p.lastname as prof_lastname
                FROM classrooms cl
                JOIN professors p ON p.code = cl.professor_code
                WHERE cl.code = ?
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar aula: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Classroom> findByCourse(String courseCode) {
        String sql = """
                SELECT cl.*, p.name as prof_name, p.lastname as prof_lastname
                FROM classrooms cl
                JOIN professors p ON p.code = cl.professor_code
                WHERE cl.course_code = ?
                ORDER BY cl.code
                """;
        List<Classroom> classrooms = new ArrayList<>();
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                classrooms.add(mapRow(rs));
            return classrooms;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar aulas: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByCourse(String courseCode) {
        String sql = "SELECT COUNT(*) FROM classrooms WHERE course_code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar aulas: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNextCode() {
        String sql = "UPDATE sequence_counters SET last_value = last_value + 1 WHERE entity = 'classroom' RETURNING last_value";
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                long val = rs.getLong(1);
                return String.format("AU%06d", val);
            }
            throw new RuntimeException("No se pudo generar codigo de aula");
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar codigo de aula: " + e.getMessage(), e);
        }
    }

    private Classroom mapRow(ResultSet rs) throws SQLException {
        Classroom classroom = new Classroom(
                rs.getString("code"),
                rs.getString("course_code"),
                rs.getString("professor_code"));
        try {
            String profName = rs.getString("prof_lastname") + " " + rs.getString("prof_name");
            classroom.setProfessorName(profName);
        } catch (SQLException ignored) {
        }
        return classroom;
    }
}
