DROP TABLE IF EXISTS SpaceSystem CASCADE;
DROP TABLE IF EXISTS SpaceObject CASCADE;
DROP TABLE IF EXISTS Ship CASCADE;
DROP TABLE IF EXISTS Route CASCADE;
DROP TABLE IF EXISTS RoutesStep CASCADE;
DROP TABLE IF EXISTS Team CASCADE;
DROP TABLE IF EXISTS Person CASCADE;
DROP TABLE IF EXISTS PersonAndTeam CASCADE;
DROP TABLE IF EXISTS Flights CASCADE;


CREATE TABLE SpaceSystem
(
    systemID int PRIMARY KEY,
    name     varchar(40) NOT NULL
);

CREATE TABLE SpaceObject
(
    objectID int PRIMARY KEY,
    systemID int         NOT NULL,
    FOREIGN KEY (systemID) REFERENCES SpaceSystem (systemID) ON DELETE CASCADE,
    type     varchar(40) NOT NULL
);

CREATE TABLE Ship
(
    shipID integer PRIMARY KEY,
    name   varchar(30) NOT NULL
);

CREATE TABLE Route
(
    routeID     integer PRIMARY KEY,
    description varchar(100) NOT NULL
);

CREATE TABLE RoutesStep
(
    stepID   integer PRIMARY KEY,
    routeID  integer NOT NULL,
    FOREIGN KEY (routeID) REFERENCES Route (routeID) ON DELETE CASCADE,
    objectID integer NOT NULL,
    FOREIGN KEY (objectID) REFERENCES SpaceObject (objectID) ON DELETE CASCADE ,
    step     integer NOT NULL
);

CREATE TABLE Team
(
    teamID integer PRIMARY KEY
);

CREATE TABLE Person
(
    personID integer PRIMARY KEY,
    name     varchar(40) NOT NULL,
    sex      varchar(6)  NOT NULL,
    age      int         NOT NULL,
    shipRole varchar(40) NOT NULL
);

CREATE TABLE PersonAndTeam
(
    PandTID  integer PRIMARY KEY,
    personID integer NOT NULL,
    FOREIGN KEY (personID) REFERENCES Person (personID) ON DELETE CASCADE,
    teamID   integer NOT NULL,
    FOREIGN KEY (teamID) REFERENCES Team (teamID) ON DELETE CASCADE
);

CREATE TABLE Flights
(
    flightID  integer PRIMARY KEY,
    teamID    integer   NOT NULL,
    FOREIGN KEY (teamID) REFERENCES Team (teamID) ON DELETE CASCADE,
    shipID    integer   NOT NULL,
    FOREIGN KEY (shipID) REFERENCES Ship (shipID) ON DELETE CASCADE,
    routeID   integer   NOT NULL,
    FOREIGN KEY (routeID) REFERENCES Route (routeID) ON DELETE CASCADE,
    startTime timestamp NOT NULL,
    endTime   timestamp NOT NULL
);

INSERT INTO SpaceSystem (systemID, name)
VALUES (1, 'Solar System'),
       (2, 'Alpha Centauri'),
       (3, 'Andromeda');

INSERT INTO SpaceObject (objectID, systemID, type)
VALUES (1, 1, 'Planet'),
       (2, 1, 'Moon'),
       (3, 2, 'Exoplanet'),
       (4, 3, 'Black Hole');

INSERT INTO Ship (shipID, name)
VALUES (1, 'Apollo 11'),
       (2, 'Enterprise'),
       (3, 'Millennium Falcon'),
       (4, 'Voyager');

INSERT INTO Route (routeID, description)
VALUES (1, 'Basic route'),
       (2, 'Mars Exploration'),
       (3, 'Deep Space Mission');

INSERT INTO RoutesStep (stepID, routeID, objectID, step)
VALUES (1, 1, 1, 1),
       (2, 1, 2, 2),
       (3, 2, 3, 1),
       (4, 3, 4, 1),
       (5, 1, 3, 3);

INSERT INTO Team (teamID)
VALUES (1),
       (2),
       (3);

INSERT INTO Person (personID, name, sex, age, shipRole)
VALUES (1, 'Neil Armstrong', 'Male', 39, 'Commander'),
       (2, 'Buzz Aldrin', 'Male', 38, 'Pilot'),
       (3, 'Yuri Gagarin', 'Male', 34, 'Engineer'),
       (4, 'Sally Ride', 'Female', 32, 'Scientist');

INSERT INTO PersonAndTeam (PandTID, personID, teamID)
VALUES (1, 1, 1),
       (2, 2, 1),
       (3, 3, 2),
       (4, 4, 3),
       (5, 4, 2);


INSERT INTO Flights (flightID, teamID, shipID, routeID, startTime, endTime)
VALUES (1, 1, 1, 1, '1969-07-16 12:43:12', '1969-07-24 12:53:14'),
       (2, 2, 2, 2, '2030-05-10 15:23:15', '2030-06-15 11:43:12'),
       (3, 3, 3, 3, '2100-01-01 22:43:12', '2102-12-31 12:43:52'),
       (4, 3, 2, 1, '2100-01-01 12:49:14', '2102-12-31 02:43:12');

DELETE FROM Route WHERE routeID = 2;











