package domain.port.input;

import domain.model.EnrollmentPeriod;
import java.time.LocalDate;
import java.util.Map;

public interface AdminUseCase {
    EnrollmentPeriod setEnrollmentPeriod(LocalDate startDate, LocalDate endDate);

    EnrollmentPeriod getCurrentPeriod();

    boolean openCourseEnrollment(String courseCode);

    boolean closeCourseEnrollment(String courseCode);

    boolean openAllEnrollments();

    boolean closeAllEnrollments();

    Map<String, Object> getEnrollmentSummary();
}
