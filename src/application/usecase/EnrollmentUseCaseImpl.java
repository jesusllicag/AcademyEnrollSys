package application.usecase;

import domain.model.Enrollment;
import domain.model.EnrollmentQueueEntry;
import domain.model.Student;
import domain.port.input.EnrollmentUseCase;
import domain.port.output.*;
import shared.algorithm.SortingAlgorithms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentUseCaseImpl implements EnrollmentUseCase {

    private static final int MAX_COURSES_PER_STUDENT = 5;

    private final EnrollmentQueueRepository queueRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentPeriodRepository periodRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentUseCaseImpl(EnrollmentQueueRepository queueRepository,
            EnrollmentRepository enrollmentRepository,
            EnrollmentPeriodRepository periodRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository) {
        this.queueRepository = queueRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.periodRepository = periodRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public boolean joinQueue(String studentCode) {
        if (queueRepository.existsByStudent(studentCode))
            return false;
        Student student = studentRepository.findByCode(studentCode).orElse(null);
        if (student == null)
            return false;

        int nextPos = queueRepository.getMaxPosition() + 1;
        EnrollmentQueueEntry entry = new EnrollmentQueueEntry(
                studentCode, student.getName(), student.getLastname(),
                student.getGpa(), nextPos, LocalDateTime.now());
        return queueRepository.add(entry);
    }

    @Override
    public boolean leaveQueue(String studentCode) {
        return queueRepository.remove(studentCode);
    }

    @Override
    public List<EnrollmentQueueEntry> getQueue() {
        return queueRepository.findAll();
    }

    @Override
    public EnrollmentQueueEntry getQueueEntry(String studentCode) {
        return queueRepository.findByStudent(studentCode).orElse(null);
    }

    @Override
    public boolean openEnrollment() {
        // Ordenar la cola usando Insertion Sort (por promedio desc, luego apellido,
        // nombre)
        List<EnrollmentQueueEntry> queue = new ArrayList<>(queueRepository.findAll());
        if (queue.isEmpty())
            return false;

        SortingAlgorithms.insertionSort(queue, EnrollmentQueueEntry::compareTo);

        // Reasignar posiciones
        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).setPosition(i + 1);
        }
        queueRepository.updatePositions(queue);

        // Actualizar el periodo activo
        periodRepository.findCurrent().ifPresent(period -> {
            period.setActive(true);
            period.setCurrentServingPosition(1);
            periodRepository.save(period);
        });

        return true;
    }

    @Override
    public boolean closeEnrollment() {
        periodRepository.findCurrent().ifPresent(period -> {
            period.setActive(false);
            periodRepository.save(period);
        });
        return true;
    }

    @Override
    public boolean enrollStudent(String studentCode, String courseCode) {
        // Validar que la matricula este abierta
        if (!isEnrollmentOpen())
            return false;

        // Validar turno del alumno
        if (!isStudentTurn(studentCode))
            return false;

        // Validar limite de cursos
        int currentCount = enrollmentRepository.countByStudent(studentCode);
        if (currentCount >= MAX_COURSES_PER_STUDENT)
            return false;

        // Validar que no este ya matriculado
        if (enrollmentRepository.existsByStudentAndCourse(studentCode, courseCode))
            return false;

        // Validar que el curso tenga vacantes
        var course = courseRepository.findByCode(courseCode).orElse(null);
        if (course == null || !course.isEnrollmentOpen())
            return false;
        int enrolled = enrollmentRepository.countByCourse(courseCode);
        if (enrolled >= course.getMaxStudents())
            return false;

        Enrollment enrollment = new Enrollment(studentCode, courseCode, LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> getStudentEnrollments(String studentCode) {
        return enrollmentRepository.findByStudent(studentCode);
    }

    @Override
    public boolean isEnrollmentOpen() {
        return periodRepository.findCurrent()
                .map(p -> p.isActive())
                .orElse(false);
    }

    @Override
    public boolean isStudentTurn(String studentCode) {
        var entry = queueRepository.findByStudent(studentCode).orElse(null);
        if (entry == null)
            return false;
        int currentPos = getCurrentServingPosition();
        return entry.getPosition() == currentPos;
    }

    @Override
    public int getCurrentServingPosition() {
        return periodRepository.findCurrent()
                .map(p -> p.getCurrentServingPosition())
                .orElse(1);
    }

    @Override
    public void advanceQueue() {
        int next = getCurrentServingPosition() + 1;
        periodRepository.updateServingPosition(next);
    }
}
