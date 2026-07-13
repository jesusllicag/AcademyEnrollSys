package application.usecase;

import domain.model.*;
import domain.port.input.AdminUseCase;
import domain.port.output.*;

import java.time.LocalDate;
import java.util.*;

public class AdminUseCaseImpl implements AdminUseCase {

    private final EnrollmentPeriodRepository periodRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AdminUseCaseImpl(EnrollmentPeriodRepository periodRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository) {
        this.periodRepository = periodRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public EnrollmentPeriod setEnrollmentPeriod(LocalDate startDate, LocalDate endDate) {
        EnrollmentPeriod period = periodRepository.findCurrent().orElse(new EnrollmentPeriod());
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        return periodRepository.save(period);
    }

    @Override
    public EnrollmentPeriod getCurrentPeriod() {
        return periodRepository.findCurrent().orElse(null);
    }

    @Override
    public boolean openCourseEnrollment(String courseCode) {
        Course course = courseRepository.findByCode(courseCode).orElse(null);
        if (course == null)
            return false;
        course.setEnrollmentOpen(true);
        return courseRepository.update(course);
    }

    @Override
    public boolean closeCourseEnrollment(String courseCode) {
        Course course = courseRepository.findByCode(courseCode).orElse(null);
        if (course == null)
            return false;
        course.setEnrollmentOpen(false);
        return courseRepository.update(course);
    }

    @Override
    public boolean openAllEnrollments() {
        List<Course> courses = courseRepository.findAll();
        boolean allOk = true;
        for (Course course : courses) {
            course.setEnrollmentOpen(true);
            if (!courseRepository.update(course))
                allOk = false;
        }
        return allOk;
    }

    @Override
    public boolean closeAllEnrollments() {
        List<Course> courses = courseRepository.findAll();
        boolean allOk = true;
        for (Course course : courses) {
            course.setEnrollmentOpen(false);
            if (!courseRepository.update(course))
                allOk = false;
        }
        return allOk;
    }

    @Override
    public Map<String, Object> getEnrollmentSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        List<Course> courses = courseRepository.findAll();
        List<Map<String, Object>> coursesSummary = new ArrayList<>();

        for (Course course : courses) {
            Map<String, Object> courseData = new LinkedHashMap<>();
            courseData.put("curso", course);
            List<Enrollment> enrollments = enrollmentRepository.findByCourse(course.getCode());
            courseData.put("matriculados", enrollments);
            courseData.put("total", enrollments.size());
            courseData.put("vacantesDisponibles", course.getMaxStudents() - enrollments.size());
            coursesSummary.add(courseData);
        }

        summary.put("cursos", coursesSummary);
        summary.put("totalCursos", courses.size());
        int totalEnrolled = courses.stream()
                .mapToInt(c -> enrollmentRepository.countByCourse(c.getCode()))
                .sum();
        summary.put("totalMatriculas", totalEnrolled);
        summary.put("periodoActual", periodRepository.findCurrent().orElse(null));
        return summary;
    }
}
