import java.sql.Connection;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public abstract class User {
    protected final Scanner scanner;
    protected final Connection conn;
    protected ArrayList<String> activities;
    protected int nextActivityIndex;
    public static final String TERMINATE_KEYWORD = "end";
    public User(Scanner scanner, Connection conn){
        this.scanner = scanner;
        this.conn = conn;
        activities = new ArrayList<>();
    }
    public abstract void setUp();
    public void displayActivities(){
        System.out.println(this.getClass().getSimpleName() + " Main Menu");
        int i = 1;
        for(String activity: activities){
            System.out.println(i + ": " + activity);
            i++;
        }
    }

    public void getActivity() {
        int input;
        while(true){
            try {
                System.out.print("Enter the number of the activity you wish to perform, or enter 0 to exit: ");
                input = scanner.nextInt();
                if (input < 0 || input > activities.size()){
                    throw new InputMismatchException();
                }
                nextActivityIndex = input - 1;
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number corresponding to an activity.");
                scanner.nextLine();
            }
        }
    }

    public boolean checkIfExit(){
        return nextActivityIndex == -1;
    }

    public abstract void doActivity();
}
