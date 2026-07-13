package domain.port.output;

import domain.model.EnrollmentPeriod;
import java.util.Optional;

public interface EnrollmentPeriodRepository {
    EnrollmentPeriod save(EnrollmentPeriod period);

    Optional<EnrollmentPeriod> findCurrent();

    boolean updateServingPosition(int position);
}
