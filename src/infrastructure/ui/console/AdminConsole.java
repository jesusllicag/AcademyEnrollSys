package infrastructure.ui.console;

import domain.model.*;
import domain.port.input.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class AdminConsole {

    private final StudentUseCase studentUseCase;
    private final ProfessorUseCase professorUseCase;
    private final CourseUseCase courseUseCase;
    private final EnrollmentUseCase enrollmentUseCase;
    private final AdminUseCase adminUseCase;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AdminConsole(StudentUseCase studentUseCase, ProfessorUseCase professorUseCase,
            CourseUseCase courseUseCase, EnrollmentUseCase enrollmentUseCase,
            AdminUseCase adminUseCase) {
        this.studentUseCase = studentUseCase;
        this.professorUseCase = professorUseCase;
        this.courseUseCase = courseUseCase;
        this.enrollmentUseCase = enrollmentUseCase;
        this.adminUseCase = adminUseCase;
    }

    public void run() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printTitle("PANEL DE ADMINISTRACION");
            ConsoleHelper.printInfo("1. Gestion de Alumnos");
            ConsoleHelper.printInfo("2. Gestion de Profesores");
            ConsoleHelper.printInfo("3. Gestion de Cursos y Aulas");
            ConsoleHelper.printInfo("4. Periodo y Control de Matriculas");
            ConsoleHelper.printInfo("5. Resumen de Matriculas");
            ConsoleHelper.printInfo("0. Volver al menu principal");
            ConsoleHelper.printSeparator();

            int opt = ConsoleHelper.readInt("  Opcion: ");
            switch (opt) {
                case 1 -> menuAlumnos();
                case 2 -> menuProfesores();
                case 3 -> menuCursos();
                case 4 -> menuMatriculas();
                case 5 -> mostrarResumen();
                case 0 -> running = false;
                default -> ConsoleHelper.printError("Opcion invalida.");
            }
        }
    }

    // ===================== ALUMNOS =====================

    private void menuAlumnos() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printTitle("GESTION DE ALUMNOS");
            ConsoleHelper.printInfo("1. Crear alumno");
            ConsoleHelper.printInfo("2. Buscar alumno por codigo");
            ConsoleHelper.printInfo("3. Listar todos los alumnos");
            ConsoleHelper.printInfo("4. Eliminar alumno");
            ConsoleHelper.printInfo("0. Volver");

            int opt = ConsoleHelper.readInt("  Opcion: ");
            switch (opt) {
                case 1 -> crearAlumno();
                case 2 -> buscarAlumno();
                case 3 -> listarAlumnos();
                case 4 -> eliminarAlumno();
                case 0 -> running = false;
                default -> ConsoleHelper.printError("Opcion invalida.");
            }
        }
    }

    private void crearAlumno() {
        ConsoleHelper.printTitle("CREAR ALUMNO");
        String nombre = ConsoleHelper.readLine("  Nombre: ");
        String apellido = ConsoleHelper.readLine("  Apellido: ");
        double promedio = ConsoleHelper.readDouble("  Promedio Ponderado (0-20): ");

        Student student = studentUseCase.createStudent(nombre, apellido, promedio);
        ConsoleHelper.printSuccess("Alumno creado exitosamente:");
        ConsoleHelper.printInfo(student.toString());
        ConsoleHelper.pause();
    }

    private void buscarAlumno() {
        ConsoleHelper.printTitle("BUSCAR ALUMNO");
        String codigo = ConsoleHelper.readLine("  Codigo del alumno: ");
        Student student = studentUseCase.findByCode(codigo);
        if (student == null) {
            ConsoleHelper.printError("Alumno no encontrado.");
        } else {
            ConsoleHelper.printInfo(student.toString());
        }
        ConsoleHelper.pause();
    }

    private void listarAlumnos() {
        ConsoleHelper.printTitle("LISTA DE ALUMNOS");
        List<Student> students = studentUseCase.findAll();
        if (students.isEmpty()) {
            ConsoleHelper.printInfo("No hay alumnos registrados.");
        } else {
            students.forEach(s -> ConsoleHelper.printInfo(s.toString()));
        }
        ConsoleHelper.pause();
    }

    private void eliminarAlumno() {
        ConsoleHelper.printTitle("ELIMINAR ALUMNO");
        String codigo = ConsoleHelper.readLine("  Codigo del alumno: ");
        if (studentUseCase.deleteStudent(codigo)) {
            ConsoleHelper.printSuccess("Alumno eliminado.");
        } else {
            ConsoleHelper.printError("No se pudo eliminar (codigo no existe o tiene matriculas activas).");
        }
        ConsoleHelper.pause();
    }

    // ===================== PROFESORES =====================

    private void menuProfesores() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printTitle("GESTION DE PROFESORES");
            ConsoleHelper.printInfo("1. Crear profesor");
            ConsoleHelper.printInfo("2. Buscar profesor por codigo");
            ConsoleHelper.printInfo("3. Listar todos los profesores");
            ConsoleHelper.printInfo("4. Eliminar profesor");
            ConsoleHelper.printInfo("0. Volver");

            int opt = ConsoleHelper.readInt("  Opcion: ");
            switch (opt) {
                case 1 -> crearProfesor();
                case 2 -> buscarProfesor();
                case 3 -> listarProfesores();
                case 4 -> eliminarProfesor();
                case 0 -> running = false;
                default -> ConsoleHelper.printError("Opcion invalida.");
            }
        }
    }

    private void crearProfesor() {
        ConsoleHelper.printTitle("CREAR PROFESOR");
        String nombre = ConsoleHelper.readLine("  Nombre: ");
        String apellido = ConsoleHelper.readLine("  Apellido: ");

        Professor professor = professorUseCase.createProfessor(nombre, apellido);
        ConsoleHelper.printSuccess("Profesor creado exitosamente:");
        ConsoleHelper.printInfo(professor.toString());
        ConsoleHelper.pause();
    }

    private void buscarProfesor() {
        ConsoleHelper.printTitle("BUSCAR PROFESOR");
        String codigo = ConsoleHelper.readLine("  Codigo del profesor: ");
        Professor professor = professorUseCase.findByCode(codigo);
        if (professor == null) {
            ConsoleHelper.printError("Profesor no encontrado.");
        } else {
            ConsoleHelper.printInfo(professor.toString());
        }
        ConsoleHelper.pause();
    }

    private void listarProfesores() {
        ConsoleHelper.printTitle("LISTA DE PROFESORES");
        List<Professor> professors = professorUseCase.findAll();
        if (professors.isEmpty()) {
            ConsoleHelper.printInfo("No hay profesores registrados.");
        } else {
            professors.forEach(p -> ConsoleHelper.printInfo(p.toString()));
        }
        ConsoleHelper.pause();
    }

    private void eliminarProfesor() {
        ConsoleHelper.printTitle("ELIMINAR PROFESOR");
        String codigo = ConsoleHelper.readLine("  Codigo del profesor: ");
        if (professorUseCase.deleteProfessor(codigo)) {
            ConsoleHelper.printSuccess("Profesor eliminado.");
        } else {
            ConsoleHelper.printError("No se pudo eliminar.");
        }
        ConsoleHelper.pause();
    }

    // ===================== CURSOS Y AULAS =====================

    private void menuCursos() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printTitle("GESTION DE CURSOS Y AULAS");
            ConsoleHelper.printInfo("1. Crear curso");
            ConsoleHelper.printInfo("2. Agregar aula a un curso");
            ConsoleHelper.printInfo("3. Listar todos los cursos");
            ConsoleHelper.printInfo("4. Ver aulas de un curso");
            ConsoleHelper.printInfo("5. Eliminar curso");
            ConsoleHelper.printInfo("0. Volver");

            int opt = ConsoleHelper.readInt("  Opcion: ");
            switch (opt) {
                case 1 -> crearCurso();
                case 2 -> agregarAula();
                case 3 -> listarCursos();
                case 4 -> verAulasCurso();
                case 5 -> eliminarCurso();
                case 0 -> running = false;
                default -> ConsoleHelper.printError("Opcion invalida.");
            }
        }
    }

    private void crearCurso() {
        ConsoleHelper.printTitle("CREAR CURSO");
        String nombre = ConsoleHelper.readLine("  Nombre del curso: ");
        int maxAlumnos = ConsoleHelper.readInt("  Maximo de alumnos: ");

        Course course = courseUseCase.createCourse(nombre, maxAlumnos);
        ConsoleHelper.printSuccess("Curso creado exitosamente:");
        ConsoleHelper.printInfo(course.toString());
        ConsoleHelper.printInfo("  -> Recuerde agregar al menos 3 aulas al curso.");
        ConsoleHelper.pause();
    }

    private void agregarAula() {
        ConsoleHelper.printTitle("AGREGAR AULA A CURSO");
        listarCursos();
        String codigoCurso = ConsoleHelper.readLine("  Codigo del curso: ");
        listarProfesores();
        String codigoProfesor = ConsoleHelper.readLine("  Codigo del profesor para esta aula: ");

        try {
            Classroom classroom = courseUseCase.addClassroom(codigoCurso, codigoProfesor);
            int totalAulas = courseUseCase.getClassrooms(codigoCurso).size();
            ConsoleHelper.printSuccess("Aula creada: " + classroom.getCode());
            ConsoleHelper.printInfo("  Total de aulas en el curso: " + totalAulas);
            if (totalAulas < 3) {
                ConsoleHelper.printInfo("  [!] Faltan " + (3 - totalAulas) + " aula(s) para el minimo requerido.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pause();
    }

    private void listarCursos() {
        ConsoleHelper.printTitle("LISTA DE CURSOS");
        List<Course> courses = courseUseCase.findAll();
        if (courses.isEmpty()) {
            ConsoleHelper.printInfo("No hay cursos registrados.");
        } else {
            for (Course c : courses) {
                int enrolled = courseUseCase.getEnrolledCount(c.getCode());
                int aulas = courseUseCase.getClassrooms(c.getCode()).size();
                ConsoleHelper.printInfo(c.toString() +
                        String.format(" | Matriculados: %d/%d | Aulas: %d", enrolled, c.getMaxStudents(), aulas));
            }
        }
        ConsoleHelper.pause();
    }

    private void verAulasCurso() {
        ConsoleHelper.printTitle("AULAS DEL CURSO");
        String codigoCurso = ConsoleHelper.readLine("  Codigo del curso: ");
        List<Classroom> classrooms = courseUseCase.getClassrooms(codigoCurso);
        if (classrooms.isEmpty()) {
            ConsoleHelper.printInfo("Este curso no tiene aulas registradas.");
        } else {
            classrooms.forEach(cl -> ConsoleHelper.printInfo(cl.toString()));
        }
        ConsoleHelper.pause();
    }

    private void eliminarCurso() {
        ConsoleHelper.printTitle("ELIMINAR CURSO");
        String codigo = ConsoleHelper.readLine("  Codigo del curso: ");
        if (courseUseCase.deleteCourse(codigo)) {
            ConsoleHelper.printSuccess("Curso eliminado.");
        } else {
            ConsoleHelper.printError("No se pudo eliminar el curso.");
        }
        ConsoleHelper.pause();
    }

    // ===================== MATRICULAS =====================

    private void menuMatriculas() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printTitle("CONTROL DE MATRICULAS");

            EnrollmentPeriod period = adminUseCase.getCurrentPeriod();
            if (period != null) {
                ConsoleHelper.printInfo("Periodo actual: " + period.toString());
            } else {
                ConsoleHelper.printInfo("No hay periodo de matricula configurado.");
            }
            ConsoleHelper.printSeparator();
            ConsoleHelper.printInfo("1. Configurar periodo de matricula");
            ConsoleHelper.printInfo("2. Aperturar matriculas de TODOS los cursos");
            ConsoleHelper.printInfo("3. Cerrar matriculas de TODOS los cursos");
            ConsoleHelper.printInfo("4. Aperturar matricula de un curso especifico");
            ConsoleHelper.printInfo("5. Cerrar matricula de un curso especifico");
            ConsoleHelper.printInfo("6. Ver cola de matricula");
            ConsoleHelper.printInfo("7. Avanzar turno en la cola");
            ConsoleHelper.printInfo("0. Volver");

            int opt = ConsoleHelper.readInt("  Opcion: ");
            switch (opt) {
                case 1 -> configurarPeriodo();
                case 2 -> {
                    if (adminUseCase.openAllEnrollments()) {
                        enrollmentUseCase.openEnrollment();
                        ConsoleHelper.printSuccess("Todas las matriculas aperturadas y cola ordenada.");
                    }
                    ConsoleHelper.pause();
                }
                case 3 -> cerrarTodasLasMatriculas();
                case 4 -> aperturarCursoEspecifico();
                case 5 -> cerrarCursoEspecifico();
                case 6 -> verCola();
                case 7 -> avanzarTurno();
                case 0 -> running = false;
                default -> ConsoleHelper.printError("Opcion invalida.");
            }
        }
    }

    private void cerrarTodasLasMatriculas() {
        adminUseCase.closeAllEnrollments();
        enrollmentUseCase.closeEnrollment();
        ConsoleHelper.printSuccess("Todas las matriculas cerradas.");

        String resp = ConsoleHelper.readLine("  ¿Eliminar la cola de matricula actual? (S/N): ");
        if (resp.equalsIgnoreCase("S")) {
            enrollmentUseCase.clearQueue();
            ConsoleHelper.printSuccess("Cola de matricula eliminada.");
        } else {
            ConsoleHelper.printInfo("Cola de matricula preservada para el siguiente periodo.");
        }
        ConsoleHelper.pause();
    }

    private void configurarPeriodo() {
        ConsoleHelper.printTitle("CONFIGURAR PERIODO DE MATRICULA");
        String inicio = ConsoleHelper.readLine("  Fecha inicio (dd/MM/yyyy): ");
        String fin = ConsoleHelper.readLine("  Fecha fin (dd/MM/yyyy): ");
        try {
            LocalDate startDate = LocalDate.parse(inicio, DATE_FMT);
            LocalDate endDate = LocalDate.parse(fin, DATE_FMT);
            EnrollmentPeriod period = adminUseCase.setEnrollmentPeriod(startDate, endDate);
            ConsoleHelper.printSuccess("Periodo configurado: " + period.toString());
        } catch (DateTimeParseException e) {
            ConsoleHelper.printError("Formato de fecha invalido. Use dd/MM/yyyy.");
        }
        ConsoleHelper.pause();
    }

    private void aperturarCursoEspecifico() {
        ConsoleHelper.printTitle("APERTURAR MATRICULA - CURSO ESPECIFICO");
        listarCursos();
        String codigo = ConsoleHelper.readLine("  Codigo del curso: ");
        if (adminUseCase.openCourseEnrollment(codigo)) {
            ConsoleHelper.printSuccess("Matricula aperturada para el curso " + codigo);
        } else {
            ConsoleHelper.printError("No se pudo aperturar la matricula.");
        }
        ConsoleHelper.pause();
    }

    private void cerrarCursoEspecifico() {
        ConsoleHelper.printTitle("CERRAR MATRICULA - CURSO ESPECIFICO");
        String codigo = ConsoleHelper.readLine("  Codigo del curso: ");
        if (adminUseCase.closeCourseEnrollment(codigo)) {
            ConsoleHelper.printSuccess("Matricula cerrada para el curso " + codigo);
        } else {
            ConsoleHelper.printError("No se pudo cerrar la matricula.");
        }
        ConsoleHelper.pause();
    }

    private void verCola() {
        ConsoleHelper.printTitle("COLA DE MATRICULA");
        List<EnrollmentQueueEntry> queue = enrollmentUseCase.getQueue();
        if (queue.isEmpty()) {
            ConsoleHelper.printInfo("La cola esta vacia.");
        } else {
            int currentPos = enrollmentUseCase.getCurrentServingPosition();
            ConsoleHelper.printInfo("Atendiendo posicion: " + currentPos);
            ConsoleHelper.printSeparator();
            queue.forEach(entry -> {
                String marker = entry.getPosition() == currentPos ? " <<< TURNO ACTUAL" : "";
                ConsoleHelper.printInfo(entry.toString() + marker);
            });
        }
        ConsoleHelper.pause();
    }

    private void avanzarTurno() {
        int current = enrollmentUseCase.getCurrentServingPosition();
        enrollmentUseCase.advanceQueue();
        int next = enrollmentUseCase.getCurrentServingPosition();
        ConsoleHelper.printSuccess("Turno avanzado: " + current + " -> " + next);
        ConsoleHelper.pause();
    }

    // ===================== RESUMEN =====================

    @SuppressWarnings("unchecked")
    private void mostrarResumen() {
        ConsoleHelper.printTitle("RESUMEN DE MATRICULAS");
        Map<String, Object> summary = adminUseCase.getEnrollmentSummary();

        EnrollmentPeriod period = (EnrollmentPeriod) summary.get("periodoActual");
        if (period != null) {
            ConsoleHelper.printInfo("Periodo: " + period.toString());
        }
        ConsoleHelper.printInfo("Total de cursos: " + summary.get("totalCursos"));
        ConsoleHelper.printInfo("Total de matriculas: " + summary.get("totalMatriculas"));
        ConsoleHelper.printSeparator();

        List<Map<String, Object>> cursos = (List<Map<String, Object>>) summary.get("cursos");
        for (Map<String, Object> courseData : cursos) {
            Course course = (Course) courseData.get("curso");
            int total = (int) courseData.get("total");
            int vacantes = (int) courseData.get("vacantesDisponibles");
            ConsoleHelper.printInfo("\n  Curso: " + course.getName() + " [" + course.getCode() + "]");
            ConsoleHelper.printInfo("  Matriculados: " + total + "/" + course.getMaxStudents() +
                    " | Vacantes: " + vacantes);

            List<Enrollment> enrollments = (List<Enrollment>) courseData.get("matriculados");
            if (enrollments.isEmpty()) {
                ConsoleHelper.printInfo("  -> Sin alumnos matriculados");
            } else {
                enrollments.forEach(
                        e -> ConsoleHelper.printInfo("     - " + e.getStudentName() + " [" + e.getStudentCode() + "]"));
            }
        }
        ConsoleHelper.pause();
    }
}
