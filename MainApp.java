package noursalem;

import java.util.Scanner;
import java.util.ArrayList;

public class MainApp {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Load habits
        ArrayList<HabitEngine.Habit> habitList = HabitEngine.HabitStorage.loadHabits();
        HabitEngine habits = new HabitEngine(habitList);

        // Create Task Manager
        TaskManager tasks = new TaskManager(habits);

        while (true) {
            System.out.println("\n=== MAIN APP ===");
            System.out.println("1. Task Manager");
            System.out.println("2. Habit Engine");
            System.out.println("0. Exit");

            int choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    tasks.run();
                    break;
                case 2:
                    habits.habitMenu();
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
