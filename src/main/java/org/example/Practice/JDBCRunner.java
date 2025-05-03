package org.example.Practice;

import java.sql.*;

public class JDBCRunner {

    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL_LOCALE_NAME = "localhost:5433/";
    private static final String DATABASE_NAME = "sea_cruises";
    public static final String USER_NAME = "daniil";
    public static final String DATABASE_PASS = "daniil";
    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;

    public JDBCRunner() {
        checkDriver();
        checkDB();
    }

    public static void checkDriver() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Нет JDBC-драйвера! Подключите JDBC-драйвер к проекту.", e);
        }
    }

    public static void checkDB() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            // Проверка подключения
        } catch (SQLException e) {
            throw new RuntimeException("Нет базы данных! Проверьте имя базы или разверните локально резервную копию.",
                    e);
        }
    }

    public ResultSet getClientsData(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM maritime_booking.customers;");
    }

    public ResultSet getTicketsData(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM maritime_booking.tickets;");
    }

    public ResultSet getTicketsForYearWithPrice(Connection connection, int year, double price) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT * FROM maritime_booking.tickets WHERE purchase_date BETWEEN    '" + year + "-01-01' AND '" + year + "-12-31' AND price <= " + price + ";");
    }

    public void buyTicket(Connection connection, String email, int voyageId, String vesselId, int cabinId,
            double price, String paymentMethod, String mealType, boolean insurance,
            double luggageWeight, String purchaseDate) throws SQLException {
        if (email == null || email.isBlank() || voyageId <= 0 || vesselId == null || vesselId.isBlank() ||
                cabinId <= 0 || price <= 0 || paymentMethod == null || mealType == null || purchaseDate == null) {
            throw new SQLException("Invalid input parameters.");
        }

        // Проверка, существует ли клиент
        PreparedStatement checkCustomerStmt = connection.prepareStatement(
                "SELECT 1 FROM maritime_booking.customers WHERE email = ?");
        checkCustomerStmt.setString(1, email);
        ResultSet customerRs = checkCustomerStmt.executeQuery();
        if (!customerRs.next()) {
            throw new SQLException("Customer with email " + email + " does not exist.");
        }

        // Проверка, существует ли рейс
        PreparedStatement checkVoyageStmt = connection.prepareStatement(
                "SELECT 1 FROM maritime_booking.voyages WHERE id = ? AND vessel_id = ?");
        checkVoyageStmt.setInt(1, voyageId);
        checkVoyageStmt.setString(2, vesselId);
        ResultSet voyageRs = checkVoyageStmt.executeQuery();
        if (!voyageRs.next()) {
            throw new SQLException("Voyage with ID " + voyageId + " and vessel ID " + vesselId + " does not exist.");
        }

        // Проверка, существует ли каюта
        PreparedStatement checkCabinStmt = connection.prepareStatement(
                "SELECT 1 FROM maritime_booking.cabins WHERE id = ? AND vessel_id = ?");
        checkCabinStmt.setInt(1, cabinId);
        checkCabinStmt.setString(2, vesselId);
        ResultSet cabinRs = checkCabinStmt.executeQuery();
        if (!cabinRs.next()) {
            throw new SQLException("Cabin with ID " + cabinId + " for vessel " + vesselId + " does not exist.");
        }

        // Вставка билета
        String insertTicketSql = "INSERT INTO maritime_booking.tickets (email, voyage_id, vessel_id, cabin_id, price, "
                +
                "payment_method, meal_type, insurance, luggage_weight, purchase_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        PreparedStatement insertStmt = connection.prepareStatement(insertTicketSql);
        insertStmt.setString(1, email);
        insertStmt.setInt(2, voyageId);
        insertStmt.setString(3, vesselId);
        insertStmt.setInt(4, cabinId);
        insertStmt.setDouble(5, price);
        insertStmt.setString(6, paymentMethod);
        insertStmt.setString(7, mealType);
        insertStmt.setBoolean(8, insurance);
        insertStmt.setDouble(9, luggageWeight);
        insertStmt.setDate(10, java.sql.Date.valueOf(purchaseDate));

        int count = insertStmt.executeUpdate();
        if (count > 0) {
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println("Inserted ticket with ID " + generatedKeys.getInt(1));
            }
        } else {
            throw new SQLException("Failed to insert ticket.");
        }
    }

    public void addClient(Connection connection, String lastName, String firstName, String middleName,
            long passportSeries, String birthDate, String email) throws SQLException {
        if (lastName == null || lastName.isBlank() || firstName == null || firstName.isBlank() ||
                passportSeries <= 0 || birthDate == null || birthDate.isBlank() || email == null || email.isBlank()) {
            throw new SQLException("Invalid input parameters.");
        }

        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO maritime_booking.customers (email, last_name, first_name, middle_name, birth_date, passport_series) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING email",
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, email);
        statement.setString(2, lastName);
        statement.setString(3, firstName);
        statement.setString(4, middleName);
        statement.setDate(5, java.sql.Date.valueOf(birthDate));
        statement.setLong(6, passportSeries);

        int count = statement.executeUpdate();
        if (count > 0) {
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Added client with email: " + rs.getString(1));
            }
        } else {
            throw new SQLException("Failed to add client.");
        }
    }
}