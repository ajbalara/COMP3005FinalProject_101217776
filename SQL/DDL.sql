CREATE TABLE MemberData (
	memberId SERIAL PRIMARY KEY,
    firstName VARCHAR(20) NOT NULL,
    lastName VARCHAR(20) NOT NULL,
    height NUMERIC,
	weight INT,
	avgHeartRate INT,
	bloodPressure INT,
	dollarsOwing INT
);

CREATE TABLE TrainerData (
	trainerId SERIAL PRIMARY KEY,
	firstName VARCHAR(20) NOT NULL,
    	lastName VARCHAR(20) NOT NULL,
	specialty TEXT
);

CREATE TABLE RoomData (
	roomNumber SERIAL PRIMARY KEY,
	capacity INT
);

CREATE TABLE MemberTimeSlots (
	memberId INT,
	day INT,
	week INT,
	isAvailable BOOLEAN,
	PRIMARY KEY(memberId, day, week),
	FOREIGN KEY (memberId)
		REFERENCES MemberData(memberId)
);

CREATE TABLE TrainerTimeSlots (
	trainerId INT,
	day INT,
	week INT,
	isAvailable BOOLEAN,
	PRIMARY KEY(trainerId, day, week),
	FOREIGN KEY (trainerId)
		REFERENCES TrainerData(trainerId)
);

CREATE TABLE RoomTimeSlots (
	roomNumber INT,
	day INT,
	week INT,
	isAvailable BOOLEAN,
	PRIMARY KEY(roomNumber, day, week),
	FOREIGN KEY (roomNumber)
		REFERENCES RoomData(roomNumber)
);

CREATE TABLE Equipment (
	equipmentId SERIAL PRIMARY KEY,
	name VARCHAR(40) NOT NULL,
	condition VARCHAR(20) NOT NULL DEFAULT 'brand new',
	isWeight BOOLEAN
);

CREATE TABLE Exercises (
    memberId INT,
	equipmentId INT,
	target NUMERIC,
	actual NUMERIC,
	PRIMARY KEY (memberId, equipmentId),
	FOREIGN KEY (memberId)
		REFERENCES MemberData(memberId),
	FOREIGN KEY (equipmentId)
		REFERENCES Equipment(equipmentId)
);

CREATE TABLE GroupClasses (
    classId SERIAL PRIMARY KEY,
	name VARCHAR(40),
	day INT,
	week INT
);

CREATE TABLE ClassRegistered (
	memberId INT,
	classId INT,
	PRIMARY KEY (memberId, classId),
	FOREIGN KEY (memberId)
		REFERENCES MemberData(memberId),
	FOREIGN KEY (classId)
		REFERENCES GroupClasses(classId)
);