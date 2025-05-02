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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MaritimeBookingApp extends Application {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL_LOCALE_NAME = "localhost:5433/";
    private static final String DATABASE_NAME = "sea_cruises";
    public static final String USER_NAME = "daniil";
    public static final String DATABASE_PASS = "daniil";
    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;

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

    private VBox createTicketsTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        TableView<Ticket> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Ticket, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Ticket, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Ticket, Integer> voyageCol = new TableColumn<>("Voyage ID");
        voyageCol.setCellValueFactory(new PropertyValueFactory<>("voyageId"));
        table.getColumns().addAll(idCol, emailCol, priceCol, voyageCol);

        Button showTicketsBtn = new Button("Show Tickets");
        showTicketsBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        showTicketsBtn.setOnAction(e -> {
            ObservableList<Ticket> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = jdbcRunner.getTicketsData(connection);
                while (rs.next()) {
                    data.add(new Ticket(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getDouble("price"),
                            rs.getInt("voyage_id")));
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tickets: " + ex.getMessage());
            }
        });
        vbox.getChildren().addAll(table, showTicketsBtn);
        return vbox;
    }

    private VBox createBuyTicketTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Buy Ticket");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField voyageIdField = new TextField();
        voyageIdField.setPromptText("Voyage ID");
        TextField vesselIdField = new TextField();
        vesselIdField.setPromptText("Vessel ID");
        TextField cabinIdField = new TextField();
        cabinIdField.setPromptText("Cabin ID");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField paymentMethodField = new TextField();
        paymentMethodField.setPromptText("Payment Method");
        TextField mealTypeField = new TextField();
        mealTypeField.setPromptText("Meal Type");
        CheckBox insuranceCheck = new CheckBox("Insurance");
        TextField luggageField = new TextField();
        luggageField.setPromptText("Luggage Weight");
        TextField purchaseDateField = new TextField();
        purchaseDateField.setPromptText("Purchase Date (YYYY-MM-DD)");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button buyTicketBtn = new Button("Buy Ticket");
        buyTicketBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        buyTicketBtn.setOnAction(e -> {
            errorLabel.setText("");
            boolean valid = true;
            StringBuilder errors = new StringBuilder();
            if (emailField.getText().isBlank()) {
                valid = false;
                errors.append("Email required. ");
                emailField.setStyle("-fx-border-color: red;");
            } else
                emailField.setStyle("");
            if (voyageIdField.getText().isBlank() || !voyageIdField.getText().matches("\\d+")) {
                valid = false;
                errors.append("Voyage ID must be a number. ");
                voyageIdField.setStyle("-fx-border-color: red;");
            } else
                voyageIdField.setStyle("");
            if (vesselIdField.getText().isBlank()) {
                valid = false;
                errors.append("Vessel ID required. ");
                vesselIdField.setStyle("-fx-border-color: red;");
            } else
                vesselIdField.setStyle("");
            if (cabinIdField.getText().isBlank() || !cabinIdField.getText().matches("\\d+")) {
                valid = false;
                errors.append("Cabin ID must be a number. ");
                cabinIdField.setStyle("-fx-border-color: red;");
            } else
                cabinIdField.setStyle("");
            if (priceField.getText().isBlank() || !priceField.getText().matches("\\d+(\\.\\d+)?")) {
                valid = false;
                errors.append("Price must be a number. ");
                priceField.setStyle("-fx-border-color: red;");
            } else
                priceField.setStyle("");
            if (paymentMethodField.getText().isBlank()) {
                valid = false;
                errors.append("Payment method required. ");
                paymentMethodField.setStyle("-fx-border-color: red;");
            } else
                paymentMethodField.setStyle("");
            if (mealTypeField.getText().isBlank()) {
                valid = false;
                errors.append("Meal type required. ");
                mealTypeField.setStyle("-fx-border-color: red;");
            } else
                mealTypeField.setStyle("");
            if (luggageField.getText().isBlank() || !luggageField.getText().matches("\\d+(\\.\\d+)?")) {
                valid = false;
                errors.append("Luggage must be a number. ");
                luggageField.setStyle("-fx-border-color: red;");
            } else
                luggageField.setStyle("");
            if (purchaseDateField.getText().isBlank() || !purchaseDateField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                valid = false;
                errors.append("Date must be YYYY-MM-DD. ");
                purchaseDateField.setStyle("-fx-border-color: red;");
            } else
                purchaseDateField.setStyle("");
            if (!valid) {
                errorLabel.setText(errors.toString());
                return;
            }
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
                emailField.clear();
                voyageIdField.clear();
                vesselIdField.clear();
                cabinIdField.clear();
                priceField.clear();
                paymentMethodField.clear();
                mealTypeField.clear();
                insuranceCheck.setSelected(false);
                luggageField.clear();
                purchaseDateField.clear();
            } catch (SQLException ex1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to purchase ticket: " + ex1.getMessage());
            }
        });

        VBox form = new VBox(10, emailField, voyageIdField, vesselIdField, cabinIdField, priceField, paymentMethodField,
                mealTypeField, insuranceCheck, luggageField, purchaseDateField, errorLabel, buyTicketBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().addAll(title, form);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createClientsTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        TableView<Client> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Client, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Client, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        table.getColumns().addAll(emailCol, firstNameCol, lastNameCol);

        Button showClientsBtn = new Button("Show Clients");
        showClientsBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white;");
        showClientsBtn.setOnAction(e -> {
            ObservableList<Client> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = jdbcRunner.getClientsData(connection);
                while (rs.next()) {
                    data.add(new Client(
                            rs.getString("email"),
                            rs.getString("first_name"),
                            rs.getString("last_name")));
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load clients: " + ex.getMessage());
            }
        });
        vbox.getChildren().addAll(table, showClientsBtn);
        return vbox;
    }

    private VBox createAddClientTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Add Client");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");
        TextField birthDateField = new TextField();
        birthDateField.setPromptText("Birth Date (YYYY-MM-DD)");
        TextField passportField = new TextField();
        passportField.setPromptText("Passport Series");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button addClientBtn = new Button("Add Client");
        addClientBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        addClientBtn.setOnAction(e -> {
            errorLabel.setText("");
            boolean valid = true;
            StringBuilder errors = new StringBuilder();
            if (emailField.getText().isBlank()) {
                valid = false;
                errors.append("Email required. ");
                emailField.setStyle("-fx-border-color: red;");
            } else
                emailField.setStyle("");
            if (lastNameField.getText().isBlank()) {
                valid = false;
                errors.append("Last name required. ");
                lastNameField.setStyle("-fx-border-color: red;");
            } else
                lastNameField.setStyle("");
            if (firstNameField.getText().isBlank()) {
                valid = false;
                errors.append("First name required. ");
                firstNameField.setStyle("-fx-border-color: red;");
            } else
                firstNameField.setStyle("");
            if (passportField.getText().isBlank() || !passportField.getText().matches("\\d+")) {
                valid = false;
                errors.append("Passport must be a number. ");
                passportField.setStyle("-fx-border-color: red;");
            } else
                passportField.setStyle("");
            if (birthDateField.getText().isBlank() || !birthDateField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                valid = false;
                errors.append("Date must be YYYY-MM-DD. ");
                birthDateField.setStyle("-fx-border-color: red;");
            } else
                birthDateField.setStyle("");
            // middleName не обязательное
            if (!valid) {
                errorLabel.setText(errors.toString());
                return;
            }
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                jdbcRunner.addClient(connection,
                        lastNameField.getText(),
                        firstNameField.getText(),
                        middleNameField.getText(),
                        Long.parseLong(passportField.getText()),
                        birthDateField.getText(),
                        emailField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Client added successfully!");
                emailField.clear();
                lastNameField.clear();
                firstNameField.clear();
                middleNameField.clear();
                birthDateField.clear();
                passportField.clear();
            } catch (SQLException ex1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add client: " + ex1.getMessage());
            }
        });

        VBox form = new VBox(10, emailField, lastNameField, firstNameField, middleNameField, birthDateField,
                passportField, errorLabel, addClientBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().addAll(title, form);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Вспомогательные классы для TableView
    public static class Ticket {
        private final Integer id;
        private final String email;
        private final Double price;
        private final Integer voyageId;

        public Ticket(Integer id, String email, Double price, Integer voyageId) {
            this.id = id;
            this.email = email;
            this.price = price;
            this.voyageId = voyageId;
        }

        public Integer getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public Double getPrice() {
            return price;
        }

        public Integer getVoyageId() {
            return voyageId;
        }
    }

    public static class Client {
        private final String email;
        private final String firstName;
        private final String lastName;

        public Client(String email, String firstName, String lastName) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}