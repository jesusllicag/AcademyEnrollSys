package domain.port.input;

import domain.model.Enrollment;
import domain.model.EnrollmentQueueEntry;
import java.util.List;

public interface EnrollmentUseCase {
    boolean joinQueue(String studentCode);

    boolean leaveQueue(String studentCode);

    boolean clearQueue();

    List<EnrollmentQueueEntry> getQueue();

    EnrollmentQueueEntry getQueueEntry(String studentCode);

    boolean openEnrollment();

    boolean closeEnrollment();

    boolean enrollStudent(String studentCode, String courseCode);

    List<Enrollment> getStudentEnrollments(String studentCode);

    boolean isEnrollmentOpen();

    boolean isStudentTurn(String studentCode);

    int getCurrentServingPosition();

    void advanceQueue();
}
