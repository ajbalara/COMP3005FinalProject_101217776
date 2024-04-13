import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
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
        int input;
        while (true) {
            try {
                System.out.print("Enter your trainer ID: ");
                input = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                if (input <= 0) {
                    throw new InputMismatchException();
                }
                if (!trainerInDB(input)) {
                    System.out.println("Trainer ID not found in DB. Please enter a valid trainer ID");
                } else {
                    System.out.println("Trainer Found in DB");
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a positive integer for member ID.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }

    private boolean trainerInDB(int inputId){
        try{
            Statement stmt = conn.createStatement();
            String SQL = "SELECT trainerId FROM TrainerData";
            ResultSet rs = stmt.executeQuery(SQL); // Process the result set
            ArrayList<Integer> trainerIds = new ArrayList<>();
            while(rs.next()){
                trainerIds.add(rs.getInt("trainerId"));
            }
            // Close resources
            rs.close();
            stmt.close();
            boolean inDB = trainerIds.contains(inputId);
            id = inputId;
            return inDB;
        } catch (SQLException e){
            System.out.println("Error in query");
        }
        return false;
    }
    @Override
    public void doActivity(){
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
        System.out.println(id);
    }

    private void viewMemberProfile() {
        // Prompt user for member's first name
        System.out.print("Enter member's first name: ");
        String firstName = scanner.nextLine();

        // Prompt user for member's last name
        System.out.print("Enter member's last name: ");
        String lastName = scanner.nextLine();

        // SQL query to search for member in MemberData table
        String sqlSelect = "SELECT memberId, firstName, lastName, height, weight, avgHeartRate, bloodPressure FROM MemberData WHERE firstName = ? AND lastName = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
            // Set first name parameter
            preparedStatement.setString(1, firstName);
            // Set last name parameter
            preparedStatement.setString(2, lastName);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Member found, display the profile as one row
                System.out.println("Member Profile:");
                System.out.println("Member ID: " + resultSet.getInt("memberId") +
                        ", First Name: " + resultSet.getString("firstName") +
                        ", Last Name: " + resultSet.getString("lastName") +
                        ", Height: " + resultSet.getObject("height") +
                        ", Weight: " + resultSet.getObject("weight") +
                        ", Average Heart Rate: " + resultSet.getObject("avgHeartRate") +
                        ", Blood Pressure: " + resultSet.getObject("bloodPressure"));
            } else {
                // Member not found
                System.out.println("No member with the given first and last name exists in the database.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }
}
