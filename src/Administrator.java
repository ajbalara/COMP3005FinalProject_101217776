import java.sql.*;
import java.util.Scanner;

public class Administrator extends User{
    public Administrator(Scanner scanner, Connection conn){
        super(scanner, conn);
        activities.add("Room Booking Management");
        activities.add("Equipment Maintenance Monitoring");
        activities.add("Class Schedule Updating");
        activities.add("Billing and Payment Processing");
    }

    @Override
    public void setUp(){}
    @Override
    public void doActivity(){
        switch (nextActivityIndex){
            case 0:
                manageRoomBooking();
                break;
            case 1:
                monitorEquipment();
                break;
            case 2:
                updateClassSchedule();
                break;
            case 3:
                manageBilling();
                break;
        }
    }

    private void manageRoomBooking() {
        boolean exit = false;

        while (!exit) {
            // Display room numbers
            System.out.println("Room Numbers:");
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT roomNumber FROM RoomData");
                while (rs.next()) {
                    System.out.println(rs.getInt("roomNumber"));
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
                return;
            }
            // Prompt user to select a room
            System.out.print("Select a room number (enter 0 to exit): ");
            int roomNumber;
            try {
                roomNumber = Integer.parseInt(scanner.nextLine());
                if (roomNumber == 0) {
                    System.out.println("Exiting room booking management.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            // Display room time slots
            String sqlSelect = "SELECT * FROM RoomTimeSlots WHERE roomNumber = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
                preparedStatement.setInt(1, roomNumber);
                ResultSet resultSet = preparedStatement.executeQuery();

                System.out.println("Room Time Slots:");
                int slotNumber = 1;
                while (resultSet.next()) {
                    System.out.println(slotNumber + ". Day: " + resultSet.getInt("day") +
                            ", Week: " + resultSet.getInt("week") +
                            ", Available: " + resultSet.getBoolean("isAvailable"));
                    slotNumber++;
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
                return;
            }

            // Prompt user to select a time slot to toggle availability
            int selectedSlot;
            try {
                System.out.print("Select a time slot to toggle availability (enter 0 to go back to room selection): ");
                selectedSlot = Integer.parseInt(scanner.nextLine());
                if (selectedSlot == 0) {
                    System.out.println("Going back to room selection.");
                    continue; // Exit the current loop and go back to displaying room numbers
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            // Update isAvailable field in RoomTimeSlots table
            String sqlUpdate = "UPDATE RoomTimeSlots SET isAvailable = NOT isAvailable WHERE roomNumber = ? AND day = ? AND week = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlUpdate)) {
                preparedStatement.setInt(1, roomNumber);
                preparedStatement.setInt(2, selectedSlot);
                // For simplicity, let's assume week is always 1 (change it as per your data model)
                preparedStatement.setInt(3, 1);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Time slot availability updated successfully.");
                } else {
                    System.out.println("Failed to update time slot availability.");
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
        }
    }

    private void monitorEquipment() {
        // SQL query to retrieve equipment information
        String sqlSelect = "SELECT equipmentId, name, condition FROM Equipment";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
            // Execute the query to get equipment data
            ResultSet resultSet = preparedStatement.executeQuery();

            // Display equipment information
            System.out.println("Equipment Information:");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("equipmentId") +
                        ", Name: " + resultSet.getString("name") +
                        ", Condition: " + resultSet.getString("condition"));
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    private void updateClassSchedule() {
        boolean exit = false;

        while (!exit) {
            // Display all group classes
            System.out.println("Group Classes:");
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM GroupClasses");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("classId") +
                            ", Name: " + rs.getString("name") +
                            ", Day: " + rs.getInt("day") +
                            ", Week: " + rs.getInt("week"));
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
                return;
            }

            // Prompt user for action
            System.out.println("Select an action:");
            System.out.println("1. Add a class");
            System.out.println("2. Remove a class");
            System.out.println("3. Update a class");
            System.out.print("Enter the number corresponding to the action: ");
            int actionChoice;
            try {
                actionChoice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (actionChoice) {
                case 1:
                    addClass();
                    break;
                case 2:
                    removeClass();
                    break;
                case 3:
                    updateExistingClass();
                    break;
                default:
                    System.out.println("Invalid action choice.");
                    break;
            }
        }
    }

    private void addClass() {
        try {
            // Prompt user for class details
            System.out.print("Enter class name: ");
            String className = scanner.nextLine();
            System.out.print("Enter day (1-7): ");
            int day = Integer.parseInt(scanner.nextLine());
            if (day < 1 || day > 7) {
                System.out.println("Invalid input. Day must be between 1 and 7.");
                return;
            }
            System.out.print("Enter week: ");
            int week = Integer.parseInt(scanner.nextLine());
            if (week < 1) {
                System.out.println("Invalid input. Week must be a positive integer.");
                return;
            }

            // Insert new class into the database
            String sqlInsert = "INSERT INTO GroupClasses (name, day, week) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlInsert)) {
                preparedStatement.setString(1, className);
                preparedStatement.setInt(2, day);
                preparedStatement.setInt(3, week);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Class added successfully.");
                } else {
                    System.out.println("Failed to add class.");
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number for day and week.");
        }
    }

    private void removeClass() {
        try {
            // Prompt user for class ID to remove
            System.out.print("Enter class ID to remove: ");
            int classId = Integer.parseInt(scanner.nextLine());

            // Delete class from the database
            String sqlDelete = "DELETE FROM GroupClasses WHERE classId = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlDelete)) {
                preparedStatement.setInt(1, classId);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Class removed successfully.");
                } else {
                    System.out.println("Failed to remove class. Class ID not found.");
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number for class ID.");
        }
    }

    private void updateExistingClass() {
        try {
            // Prompt user for class ID to update
            System.out.print("Enter class ID to update: ");
            int classId = Integer.parseInt(scanner.nextLine());

            // Prompt user for the field to update
            System.out.println("Select a field to update:");
            System.out.println("1. Name");
            System.out.println("2. Day");
            System.out.println("3. Week");
            System.out.print("Enter the number corresponding to the field: ");
            int fieldChoice = Integer.parseInt(scanner.nextLine());

            String fieldName;
            switch (fieldChoice) {
                case 1:
                    fieldName = "name";
                    break;
                case 2:
                    fieldName = "day";
                    break;
                case 3:
                    fieldName = "week";
                    break;
                default:
                    System.out.println("Invalid field choice.");
                    return;
            }

            // Prompt user for new value
            System.out.print("Enter new value: ");
            String newValue = scanner.nextLine();

            // Update class in the database
            String sqlUpdate = "UPDATE GroupClasses SET " + fieldName + " = ? WHERE classId = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlUpdate)) {
                preparedStatement.setString(1, newValue);
                preparedStatement.setInt(2, classId);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Class " + fieldName + " updated successfully.");
                } else {
                    System.out.println("Failed to update class " + fieldName + ". Class ID not found.");
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number for class ID, field choice, and new value.");
        }
    }

    private void manageBilling() {
        boolean exit = false;

        while (!exit) {
            System.out.println("Billing Management Menu:");
            System.out.println("1. Process Payment");
            System.out.println("2. Add Money to Member's Dollars Owing");

            System.out.print("Enter your choice, or enter 0 to exit: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 0:
                    System.out.println("Exiting billing management.");
                    exit = true;
                    break;
                case 1:
                    processPayment();
                    break;
                case 2:
                    addMoneyToMemberDollarsOwing();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 2.");
                    break;
            }
        }
    }

    private void processPayment() {
        // Display all members with their IDs, first names, last names, and dollarsOwing
        System.out.println("Member Information:");
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT memberId, firstName, lastName, dollarsOwing FROM MemberData");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("memberId") +
                        ", Name: " + rs.getString("firstName") + " " + rs.getString("lastName") +
                        ", Dollars Owing: " + rs.getInt("dollarsOwing"));
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return;
        }

        // Prompt user for member ID
        System.out.print("Enter member ID who made the payment: ");
        int memberId = Integer.parseInt(scanner.nextLine());

        // Prompt user for payment amount
        System.out.print("Enter payment amount: ");
        int paymentAmount = Integer.parseInt(scanner.nextLine());

        // Retrieve current dollarsOwing value
        int currentDollarsOwing = 0;
        String sqlSelect = "SELECT dollarsOwing FROM MemberData WHERE memberId = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentDollarsOwing = resultSet.getInt("dollarsOwing");
            } else {
                System.out.println("Member with ID " + memberId + " not found.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return;
        }

        // Calculate new dollarsOwing value
        int newDollarsOwing = currentDollarsOwing - paymentAmount;

        // Update dollarsOwing value in the database
        String sqlUpdate = "UPDATE MemberData SET dollarsOwing = ? WHERE memberId = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlUpdate)) {
            preparedStatement.setInt(1, newDollarsOwing);
            preparedStatement.setInt(2, memberId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment processed successfully.");
            } else {
                System.out.println("Failed to process payment. Member ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    private void addMoneyToMemberDollarsOwing() {
        // Display all members with their IDs, first names, last names, and dollarsOwing
        System.out.println("Member Information:");
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT memberId, firstName, lastName, dollarsOwing FROM MemberData");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("memberId") +
                        ", Name: " + rs.getString("firstName") + " " + rs.getString("lastName") +
                        ", Dollars Owing: " + rs.getInt("dollarsOwing"));
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return;
        }

        // Prompt user for member ID
        System.out.print("Enter member ID to add money to their dollars owing: ");
        int memberId = Integer.parseInt(scanner.nextLine());

        // Prompt user for amount to add
        System.out.print("Enter amount to add: ");
        int amountToAdd = Integer.parseInt(scanner.nextLine());

        // Retrieve current dollarsOwing value
        int currentDollarsOwing = 0;
        String sqlSelect = "SELECT dollarsOwing FROM MemberData WHERE memberId = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentDollarsOwing = resultSet.getInt("dollarsOwing");
            } else {
                System.out.println("Member with ID " + memberId + " not found.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return;
        }

        // Calculate new dollarsOwing value
        int newDollarsOwing = currentDollarsOwing + amountToAdd;

        // Update dollarsOwing value in the database
        String sqlUpdate = "UPDATE MemberData SET dollarsOwing = ? WHERE memberId = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlUpdate)) {
            preparedStatement.setInt(1, newDollarsOwing);
            preparedStatement.setInt(2, memberId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Money added to member's dollars owing successfully.");
            } else {
                System.out.println("Failed to add money to member's dollars owing. Member ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }
}
