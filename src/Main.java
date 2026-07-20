import application.usecase.*;
import domain.port.input.*;
import domain.port.output.*;
import infrastructure.persistence.postgresql.*;
import infrastructure.ui.console.MainConsole;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        System.out.println("  Conectando a la base de datos...");

        DatabaseConnection db;
        try {
            db = DatabaseConnection.getInstance();
            System.out.println("  Conexion establecida con PostgreSQL.");
        } catch (SQLException e) {
            System.err.println("  [ERROR] No se pudo conectar a PostgreSQL: " + e.getMessage());
            System.exit(1);
            return;
        }

        // -- Capa: infrastructure --
        StudentRepository studentRepo = new StudentRepositoryImpl(db);
        ProfessorRepository professorRepo = new ProfessorRepositoryImpl(db);
        CourseRepository courseRepo = new CourseRepositoryImpl(db);
        ClassroomRepository classroomRepo = new ClassroomRepositoryImpl(db);
        EnrollmentRepository enrollmentRepo = new EnrollmentRepositoryImpl(db);
        EnrollmentQueueRepository queueRepo = new EnrollmentQueueRepositoryImpl(db);
        EnrollmentPeriodRepository periodRepo = new EnrollmentPeriodRepositoryImpl(db);

        // -- Capa: application --
        StudentUseCase studentUseCase = new StudentUseCaseImpl(studentRepo);
        ProfessorUseCase professorUseCase = new ProfessorUseCaseImpl(professorRepo);
        CourseUseCase courseUseCase = new CourseUseCaseImpl(courseRepo, classroomRepo, enrollmentRepo, professorRepo);
        EnrollmentUseCase enrollmentUseCase = new EnrollmentUseCaseImpl(queueRepo, enrollmentRepo, periodRepo, courseRepo, studentRepo);
        AdminUseCase adminUseCase = new AdminUseCaseImpl(periodRepo, courseRepo, enrollmentRepo);

        // -- UI: console --
        MainConsole console = new MainConsole(studentUseCase, professorUseCase, courseUseCase, enrollmentUseCase, adminUseCase);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                db.close();
                System.out.println("\n  Conexion cerrada.");
            } catch (SQLException ignored) {}
        }));

        console.run();
    }
}
