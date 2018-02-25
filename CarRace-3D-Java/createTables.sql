DROP DATABASE IF EXISTS yurivitalimvc;
CREATE DATABASE yurivitalimvc;
USE yurivitalimvc;
/* Table Creation */
DROP TABLE IF EXISTS Person;
CREATE TABLE Person(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	password VARCHAR(50) NOT NULL,
	balance DOUBLE(30, 15) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY (name, password)
);
DROP TABLE IF EXISTS Car;
CREATE TABLE Car(
	id INT NOT NULL AUTO_INCREMENT,
	carManufacture VARCHAR(50) NOT NULL,
	color VARCHAR(50) NOT NULL,
	carSize VARCHAR(50) NOT NULL,
	carShape VARCHAR(20) NOT NULL,
	PRIMARY KEY (id)
);
DROP TABLE IF EXISTS Race;
CREATE TABLE Race(
	id INT NOT NULL AUTO_INCREMENT,
	raceCreateDate DATETIME NOT NULL,
	raceStartDate DATETIME,
	PRIMARY KEY (id)
);
DROP TABLE IF EXISTS CarRace;
CREATE TABLE CarRace (
	carId INT NOT NULL,
	raceId INT NOT NULL,
	PRIMARY KEY (raceId, carId),
	INDEX (raceId),
	FOREIGN KEY (raceId) REFERENCES Race(id),
	FOREIGN KEY (carId) REFERENCES Car(id)
);
DROP TABLE IF EXISTS Bid;
CREATE TABLE Bid(
	id INT NOT NULL AUTO_INCREMENT,
	raceId INT NOT NULL,
	personId INT NOT NULL,
	carId INT NOT NULL,
	bidAmount DOUBLE(30, 15) NOT NULL,
	afterTax DOUBLE(30, 15) NOT NULL,
	profit DOUBLE(30, 15) NOT NULL DEFAULT 0,
	PRIMARY KEY (id),
	FOREIGN KEY (raceId) REFERENCES Race(id),
	FOREIGN KEY (personId) REFERENCES Person(id),
	FOREIGN KEY (carId) REFERENCES Car(id),
	INDEX (raceId),
	INDEX(personId),
	INDEX(carId)
);
DROP TABLE IF EXISTS Stats;
CREATE TABLE Stats(
	id INT NOT NULL AUTO_INCREMENT,
	raceId INT NOT NULL,
	winnerCarId INT NOT NULL,
	distance INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (raceId) REFERENCES Race(id),
	FOREIGN KEY (winnerCarId) REFERENCES Car(id),
	INDEX(raceId)
);
/* Predefined data */
/* System user */
INSERT INTO Person(name, password, balance) VALUES ('SYSTEM', 'USER', 0);
/* Cars */
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('BMW', 'ff0000', 'regular', 'sport');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Audi', '0000ff', 'regular', 'cabriolet');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Mercedes', '009900', 'regular', 'salon');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Seat', 'ffff00', 'mini', 'salon');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Audi', 'ff0000', 'mini', 'sport');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('BMW', '993399', 'regular', 'cabriolet');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('BMW', 'ff0000', 'mini', 'salon');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Audi', '0000ff', 'large', 'salon');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Mercedes', '009900', 'mini', 'sport');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Seat', 'ffff00', 'regular', 'cabriolet');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('Audi', 'ff0000', 'mini', 'salon');
INSERT INTO Car(carManufacture, color, carSize, carShape) VALUES('BMW', '993399', 'large', 'cabriolet');
/* Race */
INSERT INTO Race(raceCreateDate) VALUES('2017-03-09 23:59:59');
INSERT INTO Race(raceCreateDate) VALUES('2017-02-09 23:59:59');
INSERT INTO Race(raceCreateDate) VALUES('2017-03-09 21:59:59');
/* CarRace Connection */
/* Race 1 */
INSERT INTO CarRace(carId, raceId) VALUES(1,1);
INSERT INTO CarRace(carId, raceId) VALUES(3,1);
INSERT INTO CarRace(carId, raceId) VALUES(4,1);
INSERT INTO CarRace(carId, raceId) VALUES(2,1);
INSERT INTO CarRace(carId, raceId) VALUES(5,1);
/* Race 2 */
INSERT INTO CarRace(carId, raceId) VALUES(4,2);
INSERT INTO CarRace(carId, raceId) VALUES(5,2);
INSERT INTO CarRace(carId, raceId) VALUES(8,2);
INSERT INTO CarRace(carId, raceId) VALUES(9,2);
INSERT INTO CarRace(carId, raceId) VALUES(10,2);
/* Race 3 */
INSERT INTO CarRace(carId, raceId) VALUES(5,3);
INSERT INTO CarRace(carId, raceId) VALUES(11,3);
INSERT INTO CarRace(carId, raceId) VALUES(12,3);
INSERT INTO CarRace(carId, raceId) VALUES(2,3);
INSERT INTO CarRace(carId, raceId) VALUES(7,3);
COMMIT;