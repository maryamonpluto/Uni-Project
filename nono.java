package noursalem;

import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.awt.Toolkit;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.google.gson.JsonSyntaxException;

import noursalem.HabitEngine;

//CRUD CREATE READ UPDATE DELETE
//main

public class nono {
    public static void main(String[] args) {
        // create HabitEngine instance
        HabitEngine habits = new HabitEngine();
        // pass it to TaskManager
        TaskManager manager = new TaskManager(habits);
        manager.run();
    }
}





class Task {
	String id;                                             //special for every task
	String title;
	String description;
	String priority;                                       //low , med , high
	String category;
	String status;                                         //new , in_progress , done
	int duration;
	boolean completed;
	String duedate;
	String attach;
	String repeat;                                          // None , Daily , weekly , monthly
	  Task() {                                              //constructor
		 id = java.util.UUID.randomUUID().toString();       // random id
		 completed = false;
		 repeat = "None";
		 duedate ="";
	 }
	                                                         //built in method toString but i edit it to be fit
	  public String toString() {
		return title + " ["+priority+"] ["+status+"] ["+(duedate==null?"":duedate)+"] ["+duration+"min] ["+category+"]" + (attach!=null?" [Attachment]":"") + (completed?" [Completed]":"") + " [Repeat: "+repeat+"]";
				
	  }	
}



class Storage{
private static final String file = "tasks.json" ;
private static Gson gson = new GsonBuilder().setPrettyPrinting().create();                      //make file clear and pretty organised rather than one line
// java objects to 	Json , Json to java objects
public static void savetasks (ArrayList<Task>tasks) {
	try (FileWriter filewriter = new FileWriter(file)){
		gson.toJson(tasks,filewriter);
	} 
	catch(Exception e) {
		e.printStackTrace();
	}
}
public static ArrayList<Task> loadtask(){
	ArrayList<Task> tasks = new ArrayList<>();
	//Exception handling
	try (BufferedReader br= new BufferedReader(new FileReader(file))){
		tasks = gson.fromJson(br, new TypeToken<ArrayList<Task>>() {}.getType());
	}catch (IOException e) 
	{e.printStackTrace();}
	catch (JsonSyntaxException e) {
	    e.printStackTrace();
	}
	if (tasks == null) 
	tasks = new ArrayList<>();
	return tasks;
}
}



class Soundplayer{
	public static void playbeeb(){
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
} 


class TaskManager{
	ArrayList<Task> tasks;
	ArrayList<String> categories;
	Scanner input = new Scanner(System.in);
	
	HabitEngine habitEngine; // reference to HabitEngine
	

	
	public TaskManager(HabitEngine habitEngine) {
	    this.tasks = Storage.loadtask();
	    this.categories = new ArrayList<>(Arrays.asList("Work","Personal","Shopping","General"));
	    this.habitEngine = habitEngine; // <--- store the reference
	}




	public void run() {
		while(true) {
			System.out.println("\n==== TASK MENGER ====");
			System.out.println("1. Add Task");
			System.out.println("2. View Tasks");
			System.out.println("3. Edit Task");
			System.out.println("4. Delete Task");
			System.out.println("5. Sort Task");
			System.out.println("6. Exit");
			System.out.println("Enter the number of your choice: ");
			int ch = input.nextInt();
			input.nextLine();
			switch (ch) {
			case 1:
				addtask(input);
				break;
			case 2:
				viewtask(input);
				break;
			case 3:
				edittask(input);
				break;
			case 4:
				deletetask(input);
				break;
			case 5:
				sorttask(input);
				break;
			case 6:
				Storage.savetasks(tasks);
				System.out.println("Bye Bye love");
				return;
				
			default :
         System.out.println("Not valid option");
			}
			}  
	}
			
			
			public  void addtask (Scanner input) {
				Task t = new Task();
				System.out.println("Title: ");
				t.title = input.nextLine();
				System.out.println("Description: ");
				t.description = input.nextLine();
				System.out.println("Due date and time (YYYY-MM-DD HH:mm): ");
				t.duedate = input.nextLine();
				
				  t.priority = getValidatedChoice("Priority (LOW/MED/HIGH): ", Arrays.asList("LOW","MED","HIGH"),input);
			        t.status = getValidatedChoice("Status (NEW/IN_PROGRESS/DONE): ", Arrays.asList("NEW","IN_PROGRESS","DONE"),input);

			        System.out.print("Category (existing or new): "); //adding a category
			        String cat = input.nextLine();
			        if(!categories.contains(cat)) 
			        	categories.add(cat);
			        t.category = cat;

			        System.out.print("Duration in minutes: ");
			        try{ t.duration = input.nextInt();
			        input.nextLine();
			        }
			        catch(Exception e){ t.duration=0; }

			        System.out.print("Attach file? (y/n): ");
			        String opt = input.nextLine();
			        if(opt.equalsIgnoreCase("y")){                         //ignoring upper case and lower case
			            System.out.println("Path: "); 
			            t.attach = input.nextLine();
			        }

			        t.repeat = getValidatedChoice("Recurrence (NONE/DAILY/WEEKLY/MONTHLY): ", Arrays.asList("NONE","DAILY","WEEKLY","MONTHLY"),input);
			        tasks.add(t);
			        Storage.savetasks(tasks);
			        System.out.println("Task added!");

			        // Automatically convert repeated task to habit
			        if (!t.repeat.equalsIgnoreCase("NONE") && habitEngine != null) {
			            habitEngine.convertTaskToHabit(t);
			        }

			        // Reminder if it is close
			        if(t.duedate != null ) { 
			            System.out.println("⚠ Remember your Task");
			        }
			}

