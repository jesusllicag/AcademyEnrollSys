package domain.port.output;

import domain.model.Classroom;
import java.util.List;
import java.util.Optional;

public interface ClassroomRepository {
    Classroom save(Classroom classroom);

    Optional<Classroom> findByCode(String code);

    List<Classroom> findByCourse(String courseCode);

    int countByCourse(String courseCode);

    String generateNextCode();
}
