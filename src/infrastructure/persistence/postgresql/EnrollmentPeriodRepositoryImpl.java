package infrastructure.persistence.postgresql;

import domain.model.EnrollmentPeriod;
import domain.port.output.EnrollmentPeriodRepository;

import java.sql.*;
import java.util.Optional;

public class EnrollmentPeriodRepositoryImpl implements EnrollmentPeriodRepository {

    private final DatabaseConnection db;

    public EnrollmentPeriodRepositoryImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public EnrollmentPeriod save(EnrollmentPeriod period) {
        if (period.getId() == 0) {
            String sql = "INSERT INTO enrollment_period (start_date, end_date, is_active, current_serving_position) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
                ps.setDate(1, period.getStartDate() != null ? Date.valueOf(period.getStartDate()) : null);
                ps.setDate(2, period.getEndDate() != null ? Date.valueOf(period.getEndDate()) : null);
                ps.setBoolean(3, period.isActive());
                ps.setInt(4, period.getCurrentServingPosition());
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    period.setId(rs.getInt(1));
                return period;
            } catch (SQLException e) {
                throw new RuntimeException("Error al guardar periodo: " + e.getMessage(), e);
            }
        } else {
            String sql = "UPDATE enrollment_period SET start_date=?, end_date=?, is_active=?, current_serving_position=? WHERE id=?";
            try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
                ps.setDate(1, period.getStartDate() != null ? Date.valueOf(period.getStartDate()) : null);
                ps.setDate(2, period.getEndDate() != null ? Date.valueOf(period.getEndDate()) : null);
                ps.setBoolean(3, period.isActive());
                ps.setInt(4, period.getCurrentServingPosition());
                ps.setInt(5, period.getId());
                ps.executeUpdate();
                return period;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar periodo: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public Optional<EnrollmentPeriod> findCurrent() {
        String sql = "SELECT * FROM enrollment_period ORDER BY id DESC LIMIT 1";
        try (Statement st = db.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                EnrollmentPeriod period = new EnrollmentPeriod();
                period.setId(rs.getInt("id"));
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                if (startDate != null)
                    period.setStartDate(startDate.toLocalDate());
                if (endDate != null)
                    period.setEndDate(endDate.toLocalDate());
                period.setActive(rs.getBoolean("is_active"));
                period.setCurrentServingPosition(rs.getInt("current_serving_position"));
                return Optional.of(period);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar periodo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateServingPosition(int position) {
        String sql = "UPDATE enrollment_period SET current_serving_position = ? WHERE id = (SELECT MAX(id) FROM enrollment_period)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, position);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar posicion de atencion: " + e.getMessage(), e);
        }
    }
}
