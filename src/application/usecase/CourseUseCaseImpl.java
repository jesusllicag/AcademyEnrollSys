package application.usecase;

import domain.model.Classroom;
import domain.model.Course;
import domain.port.input.CourseUseCase;
import domain.port.output.ClassroomRepository;
import domain.port.output.CourseRepository;
import domain.port.output.EnrollmentRepository;
import domain.port.output.ProfessorRepository;

import java.util.List;

public class CourseUseCaseImpl implements CourseUseCase {

    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProfessorRepository professorRepository;

    public CourseUseCaseImpl(CourseRepository courseRepository,
            ClassroomRepository classroomRepository,
            EnrollmentRepository enrollmentRepository,
            ProfessorRepository professorRepository) {
        this.courseRepository = courseRepository;
        this.classroomRepository = classroomRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    public Course createCourse(String name, int maxStudents) {
        // Generar codigo basado en nombre
        String baseCode = name.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (baseCode.length() > 6)
            baseCode = baseCode.substring(0, 6);
        String code = baseCode + String.format("%03d", System.currentTimeMillis() % 1000);
        Course course = new Course(code, name, maxStudents, false);
        return courseRepository.save(course);
    }

    @Override
    public Course findByCode(String code) {
        return courseRepository.findByCode(code).orElse(null);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> findAvailableForEnrollment() {
        return courseRepository.findAvailable();
    }

    @Override
    public boolean deleteCourse(String code) {
        return courseRepository.delete(code);
    }

    @Override
    public Classroom addClassroom(String courseCode, String professorCode) {
        if (courseRepository.findByCode(courseCode).isEmpty()) {
            throw new IllegalArgumentException("El curso no existe: " + courseCode);
        }
        if (professorRepository.findByCode(professorCode).isEmpty()) {
            throw new IllegalArgumentException("El profesor no existe: " + professorCode);
        }
        String classroomCode = classroomRepository.generateNextCode();
        Classroom classroom = new Classroom(classroomCode, courseCode, professorCode);
        return classroomRepository.save(classroom);
    }

    @Override
    public List<Classroom> getClassrooms(String courseCode) {
        return classroomRepository.findByCourse(courseCode);
    }

    @Override
    public int getEnrolledCount(String courseCode) {
        return enrollmentRepository.countByCourse(courseCode);
    }
}
