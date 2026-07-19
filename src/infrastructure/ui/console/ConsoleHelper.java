package infrastructure.ui.console;

import java.util.Scanner;

public class ConsoleHelper {

    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleHelper() {
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Ingrese un numero valido.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val < 0 || val > 20) {
                    System.out.println("  [!] El promedio debe estar entre 0 y 20.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Ingrese un numero valido (ej: 15.5).");
            }
        }
    }

    public static void printSeparator() {
        System.out.println("=".repeat(60));
    }

    public static void printTitle(String title) {
        System.out.println();
        printSeparator();
        System.out.println("  " + title);
//        printSeparator();
    }

    public static void printSuccess(String msg) {
        System.out.println("  [OK] " + msg);
    }

    public static void printError(String msg) {
        System.out.println("  [ERROR] " + msg);
    }

    public static void printInfo(String msg) {
        System.out.println("  " + msg);
    }

    public static void pause() {
        System.out.print("\n  Presione ENTER para continuar...");
        scanner.nextLine();
    }
}
