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

        int dollarsOwing = ManagementSystem.REGISTRATION_FEE + ManagementSystem.MEMBERSHIP_FEE;

        String sqlInsert = "INSERT INTO MemberData (firstName, lastName, height, weight, avgHeartRate, bloodPressure, dollarsOwing) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setObject(3, null);
            preparedStatement.setObject(4, null);
            preparedStatement.setObject(5, null);
            preparedStatement.setObject(6, null);
            preparedStatement.setInt(7, dollarsOwing);

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
            String sqlSelect = "SELECT * FROM MemberData WHERE memberId = ?";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
                preparedStatement.setInt(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();
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
        String sqlUpdate = "UPDATE MemberData SET " + fieldName + " = ? WHERE memberId = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlUpdate)) {
            preparedStatement.setObject(1, newValue);
            preparedStatement.setInt(2, id);

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
        String sqlSelect = "SELECT e.name AS equipment_name, ex.actual AS exercise_actual, ex.target AS exercise_target, e.isWeight " +
                "FROM Exercises ex " +
                "INNER JOIN Equipment e ON ex.equipmentId = e.equipmentId " +
                "WHERE ex.memberId = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Flag to check if the dashboard is empty
            boolean isEmpty = true;

            System.out.println("Dashboard for Member ID " + id + ":");
            while (resultSet.next()) {
                isEmpty = false;

                String equipmentName = resultSet.getString("equipment_name");
                double exerciseActual = resultSet.getDouble("exercise_actual");
                double exerciseTarget = resultSet.getDouble("exercise_target");
                boolean isWeight = resultSet.getBoolean("isWeight");

                System.out.println("Equipment Name: " + equipmentName +
                        ", Exercise Actual: " + exerciseActual +
                        (isWeight ? " lbs" : " minutes") +
                        ", Exercise Target: " + exerciseTarget +
                        (isWeight ? " lbs" : " minutes"));
            }

            // If dashboard is empty, print message
            if (isEmpty) {
                System.out.println("Dashboard is empty!");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    private void manageSchedule() {
        try {
            System.out.println("Your Time Slots:");
            displayMemberTimeSlots();

            System.out.println("\nChoose which schedule you want to view:");
            System.out.println("1. Trainer Schedule");
            System.out.println("2. Group Class Schedule");
            System.out.print("Enter your choice, or enter 0 to exit: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewTrainerSchedule();
                    break;
                case 2:
                    viewGroupClassSchedule();
                    break;
                case 0:
                    System.out.println("Exiting manage schedule.");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 2.");
                    break;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    private void displayMemberTimeSlots() throws SQLException {
        String sqlSelect = "SELECT * FROM MemberTimeSlots WHERE memberId = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect);
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int day = resultSet.getInt("day");
            int week = resultSet.getInt("week");
            boolean isAvailable = resultSet.getBoolean("isAvailable");
            System.out.println("Day: " + day + ", Week: " + week + ", Available: " + isAvailable);
        }
    }

    private void viewTrainerSchedule() throws SQLException {
        System.out.println("Trainer Schedule:");
        String sqlSelect = "SELECT TrainerTimeSlots.*, TrainerData.trainerId, TrainerData.firstName, " +
                "TrainerData.lastName, TrainerData.specialty " +
                "FROM TrainerTimeSlots " +
                "INNER JOIN TrainerData ON TrainerTimeSlots.trainerId = TrainerData.trainerId " +
                "WHERE TrainerTimeSlots.isAvailable = true";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int trainerId = resultSet.getInt("trainerId");
            int day = resultSet.getInt("day");
            int week = resultSet.getInt("week");
            String firstName = resultSet.getString("firstName");
            String lastName = resultSet.getString("lastName");
            String specialty = resultSet.getString("specialty");
            System.out.println("Trainer ID: " + trainerId + ", Name: " + firstName + " " + lastName +
                    ", Specialty: " + specialty + ", Day: " + day + ", Week: " + week);
        }

        System.out.print("\nEnter the Trainer ID of the time slot you want to book (enter 0 to cancel): ");
        int selectedTrainerId = Integer.parseInt(scanner.nextLine());
        if (selectedTrainerId == 0) {
            System.out.println("Booking cancelled.");
            return;
        }

        System.out.print("Enter the day of the time slot: ");
        int selectedDay = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter the week of the time slot: ");
        int selectedWeek = Integer.parseInt(scanner.nextLine());

        boolean isAvailable = checkTrainerAvailability(selectedTrainerId, selectedDay, selectedWeek);
        if (!isAvailable) {
            System.out.println("The selected time slot is not available.");
            return;
        }

        String sqlInsert = "INSERT INTO MemberTimeSlots (memberId, day, week, isAvailable) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = conn.prepareStatement(sqlInsert);
        insertStatement.setInt(1, id);
        insertStatement.setInt(2, selectedDay);
        insertStatement.setInt(3, selectedWeek);
        insertStatement.setBoolean(4, false);
        insertStatement.executeUpdate();

        // Update the corresponding TrainerTimeSlots entry
        String updateSql = "UPDATE TrainerTimeSlots SET isAvailable = false WHERE trainerId = ? AND day = ? AND week = ?";
        PreparedStatement updateStatement = conn.prepareStatement(updateSql);
        updateStatement.setInt(1, selectedTrainerId);
        updateStatement.setInt(2, selectedDay);
        updateStatement.setInt(3, selectedWeek);
        updateStatement.executeUpdate();

        System.out.println("Time slot booked successfully.");
    }

    private boolean checkTrainerAvailability(int trainerId, int day, int week) throws SQLException {
        String sqlSelect = "SELECT * FROM TrainerTimeSlots " +
                "WHERE trainerId = ? AND day = ? AND week = ? AND isAvailable = true";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect);
        preparedStatement.setInt(1, trainerId);
        preparedStatement.setInt(2, day);
        preparedStatement.setInt(3, week);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    private void viewGroupClassSchedule() throws SQLException {
        System.out.println("Group Class Schedule:");
        String sqlSelect = "SELECT * FROM GroupClasses";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int classId = resultSet.getInt("classId");
            String className = resultSet.getString("name");
            int day = resultSet.getInt("day");
            int week = resultSet.getInt("week");
            System.out.println("Class ID: " + classId + ", Name: " + className + ", Day: " + day + ", Week: " + week);
        }

        System.out.print("\nEnter the Class ID of the class you want to register (enter 0 to cancel): ");
        int selectedClassId = Integer.parseInt(scanner.nextLine());
        if (selectedClassId == 0) {
            System.out.println("Registration cancelled.");
            return;
        }

        // Check if the member has an available time slot for the selected class
        boolean isAvailable = checkMemberAvailability(selectedClassId);
        if (!isAvailable) {
            System.out.println("You don't have an available time slot for the selected class.");
            return;
        }

        // Create a new entry in MemberTimeSlots for the member
        String sqlInsert = "INSERT INTO MemberTimeSlots (memberId, day, week, isAvailable) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = conn.prepareStatement(sqlInsert);
        insertStatement.setInt(1, id);
        insertStatement.setInt(2, resultSet.getInt("day"));
        insertStatement.setInt(3, resultSet.getInt("week"));
        insertStatement.setBoolean(4, false);
        insertStatement.executeUpdate();

        // Add an entry to the ClassRegistered table
        String insertClassRegistered = "INSERT INTO ClassRegistered (memberId, classId) VALUES (?, ?)";
        PreparedStatement insertClassRegisteredStatement = conn.prepareStatement(insertClassRegistered);
        insertClassRegisteredStatement.setInt(1, id);
        insertClassRegisteredStatement.setInt(2, selectedClassId);
        insertClassRegisteredStatement.executeUpdate();

        System.out.println("Class registered successfully.");
    }

    private boolean checkMemberAvailability(int classId) throws SQLException {
        String sqlSelect = "SELECT * FROM MemberTimeSlots " +
                "WHERE memberId = ? AND day = (SELECT day FROM GroupClasses WHERE classId = ?) " +
                "AND week = (SELECT week FROM GroupClasses WHERE classId = ?) " +
                "AND isAvailable = true";
        PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect);
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, classId);
        preparedStatement.setInt(3, classId);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
}
