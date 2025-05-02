package org.example.Practice;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaritimeBookingApp extends Application {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost/sea_cruises";
    private static final String USER_NAME = "postgres";
    private static final String DATABASE_PASS = "postgres";

    private JDBCRunner jdbcRunner;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        jdbcRunner = new JDBCRunner(); // Создаём экземпляр вашего класса для работы с базой данных

        primaryStage.setTitle("Maritime Booking System");

        // Создаём вкладки для разных функций
        TabPane tabPane = new TabPane();

        // Вкладка 1: Список билетов
        Tab ticketsTab = new Tab("Tickets");
        ticketsTab.setClosable(false);
        ticketsTab.setContent(createTicketsTab());

        // Вкладка 2: Покупка билета
        Tab buyTicketTab = new Tab("Buy Ticket");
        buyTicketTab.setClosable(false);
        buyTicketTab.setContent(createBuyTicketTab());

        // Вкладка 3: Список клиентов
        Tab clientsTab = new Tab("Clients");
        clientsTab.setClosable(false);
        clientsTab.setContent(createClientsTab());

        // Вкладка 4: Добавление клиента
        Tab addClientTab = new Tab("Add Client");
        addClientTab.setClosable(false);
        addClientTab.setContent(createAddClientTab());

        tabPane.getTabs().addAll(ticketsTab, buyTicketTab, clientsTab, addClientTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createTicketsTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        GridPane.setConstraints(outputArea, 0, 0, 2, 1);

        Button showTicketsBtn = new Button("Show Tickets");
        GridPane.setConstraints(showTicketsBtn, 0, 1);

        showTicketsBtn.setOnAction(e -> {
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                StringBuilder tickets = new StringBuilder();
                var rs = jdbcRunner.getTicketsData(connection);
                while (rs.next()) {
                    tickets.append("ID: ").append(rs.getInt("id"))
                            .append(", Email: ").append(rs.getString("email"))
                            .append(", Price: ").append(rs.getDouble("price"))
                            .append(", Voyage ID: ").append(rs.getInt("voyage_id"))
                            .append("\n");
                }
                outputArea.setText(tickets.toString());
            } catch (SQLException ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });

        grid.getChildren().addAll(outputArea, showTicketsBtn);
        return grid;
    }

    private GridPane createBuyTicketTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        GridPane.setConstraints(emailField, 1, 0);

        TextField voyageIdField = new TextField();
        voyageIdField.setPromptText("Voyage ID");
        GridPane.setConstraints(voyageIdField, 1, 1);

        TextField vesselIdField = new TextField();
        vesselIdField.setPromptText("Vessel ID");
        GridPane.setConstraints(vesselIdField, 1, 2);

        TextField cabinIdField = new TextField();
        cabinIdField.setPromptText("Cabin ID");
        GridPane.setConstraints(cabinIdField, 1, 3);

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        GridPane.setConstraints(priceField, 1, 4);

        TextField paymentMethodField = new TextField();
        paymentMethodField.setPromptText("Payment Method");
        GridPane.setConstraints(paymentMethodField, 1, 5);

        TextField mealTypeField = new TextField();
        mealTypeField.setPromptText("Meal Type");
        GridPane.setConstraints(mealTypeField, 1, 6);

        CheckBox insuranceCheck = new CheckBox("Insurance");
        GridPane.setConstraints(insuranceCheck, 1, 7);

        TextField luggageField = new TextField();
        luggageField.setPromptText("Luggage Weight");
        GridPane.setConstraints(luggageField, 1, 8);

        TextField purchaseDateField = new TextField();
        purchaseDateField.setPromptText("Purchase Date (YYYY-MM-DD)");
        GridPane.setConstraints(purchaseDateField, 1, 9);

        Button buyTicketBtn = new Button("Buy Ticket");
        GridPane.setConstraints(buyTicketBtn, 1, 10);

        Label[] labels = new Label[] {
                new Label("Email:"), new Label("Voyage ID:"), new Label("Vessel ID:"),
                new Label("Cabin ID:"), new Label("Price:"), new Label("Payment Method:"),
                new Label("Meal Type:"), new Label("Insurance:"), new Label("Luggage Weight:"),
                new Label("Purchase Date:")
        };
        for (int i = 0; i < labels.length; i++) {
            GridPane.setConstraints(labels[i], 0, i);
        }

        buyTicketBtn.setOnAction(e -> {
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                jdbcRunner.buyTicket(connection,
                        emailField.getText(),
                        Integer.parseInt(voyageIdField.getText()),
                        vesselIdField.getText(),
                        Integer.parseInt(cabinIdField.getText()),
                        Double.parseDouble(priceField.getText()),
                        paymentMethodField.getText(),
                        mealTypeField.getText(),
                        insuranceCheck.isSelected(),
                        Double.parseDouble(luggageField.getText()),
                        purchaseDateField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket purchased successfully!");
            } catch (SQLException | NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to purchase ticket: " + ex.getMessage());
            }
        });

        grid.getChildren().addAll(emailField, voyageIdField, vesselIdField, cabinIdField, priceField,
                paymentMethodField, mealTypeField, insuranceCheck, luggageField, purchaseDateField, buyTicketBtn);
        grid.getChildren().addAll(labels);
        return grid;
    }

    private GridPane createClientsTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        GridPane.setConstraints(outputArea, 0, 0, 2, 1);

        Button showClientsBtn = new Button("Show Clients");
        GridPane.setConstraints(showClientsBtn, 0, 1);

        showClientsBtn.setOnAction(e -> {
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                StringBuilder clients = new StringBuilder();
                var rs = jdbcRunner.getClientsData(connection);
                while (rs.next()) {
                    clients.append("Email: ").append(rs.getString("email"))
                            .append(", Name: ").append(rs.getString("first_name"))
                            .append(" ").append(rs.getString("last_name"))
                            .append("\n");
                }
                outputArea.setText(clients.toString());
            } catch (SQLException ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });

        grid.getChildren().addAll(outputArea, showClientsBtn);
        return grid;
    }

    private GridPane createAddClientTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        GridPane.setConstraints(emailField, 1, 0);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        GridPane.setConstraints(lastNameField, 1, 1);

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        GridPane.setConstraints(firstNameField, 1, 2);

        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");
        GridPane.setConstraints(middleNameField, 1, 3);

        TextField birthDateField = new TextField();
        birthDateField.setPromptText("Birth Date (YYYY-MM-DD)");
        GridPane.setConstraints(birthDateField, 1, 4);

        TextField passportField = new TextField();
        passportField.setPromptText("Passport Series");
        GridPane.setConstraints(passportField, 1, 5);

        Button addClientBtn = new Button("Add Client");
        GridPane.setConstraints(addClientBtn, 1, 6);

        Label[] labels = new Label[] {
                new Label("Email:"), new Label("Last Name:"), new Label("First Name:"),
                new Label("Middle Name:"), new Label("Birth Date:"), new Label("Passport Series:")
        };
        for (int i = 0; i < labels.length; i++) {
            GridPane.setConstraints(labels[i], 0, i);
        }

        addClientBtn.setOnAction(e -> {
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                jdbcRunner.addClient(connection,
                        lastNameField.getText(),
                        firstNameField.getText(),
                        middleNameField.getText(),
                        Long.parseLong(passportField.getText()),
                        birthDateField.getText(),
                        emailField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Client added successfully!");
            } catch (SQLException | NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add client: " + ex.getMessage());
            }
        });

        grid.getChildren().addAll(emailField, lastNameField, firstNameField, middleNameField,
                birthDateField, passportField, addClientBtn);
        grid.getChildren().addAll(labels);
        return grid;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}