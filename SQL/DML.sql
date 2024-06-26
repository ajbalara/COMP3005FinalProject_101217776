-- Inserting data into MemberData table
INSERT INTO MemberData (firstName, lastName, height, weight, avgHeartRate, bloodPressure, dollarsOwing) 
VALUES 
    ('John', 'Doe', 175.5, 70, 72, 120, 0),
    ('Jane', 'Smith', 160.2, 55, 65, 110, 25),
    ('Alice', 'Johnson', 180, 85, 80, 130, 10),
    ('Bob', 'Williams', 168.8, 75, 70, 125, 0);

-- Inserting data into TrainerData table
INSERT INTO TrainerData (firstName, lastName, specialty) 
VALUES 
    ('Michael', 'Brown', 'Weightlifting'),
    ('Emily', 'Davis', 'Boxing'),
    ('Chris', 'Wilson', 'Cardio'),
    ('Sarah', 'Martinez', 'Pilates');

-- Inserting data into RoomData table
INSERT INTO RoomData (capacity)
VALUES 
    (20),
    (15),
    (30);

-- Inserting data into TrainerTimeSlots table
INSERT INTO TrainerTimeSlots (trainerId, day, week, isAvailable) 
VALUES 
    (1, 1, 1, true),
    (2, 2, 1, true),
    (3, 3, 1, true),
    (4, 4, 1, true);

-- Inserting data into RoomTimeSlots table
INSERT INTO RoomTimeSlots (roomNumber, day, week, isAvailable) 
VALUES 
    (1, 1, 1, true),
    (1, 2, 1, true),
    (1, 3, 1, true),
    (1, 4, 1, false),
    (1, 5, 1, true),
    (1, 6, 1, true),
    (1, 7, 1, true),
    (2, 2, 1, true),
    (3, 2, 1, true),
    (3, 3, 1, true),
    (3, 4, 1, true),
    (3, 5, 1, true);

-- Inserting data into Equipment table
INSERT INTO Equipment (name, condition, isWeight) 
VALUES 
    ('Treadmill', 'brand new', false),
    ('Bench Press', 'used', true),
    ('Chest Fly', 'brand new', true),
    ('Stationary Bike', 'brand new', false);

-- Inserting data into Exercises table
INSERT INTO Exercises (memberId, equipmentId, target, actual) 
VALUES 
    (1, 1, 30, 25),
    (2, 2, 100, 70),
    (3, 3, 75, 50),
    (4, 4, 25, 20);

-- Inserting data into GroupClasses table
INSERT INTO GroupClasses (name, day, week) 
VALUES 
    ('Boxing Class', 1, 1),
    ('Pilates Class', 2, 1),
    ('Zumba Class', 3, 1),
    ('Cardio Class', 4, 1);