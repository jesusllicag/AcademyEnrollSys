package domain.port.output;

import domain.model.EnrollmentQueueEntry;
import java.util.List;
import java.util.Optional;

public interface EnrollmentQueueRepository {
    boolean add(EnrollmentQueueEntry entry);

    boolean remove(String studentCode);

    List<EnrollmentQueueEntry> findAll();

    Optional<EnrollmentQueueEntry> findByStudent(String studentCode);

    boolean updatePositions(List<EnrollmentQueueEntry> sortedEntries);

    boolean existsByStudent(String studentCode);

    int getMaxPosition();

    boolean clearAll();
}