			    public void viewtask(Scanner input){
			        System.out.println("1. All  2. Completed  3. Incomplete  4. Only with Attachments");
			        System.out.println("Enter the number of your choice:");
			        int choice = input.nextInt();
			        input.nextLine();
			        for(int i=0;i<tasks.size();i++){                              //view all tasks
			            Task t = tasks.get(i);
			            if(choice==2 && !t.completed)                                 //completed only
			            	continue;
			            if(choice==3 && t.completed)                                   //incompleted only
			            	continue;
			            if(choice==4 && t.attach==null)                         //with attach
			            	continue;
			            System.out.println(i+") "+t.toString());
			        }

			        System.out.print("Mark complete? ُEnter the index of the task: ");
			        System.out.println("(If you do not wanna mark a task complete enter ( -1 ) )");
			        int idx = input.nextInt();
			        input.nextLine();
			        if(idx>=0 && idx<tasks.size()){
			            tasks.get(idx).completed=true;
			            Soundplayer.playbeeb();
			            Storage.savetasks(tasks);
			        }
			    }

			    
			    public void edittask(Scanner input){
			        System.out.print("Enter task index: ");
			        int i = input.nextInt();
			        input.nextLine();

			        if(i<0 || i>=tasks.size())
			        { 
			        	System.out.println("Invalid index"); 
			        return; 
			        }
			        Task t = tasks.get(i);
			        System.out.print("New title (current: "+t.title+"): "); 
			        String s=input.nextLine(); 
			        if(!s.isEmpty()) 
			        	t.title=s;
			        System.out.print("New status (current: "+t.status+"): "); 
			        s=input.nextLine(); 
			        if(!s.isEmpty()) 
			        	t.status=s;
			        System.out.print("New priority (current: "+t.priority+"): ");
			        s=input.nextLine(); 
			        if(!s.isEmpty()) 
			        	t.priority=s;
			        System.out.print("New category (current: "+t.category+"): ");
			        s=input.nextLine(); 
			        if(!s.isEmpty()) 
			        	t.category=s;
			        System.out.print("New duration (minutes, current: "+t.duration+"): "); 
			        s=input.nextLine();  
			        if(!s.isEmpty())
			        t.duration=Integer.parseInt(s);
			        System.out.print("New due date and time (YYYY-MM-DD HH:mm, current: "+(t.duedate==null?"":t.duedate)+"): "); 
			        s=input.nextLine(); 
			        System.out.print("Recurrence (current: "+t.repeat+"): ");
			        s=input.nextLine(); 
			        if(!s.isEmpty()) 
			        t.repeat=s;

			        Storage.savetasks(tasks);
			        System.out.println("Task updated!");
			    }

			    public void deletetask(Scanner input){
			        System.out.print("Enter task index: ");
			        int i = input.nextInt();
			        input.nextLine();

			        if(i<0 || i>=tasks.size())
			        { 
			        	System.out.println("Invalid index"); 
			        
			        return;
			        }
			        
			        System.out.print("Are you sure? (y/n): ");
			        String ans=input.nextLine();
			        if(ans.equalsIgnoreCase("y")){
			            tasks.remove(i);
			            Storage.savetasks(tasks);
			            System.out.println("Task deleted.");
			        }
			    }

			    public void sorttask(Scanner input){
			        System.out.println("1. By Due Date  2. By Priority");
			        System.out.println("Enter the number of your choice");
			        int c = input.nextInt();
			        input.nextLine();

			        switch(c){
			            case 1: 
			            	tasks.sort((t1,t2)->{                           //comparing between t1 , t2 by due date
			                if(t1.duedate==null)
			                	return 1;                                   //put it in the end if it has no due date
			                if(t2.duedate==null) 
			                	return -1;                                  //put it in the end if it has no due date
			                
			                return t1.duedate.compareTo(t2.duedate);                //comparing between their due date
			            }); 
			            	break;
			            	
			            case 2: 
			            	tasks.sort((t1,t2)-> 
			            	t2.priority.compareTo(t1.priority)); 
			            	break;
			            default: 
			            	System.out.println("Invalid choice"); 
			            	return;
			        }
			        System.out.println("Tasks sorted!");
			        Storage.savetasks(tasks);
			    }

			   public String getValidatedChoice(String prompt, List<String> options,Scanner input){                  //ensures user enters right values and not making error if the choice is not valid
			        String choice;
			        while(true){
			            System.out.print(prompt);
			            choice = input.nextLine().toUpperCase();
			            if(options.contains(choice)) 
			            	return choice;
			            System.out.println("Invalid input, choose from "+options);
			        }
                }
}




