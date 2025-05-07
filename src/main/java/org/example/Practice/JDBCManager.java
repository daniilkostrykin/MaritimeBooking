package org.example.Practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCManager {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL_LOCALE_NAME = "localhost:5433/";
    private static final String DATABASE_NAME = "sea_cruises";
    public static final String USER_NAME = "daniil";
    public static final String DATABASE_PASS = "daniil";
    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;

    public JDBCManager() {
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
        try (Connection _ = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
        } catch (SQLException e) {
            throw new RuntimeException("Нет базы данных! Проверьте имя базы или разверните локально резервную копию.",
                    e);
        }
    }

    // Методы для работы с билетами
    public static ResultSet getTicketsForYearWithPrice(Connection connection, int year, double price)
            throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT * FROM maritime_booking.tickets WHERE purchase_date BETWEEN '" + year + "-01-01' AND '"
                        + year + "-12-31' AND price <= " + price + ";");
    }

    public static void buyTicket(Connection connection, String email, int voyageId, String vesselId, int cabinId,
            double price, String paymentMethod, String mealType, boolean insurance,
            double luggageWeight, String purchaseDate) throws SQLException {
        if (email == null || email.isBlank() || voyageId <= 0 || vesselId == null || vesselId.isBlank() ||
                cabinId <= 0 || price <= 0 || paymentMethod == null || mealType == null || purchaseDate == null) {
            throw new SQLException("Invalid input parameters.");
        }
        PreparedStatement checkCustomerStmt = connection.prepareStatement(
                "SELECT 1 FROM maritime_booking.customers WHERE email = ?");
        checkCustomerStmt.setString(1, email);
        ResultSet customerRs = checkCustomerStmt.executeQuery();
        if (!customerRs.next()) {
            throw new SQLException("Customer with email " + email + " does not exist.");
        }
        PreparedStatement checkVoyageStmt = connection.prepareStatement(
                "SELECT 1 FROM maritime_booking.voyages WHERE id = ? AND vessel_id = ?");
        checkVoyageStmt.setInt(1, voyageId);
        checkVoyageStmt.setString(2, vesselId);
        ResultSet voyageRs = checkVoyageStmt.executeQuery();
        if (!voyageRs.next()) {
            throw new SQLException("Voyage with ID " + voyageId + " and vessel ID " + vesselId + " does not exist.");
        }
        PreparedStatement checkCabinStmt = connection.prepareStatement(
                "SELECT 1 FROM maritime_booking.cabins WHERE id = ? AND vessel_id = ?");
        checkCabinStmt.setInt(1, cabinId);
        checkCabinStmt.setString(2, vesselId);
        ResultSet cabinRs = checkCabinStmt.executeQuery();
        if (!cabinRs.next()) {
            throw new SQLException("Cabin with ID " + cabinId + " for vessel " + vesselId + " does not exist.");
        }
        String insertTicketSql = "INSERT INTO maritime_booking.tickets (email, voyage_id, vessel_id, cabin_id, price, "
                + "payment_method, meal_type, insurance, luggage_weight, purchase_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
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
        ResultSet rs = insertStmt.executeQuery();
        if (rs.next()) {
            System.out.println("Inserted ticket with ID " + rs.getInt(1));
        }
    }

    // Методы для работы с клиентами
    public static void addClient(Connection connection, String lastName, String firstName, String middleName,
            long passportSeries, java.sql.Date birthDate, String email) throws SQLException {
        if (lastName == null || lastName.isBlank() || firstName == null || firstName.isBlank() ||
                passportSeries <= 0 || birthDate == null || email == null || email.isBlank()) {
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
        statement.setDate(5, birthDate);
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

    // Методы для работы с рейсами
    public static ResultSet getVoyageRoute(Connection connection, int voyageId) throws SQLException {
        String sql = "SELECT v.id AS voyage_id, v.vessel_id, ves.name AS vessel_name, v.status AS voyage_status, " +
                "vs.stop_number, dp.un_locode AS departure_port_code, dp.name AS departure_port_name, " +
                "dp.city AS departure_port_city, dp.country AS departure_port_country, vs.departure_datetime, " +
                "ap.un_locode AS arrival_port_code, ap.name AS arrival_port_name, ap.city AS arrival_port_city, " +
                "ap.country AS arrival_port_country, vs.arrival_datetime " +
                "FROM maritime_booking.voyages v " +
                "JOIN maritime_booking.vessels ves ON v.vessel_id = ves.imo " +
                "JOIN maritime_booking.voyage_stages vs ON v.id = vs.voyage_id AND v.vessel_id = vs.vessel_id " +
                "JOIN maritime_booking.ports dp ON vs.departure_port_id = dp.un_locode " +
                "JOIN maritime_booking.ports ap ON vs.arrival_port_id = ap.un_locode " +
                "WHERE v.id = ? " +
                "ORDER BY v.id, vs.stop_number";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, voyageId);
        return ps.executeQuery();
    }

    public static ResultSet getVoyageCities(Connection connection, int voyageId) throws SQLException {
        String sql = "SELECT vs.stop_number, dp.city AS departure_city, ap.city AS arrival_city " +
                "FROM maritime_booking.voyage_stages vs " +
                "JOIN maritime_booking.ports dp ON vs.departure_port_id = dp.un_locode " +
                "JOIN maritime_booking.ports ap ON vs.arrival_port_id = ap.un_locode " +
                "WHERE vs.voyage_id = ? " +
                "ORDER BY vs.stop_number";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, voyageId);
        return ps.executeQuery();
    }

    // Методы для работы с продажами
    public static ResultSet getTicketSales(Connection connection, String year) throws SQLException {
        String sql = "SELECT EXTRACT(MONTH FROM t.purchase_date) AS month, p1.country AS departure_country, " +
                "p2.country AS arrival_country, COUNT(t.id) AS ticket_count, " +
                "ROUND(SUM(t.price),0) AS total_revenue, ROUND(AVG(t.price), 0) AS avg_price, " +
                "ROUND(SUM(CASE WHEN t.insurance = true THEN 1 ELSE 0 END) * 100.0 / COUNT(t.id), 0) AS insurance_percentage "
                +
                "FROM maritime_booking.tickets t " +
                "JOIN maritime_booking.voyages v ON t.voyage_id = v.id AND t.vessel_id = v.vessel_id " +
                "JOIN maritime_booking.voyage_stages vs ON v.id = vs.voyage_id AND v.vessel_id = vs.vessel_id " +
                "JOIN maritime_booking.ports p1 ON vs.departure_port_id = p1.un_locode " +
                "JOIN maritime_booking.ports p2 ON vs.arrival_port_id = p2.un_locode " +
                "WHERE t.purchase_date BETWEEN CAST(? AS date) AND CAST(? AS date) " +
                "AND vs.stop_number = 1 " +
                "GROUP BY EXTRACT(MONTH FROM t.purchase_date), p1.country, p2.country " +
                "ORDER BY total_revenue DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, year + "-01-01");
        ps.setString(2, year + "-12-31");
        return ps.executeQuery();
    }

    // Методы для работы со средним чеком
    public static ResultSet getAverageCheck(Connection connection) throws SQLException {
        String sql = "SELECT c.email, COUNT(t.id) AS tickets_cnt, AVG(t.price) AS avg_price " +
                "FROM maritime_booking.customers c " +
                "JOIN maritime_booking.tickets t ON c.email = t.email " +
                "GROUP BY c.email " +
                "ORDER BY avg_price DESC";
        return connection.createStatement().executeQuery(sql);
    }

    // Методы для работы с выручкой
    public static ResultSet getTotalRevenue(Connection connection) throws SQLException {
        String sql = "SELECT vs.departure_port_id, vs.arrival_port_id, SUM(t.price) AS total_revenue " +
                "FROM maritime_booking.voyage_stages vs " +
                "JOIN maritime_booking.tickets t ON vs.voyage_id = t.voyage_id AND vs.vessel_id = t.vessel_id " +
                "GROUP BY vs.departure_port_id, vs.arrival_port_id " +
                "ORDER BY total_revenue DESC";
        return connection.createStatement().executeQuery(sql);
    }

    // Методы для работы с багажом
    public static void updateLuggagePrice(Connection connection, double minWeight, String date) throws SQLException {
        String sql = "UPDATE maritime_booking.tickets SET price = price + (luggage_weight - ?) * 1000 " +
                "WHERE purchase_date < CAST(? AS date) AND luggage_weight > ? " +
                "AND EXISTS (SELECT 1 FROM maritime_booking.voyages WHERE tickets.voyage_id = voyages.id " +
                "AND tickets.vessel_id = voyages.vessel_id AND voyages.status = 'active') " +
                "AND EXISTS (SELECT 1 FROM maritime_booking.customers WHERE tickets.email = customers.email)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDouble(1, minWeight);
        ps.setString(2, date);
        ps.setDouble(3, minWeight);
        ps.executeUpdate();
    }

    // Методы для удаления рейса
    public static void deleteVoyage(Connection connection, int voyageId, String vesselId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Удаление билетов
            String sql1 = "DELETE FROM maritime_booking.tickets WHERE voyage_id = ? AND vessel_id = ?";
            PreparedStatement ps1 = connection.prepareStatement(sql1);
            ps1.setInt(1, voyageId);
            ps1.setString(2, vesselId);
            ps1.executeUpdate();

            // Удаление этапов рейса
            String sql2 = "DELETE FROM maritime_booking.voyage_stages WHERE voyage_id = ? AND vessel_id = ?";
            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setInt(1, voyageId);
            ps2.setString(2, vesselId);
            ps2.executeUpdate();

            // Удаление рейса
            String sql3 = "DELETE FROM maritime_booking.voyages WHERE id = ? AND vessel_id = ?";
            PreparedStatement ps3 = connection.prepareStatement(sql3);
            ps3.setInt(1, voyageId);
            ps3.setString(2, vesselId);
            ps3.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Методы для получения списков
    public static ResultSet getActiveVoyages(Connection connection) throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT id, vessel_id FROM maritime_booking.voyages WHERE status = 'active'");
    }

    public static ResultSet getVessels(Connection connection) throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT imo, name FROM maritime_booking.vessels");
    }

    public static ResultSet getCabins(Connection connection) throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT id, vessel_id, category, capacity, window_view FROM maritime_booking.cabins");
    }

    public static ResultSet getCustomers(Connection connection) throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT email, first_name, last_name FROM maritime_booking.customers ORDER BY last_name, first_name");
    }

    public static ResultSet getCountries(Connection connection) throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT DISTINCT country FROM maritime_booking.ports ORDER BY country");
    }

    public static ResultSet getVoyagesWithVessels(Connection connection) throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT v.id, v.vessel_id, vs.name " +
                        "FROM maritime_booking.voyages v " +
                        "JOIN maritime_booking.vessels vs ON v.vessel_id = vs.imo");
    }
}