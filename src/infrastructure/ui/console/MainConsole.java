package infrastructure.ui.console;

import domain.port.input.*;

public class MainConsole {

    private final AdminConsole adminConsole;
    private final StudentConsole studentConsole;

    public MainConsole(StudentUseCase studentUseCase, ProfessorUseCase professorUseCase,
            CourseUseCase courseUseCase, EnrollmentUseCase enrollmentUseCase,
            AdminUseCase adminUseCase) {
        this.adminConsole = new AdminConsole(studentUseCase, professorUseCase,
                courseUseCase, enrollmentUseCase, adminUseCase);
        this.studentConsole = new StudentConsole(studentUseCase, courseUseCase, enrollmentUseCase);
    }

    public void run() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printTitle("SISTEMA DE MATRICULA - UTP");
            ConsoleHelper.printInfo("1. Acceso Administrador");
            ConsoleHelper.printInfo("2. Acceso Alumno");
            ConsoleHelper.printInfo("0. Salir");
            ConsoleHelper.printSeparator();

            int opt = ConsoleHelper.readInt("  Seleccione una opcion: ");
            switch (opt) {
                case 1 -> adminConsole.run();
                case 2 -> studentConsole.run();
                case 0 -> {
                    ConsoleHelper.printInfo("Cerrando sistema...");
                    running = false;
                }
                default -> ConsoleHelper.printError("Opcion invalida.");
            }
        }
    }
}
