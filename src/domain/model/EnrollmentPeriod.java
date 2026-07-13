package domain.model;

import java.time.LocalDate;

public class EnrollmentPeriod {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private int currentServingPosition;

    public EnrollmentPeriod() {
    }

    public EnrollmentPeriod(int id, LocalDate startDate, LocalDate endDate, boolean active,
            int currentServingPosition) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.currentServingPosition = currentServingPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCurrentServingPosition() {
        return currentServingPosition;
    }

    public void setCurrentServingPosition(int pos) {
        this.currentServingPosition = pos;
    }

    @Override
    public String toString() {
        return String.format("Periodo de Matricula | Inicio: %s | Fin: %s | Estado: %s",
                startDate, endDate, active ? "ACTIVO" : "INACTIVO");
    }
}
