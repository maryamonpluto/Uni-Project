package noursalem;

import java.util.Scanner;
import java.util.ArrayList;

public class MainApp {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);  // Scanner for user input

        // Load previously saved habits from JSON file
        ArrayList<HabitEngine.Habit> habitList = HabitEngine.HabitStorage.loadHabits();
        HabitEngine habits = new HabitEngine(habitList);

         // Load TaskManager and pass HabitEngine to it
        TaskManager tasks = new TaskManager(habits);
        
        // Main loop of the application
        while (true) {
            System.out.println("\n=== MAIN APP ===");
            System.out.println("1. Task Manager");
            System.out.println("2. Habit Engine");
            System.out.println("0. Exit");

            int choice = input.nextInt();  // Read user's menu choice
            input.nextLine();

            switch (choice) {
                case 1:
                    tasks.run();   // Run the TaskManager menu
                    break;
                case 2:
                    habits.habitMenu();   // Run the HabitEngine menu
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");   // Handle invalid input
            }
        }
    }
}
