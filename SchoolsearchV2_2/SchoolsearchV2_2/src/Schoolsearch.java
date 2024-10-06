import java.io.*;
import java.util.*;

public class Schoolsearch {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("\nEnter search instruction\nS[tudent]: <lastname> B[us]\nS[tudent]: <lastname>\nT[eacher]: <lastname>\nC[lassroom]: <number>\nB[us]: <number>\nQ[uit]\n");
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Q") || input.equalsIgnoreCase("Quit")) {
                break;
            }

            processInput(input);
        }

        scanner.close();
        System.out.println("Program terminated.");
    }

    private static void processInput(String input) {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            System.out.println("Invalid command. Please try again.");
            return;
        }

        String command = parts[0];
        String argument = input.substring(input.indexOf(' ') + 1).trim();

        switch (command) {
            case "S:":
            case "Student:":
                if (input.endsWith("Bus") || input.endsWith("B")) {
                    argument = input.substring(input.indexOf(' '), input.indexOf(" B")).trim();
                    StudentSearch.searchByLastNameAndBus(argument);
                } else {
                    StudentSearch.searchByLastName(argument);
                }
                break;
            case "T:":
            case "Teacher:":
                TeacherSearch.searchByLastName(argument);
                break;
            case "C:":
            case "Classroom:":
                ClassroomSearch.searchByNumber(argument);
                break;
            case "B:":
            case "Bus:":
                BusSearch.searchByNumber(argument);
                break;
            default:
                System.out.println("Invalid command. Please try again.");
                break;
        }
    }
}

class StudentSearch {

    public static void searchByLastName(String lastName) {
        search("list.txt", lastName, 0, result -> {
            String teacherName = TeacherSearch.getTeacherByClassroom(result[3]);
            if (teacherName != null) {
                System.out.println("Found student: " + result[0] + " " + result[1] + " in class: " + result[3] + ", Teacher: " + teacherName);
            } else {
                System.out.println("Found student: " + result[0] + " " + result[1] + " in class: " + result[3] + ", Teacher: Not found");
            }
        });
    }

    public static void searchByLastNameAndBus(String lastName) {
        search("list.txt", lastName, 0, result -> {
            System.out.println("Found student: " + result[0] + " " + result[1] + " on bus: " + result[4]);
        });
    }

    public static void search(String fileName, String value, int column, SearchResultProcessor processor) {
        boolean found = false;
        long startTime = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[column].trim().equalsIgnoreCase(value)) {
                    processor.process(parts);
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        if (!found) {
            System.out.println("No results found for: " + value);
        }

        Utils.printDuration(startTime);
    }
}

class TeacherSearch {

    public static void searchByLastName(String lastName) {
        search("teacher.txt", lastName, 0, result -> {
            System.out.println("Found teacher: " + result[0] + " " + result[1] + " in classroom: " + result[2]);
        });
    }

    public static void search(String fileName, String value, int column, SearchResultProcessor processor) {
        boolean found = false;
        long startTime = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[column].trim().equalsIgnoreCase(value)) {
                    processor.process(parts);
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        if (!found) {
            System.out.println("No results found for: " + value);
        }

        Utils.printDuration(startTime);
    }

    public static String getTeacherByClassroom(String classroom) {
        try (BufferedReader br = new BufferedReader(new FileReader("teacher.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[2].trim().equalsIgnoreCase(classroom)) {
                    return parts[0].trim() + " " + parts[1].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return null;
    }
}


class ClassroomSearch {

    public static void searchByNumber(String classroomNumber) {
        StudentSearch.search("list.txt", classroomNumber, 3, result -> {
            String teacherName = TeacherSearch.getTeacherByClassroom(classroomNumber);
            if (teacherName != null) {
                System.out.println("Found student in class " + result[3] + ": " + result[0] + " " + result[1] + ", Teacher: " + teacherName);
            } else {
                System.out.println("Found student in class " + result[3] + ": " + result[0] + " " + result[1] + ", Teacher: Not found");
            }
        });
    }
}

class BusSearch {

    public static void searchByNumber(String busNumber) {
        StudentSearch.search("list.txt", busNumber, 4, result -> {
            System.out.println("Found student on bus " + result[4] + ": " + result[0] + " " + result[1]);
        });
    }
}

@FunctionalInterface
interface SearchResultProcessor {
    void process(String[] result);
}

class Utils {
    public static void printDuration(long startTime) {
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Search took: " + duration + " ms");
    }
}
