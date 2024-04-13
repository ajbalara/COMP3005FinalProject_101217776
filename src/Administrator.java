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
                processPayment();
                break;
        }
    }

    private void manageRoomBooking() {
        boolean exit = false;
        boolean firstIteration = true;

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

            if(firstIteration) {
                // Consume newline character from previous input
                scanner.nextLine();
                firstIteration = false;
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
    }

    private void processPayment() {
    }
}
