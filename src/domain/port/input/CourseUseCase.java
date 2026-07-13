package domain.port.input;

import domain.model.Classroom;
import domain.model.Course;
import java.util.List;

public interface CourseUseCase {
    Course createCourse(String name, int maxStudents);

    Course findByCode(String code);

    List<Course> findAll();

    List<Course> findAvailableForEnrollment();

    boolean deleteCourse(String code);

    Classroom addClassroom(String courseCode, String professorCode);

    List<Classroom> getClassrooms(String courseCode);

    int getEnrolledCount(String courseCode);
}
