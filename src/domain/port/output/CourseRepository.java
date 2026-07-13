package domain.port.output;

import domain.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);

    Optional<Course> findByCode(String code);

    List<Course> findAll();

    List<Course> findAvailable();

    boolean delete(String code);

    boolean update(Course course);
}
