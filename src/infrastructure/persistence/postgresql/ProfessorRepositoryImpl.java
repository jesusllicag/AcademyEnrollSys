package infrastructure.persistence.postgresql;

import domain.model.Professor;
import domain.port.output.ProfessorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfessorRepositoryImpl implements ProfessorRepository {

    private final DatabaseConnection db;

    public ProfessorRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Professor save(Professor professor) {
        String sql = "INSERT INTO professors (code, name, lastname, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, professor.getCode());
            ps.setString(2, professor.getName());
            ps.setString(3, professor.getLastname());
            ps.setString(4, professor.getEmail());
            ps.executeUpdate();
            return professor;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar profesor: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Professor> findByCode(String code) {
        String sql = "SELECT * FROM professors WHERE code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar profesor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Professor> findAll() {
        String sql = "SELECT * FROM professors ORDER BY code";
        List<Professor> professors = new ArrayList<>();
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                professors.add(mapRow(rs));
            return professors;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar profesores: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String code) {
        String sql = "DELETE FROM professors WHERE code = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar profesor: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNextCode() {
        String sql = "UPDATE sequence_counters SET last_value = last_value + 1 WHERE entity = 'professor' RETURNING last_value";
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                long val = rs.getLong(1);
                return String.format("P%08d", val);
            }
            throw new RuntimeException("No se pudo generar codigo de profesor");
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar codigo: " + e.getMessage(), e);
        }
    }

    private Professor mapRow(ResultSet rs) throws SQLException {
        return new Professor(
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("lastname"),
                rs.getString("email"));
    }
}
