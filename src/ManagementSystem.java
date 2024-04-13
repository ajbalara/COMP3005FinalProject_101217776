import java.sql.*;
import java.util.Scanner;

public class ManagementSystem {
    public static final String url = "jdbc:postgresql://localhost:5432/COMP3005FinalProject";
    public static final String user = "postgres";
    public static final String password = "password";
    public static final Scanner SCANNER = new Scanner(System.in);
    public static final int MEMBERSHIP_FEE = 40;
    public static final int REGISTRATION_FEE = 100;

    private static Connection conn;

    public static Connection getConn() {
        return conn;
    }

    private static User retrieveUserType(){
        String input;
        while(true){
            System.out.print("Enter your user type: ");
            input = SCANNER.nextLine().toLowerCase();
            switch (input){
                case "member":
                    return new Member(SCANNER, conn);
                case "trainer":
                    return new Trainer(SCANNER, conn);
                case "administrator":
                    return new Administrator(SCANNER, conn);
            }
            System.out.println("Invalid user type");
        }
    }

    public static void setUpConnection(){
        System.out.println("Setting up DB Connection...");
        try{
            Class.forName("org.postgresql.Driver");
            // Connect to the database
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("DB Connection Formed Successfully!");
            if(conn == null){
                throw new SQLException();
            }
        } catch (ClassNotFoundException | SQLException e){
            System.out.println("Error Connecting to DB");
            System.exit(1);
        }
    }

    public static void end(){
        System.out.println("Thank you for using our app! We hope you have a great day");
        try{
            SCANNER.close();
            conn.close();
            Thread.sleep(2000);
        } catch (SQLException | InterruptedException e){}
    }


    public static void main(String[] args) {
        System.out.println("Welcome to Ottawa Health and Fitness Club!");
        setUpConnection();
        System.out.println("Please enter the type of user that you are: member, trainer or administrator");
        User user = retrieveUserType();
        user.setUp();
        while(true){
            user.displayActivities();
            user.getActivity();
            if(user.checkIfExit()){
                break;
            }
            user.doActivity();
        }
        end();
    }
}
