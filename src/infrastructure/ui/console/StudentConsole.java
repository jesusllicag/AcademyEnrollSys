package infrastructure.ui.console;

import domain.model.*;
import domain.port.input.*;

import java.util.List;

public class StudentConsole {

    private final StudentUseCase studentUseCase;
    private final CourseUseCase courseUseCase;
    private final EnrollmentUseCase enrollmentUseCase;

    public StudentConsole(StudentUseCase studentUseCase, CourseUseCase courseUseCase,
            EnrollmentUseCase enrollmentUseCase) {
        this.studentUseCase = studentUseCase;
        this.courseUseCase = courseUseCase;
        this.enrollmentUseCase = enrollmentUseCase;
    }

    public void run() {
        ConsoleHelper.printTitle("ACCESO DE ALUMNO");
        ConsoleHelper.printInfo("Ingrese su correo institucional para identificarse.");
        String email = ConsoleHelper.readLine("  Correo (ej: A00000001@utp.edu.pe): ");

        Student student = studentUseCase.findByEmail(email);
        if (student == null) {
            ConsoleHelper.printError("Correo no encontrado. Verifique sus credenciales.");
            ConsoleHelper.pause();
            return;
        }

        ConsoleHelper.printSuccess("Bienvenido, " + student.getName() + " " + student.getLastname());
        menuAlumno(student);
    }

    private void menuAlumno(Student student) {
        boolean running = true;
        while (running) {
            ConsoleHelper.printTitle("MENU ALUMNO - " + student.getName() + " " + student.getLastname());

            boolean enrollmentOpen = enrollmentUseCase.isEnrollmentOpen();
            ConsoleHelper.printInfo("Estado matricula: " + (enrollmentOpen ? "ABIERTA" : "CERRADA"));

            EnrollmentQueueEntry entry = enrollmentUseCase.getQueueEntry(student.getCode());
            if (entry != null) {
                ConsoleHelper.printInfo("Tu posicion en cola: #" + entry.getPosition());
                if (enrollmentOpen) {
                    int current = enrollmentUseCase.getCurrentServingPosition();
                    ConsoleHelper.printInfo("Turno actual siendo atendido: #" + current);
                    if (entry.getPosition() == current) {
                        ConsoleHelper.printInfo("  *** ES TU TURNO DE MATRICULARTE ***");
                    }
                }
            } else {
                ConsoleHelper.printInfo("No estas en la cola de matricula.");
            }

            ConsoleHelper.printSeparator();
            ConsoleHelper.printInfo("1. Ingresar a la cola de matricula");
            ConsoleHelper.printInfo("2. Ver estado de la matricula");
            ConsoleHelper.printInfo("3. Matricularme en un curso (si es mi turno)");
            ConsoleHelper.printInfo("4. Ver mis cursos matriculados");
            ConsoleHelper.printInfo("5. Ver cursos disponibles");
            ConsoleHelper.printInfo("0. Cerrar sesion");

            int opt = ConsoleHelper.readInt("  Opcion: ");
            switch (opt) {
                case 1 -> ingresarCola(student);
                case 2 -> verEstadoMatricula(student);
                case 3 -> matricularse(student);
                case 4 -> verCursosMatriculados(student);
                case 5 -> verCursosDisponibles();
                case 0 -> running = false;
                default -> ConsoleHelper.printError("Opcion invalida.");
            }
        }
    }

    private void ingresarCola(Student student) {
        ConsoleHelper.printTitle("UNIRSE A LA COLA DE MATRICULA");

        if (enrollmentUseCase.getQueueEntry(student.getCode()) != null) {
            ConsoleHelper.printInfo("Ya estas registrado en la cola de matricula.");
            ConsoleHelper.pause();
            return;
        }

        if (enrollmentUseCase.joinQueue(student.getCode())) {
            EnrollmentQueueEntry entry = enrollmentUseCase.getQueueEntry(student.getCode());
            ConsoleHelper.printSuccess("Te has unido a la cola.");
            if (entry != null) {
                ConsoleHelper.printInfo("Tu posicion: #" + entry.getPosition());
            }
        } else {
            ConsoleHelper.printError("No se pudo unir a la cola.");
        }
        ConsoleHelper.pause();
    }

    private void verEstadoMatricula(Student student) {
        ConsoleHelper.printTitle("ESTADO DE MATRICULA");
        boolean enrollmentOpen = enrollmentUseCase.isEnrollmentOpen();
        ConsoleHelper.printInfo("Estado general: " + (enrollmentOpen ? "ABIERTA" : "CERRADA"));

        EnrollmentQueueEntry entry = enrollmentUseCase.getQueueEntry(student.getCode());
        if (entry == null) {
            ConsoleHelper.printInfo("No estas registrado en la cola.");
        } else {
            ConsoleHelper.printInfo("Tu posicion en la cola: #" + entry.getPosition());
            if (enrollmentOpen) {
                int current = enrollmentUseCase.getCurrentServingPosition();
                if (entry.getPosition() == current) {
                    ConsoleHelper.printInfo("  *** ES TU TURNO. Puedes matricularte ahora. ***");
                } else if (entry.getPosition() > current) {
                    ConsoleHelper.printInfo("Faltan " + (entry.getPosition() - current) + " alumno(s) antes que tu.");
                }
            }
        }
        ConsoleHelper.pause();
    }

