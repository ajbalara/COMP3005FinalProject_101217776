import java.sql.*;
import java.util.Scanner;

public class Trainer extends User{
    private int id;

    public Trainer(Scanner scanner, Connection conn){
        super(scanner, conn);
        activities.add("Schedule Management");
        activities.add("Member Profile Viewing");
    }

    @Override
    public void setUp(){

    }
    @Override
    public void doActivity(){
        scanner.nextLine();
        switch (nextActivityIndex){
            case 0:
                manageSchedule();
                break;
            case 1:
                viewMemberProfile();
                break;
        }
    }

    private void manageSchedule() {
    }

    private void viewMemberProfile() {
    }
}
