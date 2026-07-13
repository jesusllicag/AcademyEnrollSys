package infrastructure.persistence.postgresql;

import domain.model.Student;
import domain.port.output.StudentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepositoryImpl implements StudentRepository {

    private final DatabaseConnection db;

    public StudentRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Student save(Student student) {
        String sql = "INSERT INTO students (code, name, lastname, email, gpa) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, student.getCode());
            ps.setString(2, student.getName());
            ps.setString(3, student.getLastname());
            ps.setString(4, student.getEmail());
            ps.setDouble(5, student.getGpa());
            ps.executeUpdate();
            return student;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar alumno: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findByCode(String code) {
        String sql = "SELECT * FROM students WHERE code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar alumno: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar alumno por email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY code";
        List<Student> students = new ArrayList<>();
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                students.add(mapRow(rs));
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar alumnos: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String code) {
        String sql = "DELETE FROM students WHERE code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar alumno: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE students SET name=?, lastname=?, email=?, gpa=? WHERE code=?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getLastname());
            ps.setString(3, student.getEmail());
            ps.setDouble(4, student.getGpa());
            ps.setString(5, student.getCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar alumno: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNextCode() {
        String sql = "UPDATE sequence_counters SET last_value = last_value + 1 WHERE entity = 'student' RETURNING last_value";
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                long val = rs.getLong(1);
                return String.format("A%08d", val);
            }
            throw new RuntimeException("No se pudo generar codigo de alumno");
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar codigo: " + e.getMessage(), e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("lastname"),
                rs.getString("email"),
                rs.getDouble("gpa"));
    }
}
