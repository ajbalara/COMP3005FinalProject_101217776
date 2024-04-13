import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Member extends User{
    private int id;

    public Member(Scanner scanner, Connection conn){
        super(scanner, conn);
        activities.add("Profile Management");
        activities.add("Dashboard Display");
        activities.add("Schedule Management");
    }

    @Override
    public void setUp() {
        int input;
        while (true) {
            try {
                System.out.print("Enter your member ID: ");
                input = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                if (input <= 0) {
                    throw new InputMismatchException();
                }
                boolean memberSet = false;
                if (!memberInDB(input)) {
                    System.out.print("Member ID not found in DB. Do you want to register a new member (y/n)? Note that you may not get the member ID entered: ");
                    String register;
                    while (true) {
                        register = scanner.nextLine().toLowerCase();
                        if (register.equals("y")) {
                            createNewMember();
                            memberSet = true;
                            break; // Exit the inner loop after creating a new member
                        } else if (register.equals("n")) {
                            break; // Exit the inner loop if not registering a new member
                        } else {
                            System.out.println("Please confirm that you want to register a new member (y/n)");
                        }
                    }
                } else {
                    memberSet = true;
                    System.out.println("Member Found in DB");
                }
                if(memberSet){
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a positive integer for member ID.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }

    private boolean memberInDB(int inputId){
        try{
            Statement stmt = conn.createStatement();
            String SQL = "SELECT memberId FROM MemberData";
            ResultSet rs = stmt.executeQuery(SQL); // Process the result set
            ArrayList<Integer> memberIds = new ArrayList<>();
            while(rs.next()){
                memberIds.add(rs.getInt("memberId"));
            }
            // Close resources
            rs.close();
            stmt.close();
            boolean inDB = memberIds.contains(inputId);
            id = inputId;
            return inDB;
        } catch (SQLException e){
            System.out.println("Error in query");
        }
        return false;
    }

    private void createNewMember(){
        // Prompt the user for firstName and lastName
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        // Default values
        Double height = null;
        Integer weight = null;
        Integer avgHeartRate = null;
        Integer bloodPressure = null;
        Integer dollarsOwing = ManagementSystem.REGISTRATION_FEE + ManagementSystem.MEMBERSHIP_FEE;

        // SQL query to insert a new member with default values
        String sqlInsert = "INSERT INTO MemberData (firstName, lastName, height, weight, avgHeartRate, bloodPressure, dollarsOwing) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            // Set parameter values
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setObject(3, height);
            preparedStatement.setObject(4, weight);
            preparedStatement.setObject(5, avgHeartRate);
            preparedStatement.setObject(6, bloodPressure);
            preparedStatement.setInt(7, dollarsOwing);

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve the memberId of the newly inserted row
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                    System.out.println("A new member has been inserted successfully with memberId: " + id);
                } else {
                    System.out.println("Failed to retrieve the memberId of the newly inserted row.");
                }
            } else {
                System.out.println("Failed to insert a new member.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            System.exit(1);
        }
    }


    @Override
    public void doActivity(){
        scanner.nextLine();
        switch (nextActivityIndex){
            case 0:
                manageProfile();
                break;
            case 1:
                displayDashboard();
                break;
            case 2:
                manageSchedule();
                break;
        }
    }

    private void manageProfile() {
        boolean exit = false;

        while (!exit) {
            // SQL query to select member data based on memberId
            String sqlSelect = "SELECT * FROM MemberData WHERE memberId = ?";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
                // Set memberId parameter
                preparedStatement.setInt(1, id);

                // Execute the query to get member data
                ResultSet resultSet = preparedStatement.executeQuery();

                // Display member profile
                if (resultSet.next()) {
                    System.out.println("Member Profile:");
                    System.out.println("1. First Name: " + resultSet.getString("firstName"));
                    System.out.println("2. Last Name: " + resultSet.getString("lastName"));
                    System.out.println("3. Height: " + resultSet.getObject("height"));
                    System.out.println("4. Weight: " + resultSet.getObject("weight"));
                    System.out.println("5. Average Heart Rate: " + resultSet.getObject("avgHeartRate"));
                    System.out.println("6. Blood Pressure: " + resultSet.getObject("bloodPressure"));
                    System.out.println("7. Dollars Owing: " + resultSet.getInt("dollarsOwing"));
                } else {
                    System.out.println("Member with ID " + id + " not found.");
                    return;
                }

                // Prompt user for field to change
                System.out.println("\nSelect a field to change (1-7), or enter 0 to exit:");
                String choiceStr = scanner.nextLine();

                if (choiceStr.equals("0")) {
                    System.out.println("Exiting profile management.");
                    exit = true;
                    continue;
                }

                int choice;
                try {
                    choice = Integer.parseInt(choiceStr);
                    if (choice < 1 || choice > 7) {
                        throw new NumberFormatException("Invalid choice. Please enter a number between 1 and 7.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                    continue;
                }

                // Process user choice
                switch (choice) {
                    case 1:
                        System.out.print("Enter new first name: ");
                        String newFirstName = scanner.nextLine();
                        updateField("firstName", newFirstName);
                        break;
                    case 2:
                        System.out.print("Enter new last name: ");
                        String newLastName = scanner.nextLine();
                        updateField("lastName", newLastName);
                        break;
                    case 3:
                        int newHeight = readIntInput("Enter new height: ");
                        updateField("height", newHeight);
                        break;
                    case 4:
                        int newWeight = readIntInput("Enter new weight: ");
                        updateField("weight", newWeight);
                        break;
                    case 5:
                        int newAvgHeartRate = readIntInput("Enter new average heart rate: ");
                        updateField("avgHeartRate", newAvgHeartRate);
                        break;
                    case 6:
                        int newBloodPressure = readIntInput("Enter new blood pressure: ");
                        updateField("bloodPressure", newBloodPressure);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }

            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
        }
    }

    private int readIntInput(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }



    private void updateField(String fieldName, Object newValue) {
        // SQL query to update member data
        String sqlUpdate = "UPDATE MemberData SET " + fieldName + " = ? WHERE memberId = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlUpdate)) {
            // Set new value parameter
            preparedStatement.setObject(1, newValue);
            // Set memberId parameter
            preparedStatement.setInt(2, id);

            // Execute the update statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Field " + fieldName + " updated successfully.");
            } else {
                System.out.println("Failed to update field " + fieldName + ".");
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }


    private void displayDashboard() {
    }

    private void manageSchedule() {
    }
}
