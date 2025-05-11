//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.utility;

import general.User;
import general.main_classes.Coordinates;
import general.main_classes.FormOfEducation;
import general.main_classes.Person;
import general.main_classes.StudyGroup;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class DBmanager {
    private String url = "jdbc:postgresql://localhost:6405/studs";
    private String password = "password";
    private String user = "s465826";
    private Connection connection = null;
    private PasswordManager passwordManager = new PasswordManager("s465826");
    public static HashMap<Integer, String> logins = new HashMap();
    public static HashMap<String, Integer> ids = new HashMap();

    public DBmanager() {
        try {
            MyLogger.info("Connecting to database...");
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            MyLogger.info("Database connection established");
            this.connection.setAutoCommit(false);
            this.initializeMaps();
            this.getCollection();
            MyLogger.info("Collection has been updated");
        } catch (SQLException e) {
            MyLogger.info("Error: " + e);
            MyLogger.info("Server cannot work without database");
            System.exit(1);
        }

    }

    public synchronized String checkUser(User user, String type) throws SQLException {
        String queryUser = "SELECT userID FROM users WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryUser, Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet ans = preparedStatement.executeQuery();
            if (!ans.next()) {
                MyLogger.info("User " + user.getUsername() + " not found");
                throw new SQLException("User not found\n");
            } else {
                MyLogger.info("User found");
                Integer userID = checkPassword(user);
                if (userID != -1 && type.equals("login")) {
                    logins.put(userID, user.getUsername());
                    ids.put(user.getUsername(), userID);
                    return "Successfully logged in\n";
                }
                if (type.equals("login")) {
                    return "Invalid password\n";
                }
                return "Login is busy";
            }
        } catch (SQLException e) {
            if (type.equals("login")) {
                return "User not found\n";
            }
            this.registerUser(user);
            return "Successfully registered";
        }
    }

    public synchronized Integer checkPassword(User user) {
        String queryPassword = "SELECT userID, hash, salt FROM users WHERE name = ?";

        try {
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(queryPassword, 1)) {
                preparedStatement.setString(1, user.getUsername());
                ResultSet ans = preparedStatement.executeQuery();
                if (!ans.next()) {
                    return -1;
                } else {
                    Integer id = ans.getInt(1);
                    byte[] hash = ans.getBytes(2);
                    String salt = ans.getString(3);
                    if (this.passwordManager.checkPassword(user.getPassword(), salt, hash)) {
                        return id;
                    } else {
                        return -1;
                    }
                }
            }
        } catch (SQLException var11) {
            return -1;
        }
    }

    private synchronized Integer registerUser(User user) throws SQLException {
        String queryUser = "INSERT INTO Users (name, hash, salt) VALUES (?, ?, ?)";
        String salt = this.passwordManager.getRandomString(30);

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(queryUser, 1)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setBytes(2, this.passwordManager.hashPassword(salt, user.getPassword()));
            preparedStatement.setString(3, salt);
            int ans = preparedStatement.executeUpdate();
            if (ans > 0) {
                ResultSet keys = preparedStatement.getGeneratedKeys();
                if (keys.next()) {
                    String id = String.valueOf(keys.getInt(1));
                    Integer userID = Integer.parseInt(id);
                    logins.put(userID, user.getUsername());
                    ids.put(user.getUsername(), userID);
                    MyLogger.info("Registered user: " + user.getUsername());
                    this.connection.commit();
                    return Integer.parseInt(id);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            MyLogger.info(e.getMessage());
            this.connection.rollback();
            return null;
        }
    }

    public Integer addStudyGroup(StudyGroup group, String login) throws SQLException {
        int idPerson = this.addPerson(group.getGroupAdmin());
        int idCoordinates = this.addCoordinates(group.getCoordinates());
        String queryGroup = "INSERT INTO StudyGroup (ownerID, name, coordinates, creationDate, studentsCount, expelledStudents, shouldbeexpelled, formOfEducation, groupAdmin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Integer var11;
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(queryGroup, 1)) {
                preparedStatement.setInt(1, ids.get(login));
                preparedStatement.setString(2, group.getName());
                preparedStatement.setInt(3, idCoordinates);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(group.getCreationDate().toLocalDateTime()));
                preparedStatement.setLong(5, group.getStudentsCount());
                preparedStatement.setLong(6, group.getExpelledStudents());
                preparedStatement.setLong(7, group.getShouldBeExpelled());
                String form = group.getFormOfEducation() != null ? group.getFormOfEducation().toString() : null;
                preparedStatement.setString(8, form);
                preparedStatement.setObject(9, idPerson != -1 ? idPerson : null, 4);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating group failed, no rows affected");
                }

                try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        this.connection.rollback();
                        throw new SQLException("Creating group failed, no ID obtained");
                    }

                    int generatedId = keys.getInt(1);
                    this.connection.commit();
                    var11 = generatedId;
                }
            }

            return var11;
        } catch (SQLException e) {
            this.connection.rollback();
            MyLogger.info("Error: " + e);
            return -1;
        }
    }

    private Integer addPerson(Person person) throws SQLException {
        if (person == null) {
            return -1;
        } else {
            String queryPerson = "INSERT INTO Person (name, birthday, height, weight, passportID)  VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(queryPerson, 1)) {
                preparedStatement.setString(1, person.getName());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(person.getBirthday()));
                preparedStatement.setLong(3, person.getHeight());
                preparedStatement.setInt(4, person.getWeight());
                preparedStatement.setString(5, person.getPassportID());
                preparedStatement.executeUpdate();
                ResultSet keys = preparedStatement.getGeneratedKeys();
                if (keys.next()) {
                    String id = String.valueOf(keys.getInt(1));
                    this.connection.commit();
                    return Integer.parseInt(id);
                }
            } catch (SQLException e) {
                this.connection.rollback();
                MyLogger.info(e.getMessage());
            }

            return -1;
        }
    }

    private Integer addCoordinates(Coordinates coordinates) throws SQLException {
        String queryCoordinates = "INSERT INTO Coordinates (x, y) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(queryCoordinates, 1)) {
            preparedStatement.setDouble(1, coordinates.getX());
            preparedStatement.setDouble(2, coordinates.getY());
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                String id = String.valueOf(keys.getInt(1));
                this.connection.commit();
                return Integer.parseInt(id);
            }
        } catch (SQLException e) {
            this.connection.rollback();
            MyLogger.info(e.getMessage());
        }

        return -1;
    }

    private void getCollection() throws SQLException {
        String query = "SELECT * FROM studygroup";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query, 1)) {
            ResultSet ans = preparedStatement.executeQuery();

            while (ans.next()) {
                Integer id = ans.getInt(1);
                Integer ownerID = ans.getInt(2);
                String name = ans.getString(3);
                Integer coordinatesID = ans.getInt(4);
                ZonedDateTime creationDate = ans.getTimestamp(5).toInstant().atZone(ZoneId.of("Europe/Moscow"));
                Integer studentsCount = ans.getInt(6);
                Integer expelledStudents = ans.getInt(7);
                Integer shouldBeExpelled = ans.getInt(8);
                String formOfEducation = ans.getString(9);
                FormOfEducation form = null;
                if (formOfEducation != null) {
                    form = FormOfEducation.valueOf(formOfEducation);
                }

                Integer groupAdminID = ans.getInt(10);
                Person person = this.getPerson(groupAdminID);
                Coordinates coordinates = this.getCoordinates(coordinatesID);
                StudyGroup.StudyGroupBuilder group = StudyGroup.builder();
                group.id(id).login(logins.get(ownerID)).name(name).coordinates(coordinates).creationDate(creationDate).studentsCount((long) studentsCount).expelledStudents(expelledStudents).shouldBeExpelled((long) shouldBeExpelled).formOfEducation(form).groupAdmin(person);
                StudyGroup studyGroup = group.build();
                CollectionManager.groups.add(studyGroup);
                CollectionManager.ids.put(id, studyGroup);
            }
        }

    }

    private Coordinates getCoordinates(Integer coordinatesID) throws SQLException {
        String queryCoordinates = "SELECT * FROM coordinates WHERE coordinatesid = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(queryCoordinates, 1)) {
            preparedStatement.setInt(1, coordinatesID);
            ResultSet ans = preparedStatement.executeQuery();
            if (ans.next()) {
                Integer x = ans.getInt(1);
                Integer y = ans.getInt(2);
                Coordinates.CoordinatesBuilder coordinates = Coordinates.builder().x((double) x).y((double) y);
                return coordinates.build();
            }
        }

        return null;
    }

    private Person getPerson(Integer personID) throws SQLException {
        String queryPerson = "SELECT * FROM person WHERE personid = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(queryPerson, 1)) {
            preparedStatement.setInt(1, personID);
            ResultSet ans = preparedStatement.executeQuery();
            if (ans.next()) {
                String name = ans.getString(2);
                LocalDateTime birthday = ans.getTimestamp(3).toLocalDateTime();
                Integer height = ans.getInt(4);
                Integer weight = ans.getInt(5);
                String passport = ans.getString(6);
                Person.PersonBuilder person = Person.builder();
                person.name(name).birthday(birthday).height((long) height).weight(weight).passportID(passport);
                return person.build();
            }
        }

        return null;
    }

    private void initializeMaps() throws SQLException {
        String query = "SELECT userid, name FROM users";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query, 1)) {
            ResultSet ans = preparedStatement.executeQuery();

            while (ans.next()) {
                Integer id = ans.getInt(1);
                String login = ans.getString(2);
                logins.put(id, login);
                ids.put(login, id);
            }
        }

    }

    public int deleteGroup(Integer groupID, String login) throws SQLException {
        if (!login.equals((CollectionManager.ids.get(groupID)).getLogin())) {
            return -11;
        } else {
            String deleteGroupQuery = "DELETE FROM studygroup WHERE groupid = ?";

            try (PreparedStatement deleteGroupStmt = this.connection.prepareStatement(deleteGroupQuery)) {
                deleteGroupStmt.setInt(1, groupID);
                int affectedRows = deleteGroupStmt.executeUpdate();
                this.connection.commit();
                return affectedRows;
            } catch (SQLException e) {
                this.connection.rollback();
                MyLogger.info(e.getMessage());
                return -1;
            }
        }
    }

    public Integer updateStudyGroup(StudyGroup group, Integer groupId, String login) throws SQLException {
        if (!login.equals(((StudyGroup) CollectionManager.ids.get(groupId)).getLogin())) {
            return -11;
        } else {
            this.updateCoordinates(group.getCoordinates(), groupId);
            this.updatePerson(group.getGroupAdmin(), groupId);
            String query = "UPDATE studygroup SET name = ?, creationdate = ?, studentscount = ?, expelledstudents = ?, shouldbeexpelled = ?, formofeducation = ? WHERE groupid = ?";

            try {
                Integer var8;
                try (PreparedStatement preparedStatement = this.connection.prepareStatement(query, 1)) {
                    preparedStatement.setString(1, group.getName());
                    preparedStatement.setTimestamp(2, Timestamp.valueOf(group.getCreationDate().toLocalDateTime()));
                    preparedStatement.setLong(3, group.getStudentsCount());
                    preparedStatement.setLong(4, (long) group.getExpelledStudents());
                    preparedStatement.setLong(5, group.getShouldBeExpelled());
                    String form = group.getFormOfEducation() != null ? group.getFormOfEducation().toString() : null;
                    preparedStatement.setString(6, form);
                    preparedStatement.setInt(7, groupId);
                    int affected = preparedStatement.executeUpdate();
                    this.connection.commit();
                    var8 = affected;
                }

                return var8;
            } catch (SQLException e) {
                this.connection.rollback();
                MyLogger.info("Update failed: " + e.getMessage());
                return -1;
            }
        }
    }

    private int updatePerson(Person person, Integer groupId) throws SQLException {
        if (person == null) {
            return -1;
        } else {
            Integer oldPersonId = this.getCurrentAdmin(groupId);
            String query = "UPDATE person SET name = ?, birthday = ?, height = ?, weight = ?, passportid = ? WHERE personid = ?";

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                preparedStatement.setString(1, person.getName());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(person.getBirthday()));
                preparedStatement.setLong(3, person.getHeight());
                preparedStatement.setInt(4, person.getWeight());
                preparedStatement.setString(5, person.getPassportID());
                preparedStatement.setInt(6, oldPersonId);
                int id = preparedStatement.executeUpdate();
                this.connection.commit();
                return id;
            } catch (SQLException e) {
                this.connection.rollback();
                MyLogger.info("Update failed: " + e.getMessage());
                return -1;
            }
        }
    }

    private int updateCoordinates(Coordinates coordinates, Integer groupId) throws SQLException {
        Integer oldCoordinatesId = this.getCurrentCoordinatesId(groupId);
        String query = "UPDATE coordinates SET x = ?, y = ? WHERE coordinatesid = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, coordinates.getX());
            preparedStatement.setDouble(2, coordinates.getY());
            preparedStatement.setInt(3, oldCoordinatesId);
            int id = preparedStatement.executeUpdate();
            this.connection.commit();
            return id;
        } catch (SQLException e) {
            this.connection.rollback();
            MyLogger.info("Update failed: " + e.getMessage());
            return -1;
        }
    }

    private Integer getCurrentCoordinatesId(Integer groupId) throws SQLException {
        String query = "SELECT coordinates FROM studygroup WHERE groupId = ?";

        Integer var5;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query, 1)) {
            preparedStatement.setInt(1, groupId);
            ResultSet rs = preparedStatement.executeQuery();
            var5 = rs.next() ? rs.getInt(1) : null;
        }

        return var5;
    }

    private Integer getCurrentAdmin(Integer groupId) throws SQLException {
        String query = "SELECT groupadmin FROM studygroup WHERE groupid = ?";

        Integer var5;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query, 1)) {
            preparedStatement.setInt(1, groupId);
            ResultSet rs = preparedStatement.executeQuery();
            var5 = rs.next() ? rs.getInt(1) : null;
        }

        return var5;
    }
}
