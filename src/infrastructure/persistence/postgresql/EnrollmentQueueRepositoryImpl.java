package infrastructure.persistence.postgresql;

import domain.model.EnrollmentQueueEntry;
import domain.port.output.EnrollmentQueueRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentQueueRepositoryImpl implements EnrollmentQueueRepository {

    private final DatabaseConnection db;

    public EnrollmentQueueRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public boolean add(EnrollmentQueueEntry entry) {
        String sql = "INSERT INTO enrollment_queue (student_code, position, registered_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, entry.getStudentCode());
            ps.setInt(2, entry.getPosition());
            ps.setTimestamp(3, Timestamp.valueOf(entry.getRegisteredAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar a la cola: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean remove(String studentCode) {
        String sql = "DELETE FROM enrollment_queue WHERE student_code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al remover de la cola: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EnrollmentQueueEntry> findAll() {
        String sql = """
                SELECT eq.*, s.name as student_name, s.lastname as student_lastname, s.gpa
                FROM enrollment_queue eq
                JOIN students s ON s.code = eq.student_code
                ORDER BY eq.position
                """;
        List<EnrollmentQueueEntry> entries = new ArrayList<>();
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                entries.add(mapRow(rs));
            return entries;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cola: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<EnrollmentQueueEntry> findByStudent(String studentCode) {
        String sql = """
                SELECT eq.*, s.name as student_name, s.lastname as student_lastname, s.gpa
                FROM enrollment_queue eq
                JOIN students s ON s.code = eq.student_code
                WHERE eq.student_code = ?
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar en cola: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePositions(List<EnrollmentQueueEntry> sortedEntries) {
        String sql = "UPDATE enrollment_queue SET position = ? WHERE student_code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            for (EnrollmentQueueEntry entry : sortedEntries) {
                ps.setInt(1, entry.getPosition());
                ps.setString(2, entry.getStudentCode());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar posiciones: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByStudent(String studentCode) {
        String sql = "SELECT 1 FROM enrollment_queue WHERE student_code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentCode);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar cola: " + e.getMessage(), e);
        }
    }

    @Override
    public int getMaxPosition() {
        String sql = "SELECT COALESCE(MAX(position), 0) FROM enrollment_queue";
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener max posicion: " + e.getMessage(), e);
        }
    }

    private EnrollmentQueueEntry mapRow(ResultSet rs) throws SQLException {
        return new EnrollmentQueueEntry(
                rs.getString("student_code"),
                rs.getString("student_name"),
                rs.getString("student_lastname"),
                rs.getDouble("gpa"),
                rs.getInt("position"),
                rs.getTimestamp("registered_at").toLocalDateTime());
    }
}