    private void matricularse(Student student) {
        ConsoleHelper.printTitle("MATRICULARSE EN CURSO");

        if (!enrollmentUseCase.isEnrollmentOpen()) {
            ConsoleHelper.printError("Las matriculas no estan abiertas en este momento.");
            ConsoleHelper.pause();
            return;
        }

        if (!enrollmentUseCase.isStudentTurn(student.getCode())) {
            ConsoleHelper.printError("Aun no es tu turno. Espera a que el administrador avance la cola.");
            EnrollmentQueueEntry entry = enrollmentUseCase.getQueueEntry(student.getCode());
            if (entry != null) {
                int current = enrollmentUseCase.getCurrentServingPosition();
                ConsoleHelper.printInfo("Tu posicion: #" + entry.getPosition() + " | Atendiendo: #" + current);
            }
            ConsoleHelper.pause();
            return;
        }

        int cursosActuales = enrollmentUseCase.getStudentEnrollments(student.getCode()).size();
        if (cursosActuales >= 5) {
            ConsoleHelper.printError("Ya alcanzaste el maximo de 5 cursos.");
            ConsoleHelper.pause();
            return;
        }

        List<Course> disponibles = courseUseCase.findAvailableForEnrollment();
        // Filtrar cursos en los que ya esta matriculado
        List<Enrollment> yaMatriculados = enrollmentUseCase.getStudentEnrollments(student.getCode());
        disponibles.removeIf(c -> yaMatriculados.stream()
                .anyMatch(e -> e.getCourseCode().equals(c.getCode())));

        if (disponibles.isEmpty()) {
            ConsoleHelper.printInfo("No hay cursos disponibles para matricularse.");
            ConsoleHelper.pause();
            return;
        }

        ConsoleHelper.printInfo("Cursos disponibles (tienes " + cursosActuales + "/5 cursos):");
        for (int i = 0; i < disponibles.size(); i++) {
            Course c = disponibles.get(i);
            int enrolled = courseUseCase.getEnrolledCount(c.getCode());
            ConsoleHelper.printInfo(String.format("  %d. %s [%s] | Vacantes: %d/%d",
                    i + 1, c.getName(), c.getCode(), enrolled, c.getMaxStudents()));
        }

        ConsoleHelper.printInfo("  0. Terminar mi matricula (avanzar turno)");
        ConsoleHelper.printSeparator();

        boolean enrolling = true;
        while (enrolling && cursosActuales < 5) {
            int sel = ConsoleHelper.readInt("  Seleccione curso (0 para terminar): ");
            if (sel == 0) {
                enrolling = false;
            } else if (sel >= 1 && sel <= disponibles.size()) {
                Course selected = disponibles.get(sel - 1);
                if (enrollmentUseCase.enrollStudent(student.getCode(), selected.getCode())) {
                    ConsoleHelper.printSuccess("Matriculado en: " + selected.getName());
                    cursosActuales++;
                    disponibles.remove(sel - 1);
                    if (disponibles.isEmpty() || cursosActuales >= 5)
                        enrolling = false;
                } else {
                    ConsoleHelper.printError("No se pudo matricular en el curso seleccionado.");
                }
            } else {
                ConsoleHelper.printError("Seleccion invalida.");
            }
        }

        // Avanzar turno al terminar
        ConsoleHelper.printSuccess("Has completado tu matricula. Avanzando turno...");
        enrollmentUseCase.advanceQueue();
        ConsoleHelper.pause();
    }

    private void verCursosMatriculados(Student student) {
        ConsoleHelper.printTitle("MIS CURSOS MATRICULADOS");
        List<Enrollment> enrollments = enrollmentUseCase.getStudentEnrollments(student.getCode());
        if (enrollments.isEmpty()) {
            ConsoleHelper.printInfo("No tienes cursos matriculados.");
        } else {
            ConsoleHelper.printInfo("Tienes " + enrollments.size() + " curso(s):");
            enrollments.forEach(e -> ConsoleHelper.printInfo("  - " + e.toString()));
        }
        ConsoleHelper.pause();
    }

    private void verCursosDisponibles() {
        ConsoleHelper.printTitle("CURSOS DISPONIBLES");
        List<Course> courses = courseUseCase.findAvailableForEnrollment();
        if (courses.isEmpty()) {
            ConsoleHelper.printInfo("No hay cursos disponibles actualmente.");
        } else {
            courses.forEach(c -> {
                int enrolled = courseUseCase.getEnrolledCount(c.getCode());
                ConsoleHelper.printInfo(String.format("  [%s] %s | Vacantes: %d/%d",
                        c.getCode(), c.getName(), enrolled, c.getMaxStudents()));
            });
        }
        ConsoleHelper.pause();
    }
}
