package noursalem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class HabitEngine {
	Scanner input = new Scanner(System.in);
    ArrayList<Habit> habits;
    
    // No-arg constructor
    public HabitEngine() {
    this.habits = HabitStorage.loadHabits();
    }
    
    // Existing constructor
    public HabitEngine(ArrayList<Habit> habits) {
        this.habits = habits;
    }

    
    class Habit {
        String id;
        String title;
        String frequency;  
        int streak;
        int goal;
        String lastCheckDate;
        boolean autoAdjust;

        // Constructor 1: default
        Habit() {
            id = java.util.UUID.randomUUID().toString();
            streak = 0;
            goal = 30;
            autoAdjust = true;
            lastCheckDate = "";
        }

        // Constructor 2: with parameters
        Habit(String title, String frequency, int goal) {
            this.id = java.util.UUID.randomUUID().toString();
            this.title = title;
            this.frequency = frequency;
            this.goal = goal;

            this.streak = 0;
            this.autoAdjust = true;
            this.lastCheckDate = "";
        }

        public String toString() {
            return title + " [Freq:" + frequency + "] [Streak:" + streak + "/" + goal + "] [Last: " + lastCheckDate + "]";
        }
    }
    
    
    
    class HabitStorage {
        private static final String file = "habits.json";
        private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

        public static void saveHabits(ArrayList<Habit> habits) {
            try(FileWriter fw = new FileWriter(file)){
                gson.toJson(habits, fw);
            } catch (Exception e) { e.printStackTrace(); }
        }

        public static ArrayList<Habit> loadHabits() {
            ArrayList<Habit> habits = new ArrayList<>();
            try(BufferedReader br = new BufferedReader(new FileReader(file))){
                habits = gson.fromJson(br, new TypeToken<ArrayList<Habit>>(){}.getType());
            } catch(Exception e) {}
            return habits == null ? new ArrayList<>() : habits;
        }
    }


    
    
    public void habitMenu() {
    	// Warn if any habit was missed
		   String today = LocalDate.now().toString();
		   for(Habit h : habits){
		       if(h.lastCheckDate == null || h.lastCheckDate.isEmpty()) continue;

		       if(h.frequency.equals("DAILY") && !h.lastCheckDate.equals(today)){
		           System.out.println("⚠ You missed daily habit: " + h.title);
		       }
		       if(h.frequency.equals("WEEKLY") && !h.lastCheckDate.equals(today)){
		           System.out.println("⚠ You missed weekly habit: " + h.title);
		       }
		   }
		    while(true){
		        System.out.println("\n=== *Habit Engine* ===");
		        System.out.println("1. Add Habit");
		        System.out.println("2. View Habits");
		        System.out.println("3. Check-in");
		        System.out.println("4. Delete Habit");
		        System.out.println("5. Back");

		        int c = input.nextInt();
		        input.nextLine();

		        switch(c){
		            case 1: addHabit(); break;
		            case 2: viewHabits(); break;
		            case 3: checkIn(); break;
		            case 4: deleteHabit(); break;
		            case 5: return;
		        }
		    }
    }
    
    
    public void addHabit() {
    	System.out.print("Habit Title: ");
	    String title = input.nextLine();

	    System.out.print("Frequency (DAILY/WEEKLY): ");
	    String freq = input.nextLine().trim().toUpperCase();
	    while (!freq.equals("DAILY") && !freq.equals("WEEKLY")) {
	        System.out.print("Invalid input, choose DAILY/WEEKLY: ");
	        freq = input.nextLine().trim().toUpperCase();
	    }

	    System.out.print("Streak goal (days/weeks) : ");
	    String goalInput = input.nextLine();
	    while(!goalInput.matches("\\d+")) {
	        System.out.print("Numbers only! Enter streak goal (EX: 30 ): ");
	        goalInput = input.nextLine();
	    }
	    
	    int goal = Integer.parseInt(goalInput);

	    Habit h = new Habit(title, freq, goal);
	    habits.add(h);
	    HabitStorage.saveHabits(habits);
	    System.out.println("Habit added successfully!");
    }
    
    
    public void viewHabits() {
    	System.out.println("\n=== Your Habits Pookie : ===");
	    for(int i=0;i<habits.size();i++){
	    	Habit h = habits.get(i);
	    	double percent = ((double)h.streak / h.goal) * 100;  // calculate percentage
	        System.out.println(i + ") " + h + " [" + String.format("%.2f%%", percent) + "]");

	    }
    }
    
    
    public void checkIn() {
    	if (habits.isEmpty()) {
	        System.out.println("No habits available to check-in.");
	        return;
	    }

	    viewHabits();

	    int i = -1;
	    while (true) {
	        System.out.print("Enter habit index to check-in: ");
	        String inputStr = input.nextLine();

	        try {
	            i = Integer.parseInt(inputStr);
	            if (i >= 0 && i < habits.size()) break; // valid index
	            else System.out.println("Invalid index! Please enter a number between 0 and " + (habits.size() - 1));
	        } catch (NumberFormatException e) {
	            System.out.println("Please enter a valid number.");
	        }
	    }

	    Habit h = habits.get(i);

	    // Prevent double check-in
	    if (!canCheckIn(h)) {
	        System.out.println("You already checked in for this!");
	        return;
	    }

	    String today = LocalDate.now().toString();

	    // Reset streak if missed period
	    if (h.lastCheckDate != null && !h.lastCheckDate.isEmpty()) {
	        LocalDate last = LocalDate.parse(h.lastCheckDate);

	        if (h.frequency.equals("DAILY")) {
	            LocalDate yesterday = LocalDate.now().minusDays(1);
	            if (!last.equals(yesterday)) {
	                h.streak = 0;
	                System.out.println("⚠ Missed a day! Streak reset :(");
	            }
	        }

	        if (h.frequency.equals("WEEKLY")) {
	            java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.ISO;
	            int lastWeek = last.get(weekFields.weekOfWeekBasedYear());
	            int currentWeek = LocalDate.now().get(weekFields.weekOfWeekBasedYear());
	            int lastYear = last.getYear();
	            int currentYear = LocalDate.now().getYear();
	            if (currentYear > lastYear || currentWeek - lastWeek > 1) {
	                h.streak = 0;
	                System.out.println("⚠ Missed a week! Streak reset :(");
	            }
	        }
	    }

	    h.streak++;                 // Increment streak
	    h.lastCheckDate = today;    // Update last check date

	    // Auto-adjust goal
	    if (h.streak >= h.goal && h.autoAdjust) {
	        System.out.println("🎉 Goal reached!! Increasing goal automatically.");
	        h.goal += 7;
	    }

	    HabitStorage.saveHabits(habits);
	    System.out.println("✅ Great job! Streak updated :)");
	    Soundplayer.playbeeb();
    }
    
    
    public void deleteHabit() {
    	viewHabits();
	    System.out.print("Enter habit index to delete: ");
	    int i = input.nextInt();
	    input.nextLine();

	    if(i<0 || i>=habits.size()) return;

	    habits.remove(i);
	    HabitStorage.saveHabits(habits);
	    System.out.println("Habit deleted.");
    }
    
    
    public boolean canCheckIn(Habit h) {
	    String today = LocalDate.now().toString();

	    // If never checked in, user can check in
	    if (h.lastCheckDate == null || h.lastCheckDate.isEmpty()) return true;

	    // Daily habit: can't check in twice on the same day
	    if (h.frequency.equals("DAILY")) {
	        return !h.lastCheckDate.equals(today);
	    }

	    // Weekly habit: can't check in twice in the same week
	    if (h.frequency.equals("WEEKLY")) {
	        LocalDate last = LocalDate.parse(h.lastCheckDate);
	        LocalDate now = LocalDate.now();
	        java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.ISO;
	        int lastWeek = last.get(weekFields.weekOfWeekBasedYear());
	        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
	        int lastYear = last.getYear();
	        int currentYear = now.getYear();
	        // Allow check-in only if it's a new week
	        return !(lastWeek == currentWeek && lastYear == currentYear);
	    }

	    return true;
    }
    
    public void convertTaskToHabit(Task t) {

        if(t.repeat.equalsIgnoreCase("NONE")) return;

        this.habits = HabitStorage.loadHabits();

        boolean exists = false;
        for (Habit h : habits) {
            if (h.title.equalsIgnoreCase(t.title)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            Habit h = new Habit();
            h.title = t.title;
            h.frequency = t.repeat.equalsIgnoreCase("DAILY") ? "DAILY" : "WEEKLY";

            habits.add(h);
            HabitStorage.saveHabits(habits);
            System.out.println("🔄 Task converted to Habit automatically!");
        } else {
            System.out.println("⚠ Habit already exists.");
        }
    }

}
