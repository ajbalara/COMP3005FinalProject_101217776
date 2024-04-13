import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
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
        try {
            // SQL query to retrieve TrainerTimeSlots for a given trainer ID
            String sqlSelect = "SELECT * FROM TrainerTimeSlots WHERE trainerId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect);
            preparedStatement.setInt(1, id);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Display TrainerTimeSlots
            System.out.println("Trainer Time Slots:");
            while (resultSet.next()) {
                int day = resultSet.getInt("day");
                int week = resultSet.getInt("week");
                boolean isAvailable = resultSet.getBoolean("isAvailable");

                System.out.println("Day: " + day + ", Week: " + week + ", Available: " + isAvailable);
            }

            // Prompt user for action
            System.out.println("\nOptions:");
            System.out.println("1. Remove a timeslot");
            System.out.println("2. Add a timeslot");

            System.out.print("Enter your choice, or enter 0: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    removeTimeSlot();
                    break;
                case 2:
                    addTimeSlot();
                    break;
                case 0:
                    System.out.println("Exiting manage schedule.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 2.");
                    break;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    private void removeTimeSlot() throws SQLException {
        System.out.print("Enter day of timeslot to remove: ");
        int day = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter week of timeslot to remove: ");
        int week = Integer.parseInt(scanner.nextLine());

        // Check if timeslot exists for the trainer
        String checkSql = "SELECT * FROM TrainerTimeSlots WHERE trainerId = ? AND day = ? AND week = ?";
        PreparedStatement checkStatement = conn.prepareStatement(checkSql);
        checkStatement.setInt(1, id);
        checkStatement.setInt(2, day);
        checkStatement.setInt(3, week);
        ResultSet checkResult = checkStatement.executeQuery();

        if (checkResult.next()) {
            // Timeslot exists, check if it's available
            boolean isAvailable = checkResult.getBoolean("isAvailable");
            if (!isAvailable) {
                System.out.println("Cannot remove unavailable timeslot.");
            } else {
                // Delete the timeslot
                String deleteSql = "DELETE FROM TrainerTimeSlots WHERE trainerId = ? AND day = ? AND week = ?";
                PreparedStatement deleteStatement = conn.prepareStatement(deleteSql);
                deleteStatement.setInt(1, id);
                deleteStatement.setInt(2, day);
                deleteStatement.setInt(3, week);
                int rowsDeleted = deleteStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Timeslot removed successfully.");
                } else {
                    System.out.println("Failed to remove timeslot.");
                }
            }
        } else {
            System.out.println("Timeslot does not exist for this trainer.");
        }
    }



    private void addTimeSlot() throws SQLException {
        System.out.print("Enter day of new timeslot: ");
        int day = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter week of new timeslot: ");
        int week = Integer.parseInt(scanner.nextLine());

        // Check if timeslot already exists for the trainer
        String checkSql = "SELECT * FROM TrainerTimeSlots WHERE trainerId = ? AND day = ? AND week = ?";
        PreparedStatement checkStatement = conn.prepareStatement(checkSql);
        checkStatement.setInt(1, id);
        checkStatement.setInt(2, day);
        checkStatement.setInt(3, week);
        ResultSet checkResult = checkStatement.executeQuery();

        if (checkResult.next()) {
            System.out.println("Timeslot already exists for this trainer.");
        } else {
            // Insert new timeslot
            String insertSql = "INSERT INTO TrainerTimeSlots (trainerId, day, week, isAvailable) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = conn.prepareStatement(insertSql);
            insertStatement.setInt(1, id);
            insertStatement.setInt(2, day);
            insertStatement.setInt(3, week);
            insertStatement.setBoolean(4, true);
            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Timeslot added successfully.");
            } else {
                System.out.println("Failed to add timeslot.");
            }
        }
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
