package domain.port.output;

import domain.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    boolean save(Enrollment enrollment);

    List<Enrollment> findByStudent(String studentCode);

    List<Enrollment> findByCourse(String courseCode);

    int countByCourse(String courseCode);

    int countByStudent(String studentCode);

    boolean existsByStudentAndCourse(String studentCode, String courseCode);
}
