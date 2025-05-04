package org.example.Practice;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MaritimeBookingApp extends Application {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String URL_LOCALE_NAME = "localhost:5433/";
    private static final String DATABASE_NAME = "sea_cruises";
    public static final String USER_NAME = "daniil";
    public static final String DATABASE_PASS = "daniil";
    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Maritime Booking System");

        TabPane tabPane = new TabPane();

        Tab universalTab = new Tab("Таблицы");
        universalTab.setClosable(false);
        universalTab.setContent(createUniversalTableTab());

        Tab buyTicketTab = new Tab("Покупка билета");
        buyTicketTab.setClosable(false);
        buyTicketTab.setContent(createBuyTicketTab());

        Tab addClientTab = new Tab("Добавление клиента");
        addClientTab.setClosable(false);
        addClientTab.setContent(createAddClientTab());

        Tab ticketsForYearWithPriceTab = new Tab("Билеты за год с ценой меньше");
        ticketsForYearWithPriceTab.setClosable(false);
        ticketsForYearWithPriceTab.setContent(createTicketsForYearWithPriceTab());

        Tab ticketsByClientTab = new Tab("Билеты клиента");
        ticketsByClientTab.setClosable(false);
        ticketsByClientTab.setContent(createTicketsByClientTab());

        Tab clientsByMealTab = new Tab("Клиенты по питанию");
        clientsByMealTab.setClosable(false);
        clientsByMealTab.setContent(createClientsByMealTab());

        Tab completedVoyagesTab = new Tab("Рейсы по статусу и питанию");
        completedVoyagesTab.setClosable(false);
        completedVoyagesTab.setContent(createCompletedVoyagesTab());

        Tab insuredTicketsFromCountryTab = new Tab("Билеты с страховкой по стране");
        insuredTicketsFromCountryTab.setClosable(false);
        insuredTicketsFromCountryTab.setContent(createInsuredTicketsFromCountryTab());

        Tab addClientAndTicketTab = new Tab("Добавить клиента и билет");
        addClientAndTicketTab.setClosable(false);
        addClientAndTicketTab.setContent(createAddClientAndTicketTab());

        Tab deleteVoyageTab = new Tab("Удалить рейс и всё связанное");
        deleteVoyageTab.setClosable(false);
        deleteVoyageTab.setContent(createDeleteVoyageTab());

        Tab luggagePriceUpdateTab = new Tab("Корректировка цены багажа");
        luggagePriceUpdateTab.setClosable(false);
        luggagePriceUpdateTab.setContent(createLuggagePriceUpdateTab());

        Tab voyageRouteTab = new Tab("Маршрут рейса");
        voyageRouteTab.setClosable(false);
        voyageRouteTab.setContent(createVoyageRouteTab());

        Tab ticketSalesTab = new Tab("Продажи билетов");
        ticketSalesTab.setClosable(false);
        ticketSalesTab.setContent(createTicketSalesTab());

        Tab avgCheckTab = new Tab("Средний чек по клиентам");
        avgCheckTab.setClosable(false);
        avgCheckTab.setContent(createAvgCheckTab());

        Tab totalRevenueTab = new Tab("Выручка по маршрутам");
        totalRevenueTab.setClosable(false);
        totalRevenueTab.setContent(createTotalRevenueTab());

        tabPane.getTabs().addAll(universalTab, buyTicketTab, addClientTab, ticketsForYearWithPriceTab,
                ticketsByClientTab, clientsByMealTab, completedVoyagesTab, insuredTicketsFromCountryTab,
                addClientAndTicketTab, deleteVoyageTab, luggagePriceUpdateTab, voyageRouteTab, ticketSalesTab,
                avgCheckTab, totalRevenueTab);

        // --- ComboBox для выбора темы ---
        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Тёмная", "Светлая");
        themeCombo.setValue("Тёмная");
        themeCombo.setStyle("-fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 2 10 2 10;");

        HBox topBar = new HBox(themeCombo);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(8, 16, 8, 8));
        topBar.setSpacing(10);
        topBar.setStyle("-fx-background-color: #222;"); // по умолчанию тёмная

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        // По умолчанию тёмная тема
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());

        themeCombo.setOnAction(e -> {
            scene.getStylesheets().clear();
            if (themeCombo.getValue().equals("Тёмная")) {
                scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
                topBar.setStyle("-fx-background-color: #222;");
            } else {
                scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
                topBar.setStyle("-fx-background-color: #f4f4f4;");
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createBuyTicketTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Покупка билета");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email клиента");
        TextField voyageIdField = new TextField();
        voyageIdField.setPromptText("ID рейса");
        TextField vesselIdField = new TextField();
        vesselIdField.setPromptText("ID судна");
        TextField cabinIdField = new TextField();
        cabinIdField.setPromptText("ID каюты");
        TextField priceField = new TextField();
        priceField.setPromptText("Цена");
        TextField paymentMethodField = new TextField();
        paymentMethodField.setPromptText("Способ оплаты");
        TextField mealTypeField = new TextField();
        mealTypeField.setPromptText("Тип питания");
        CheckBox insuranceCheck = new CheckBox("Страховка");
        TextField luggageField = new TextField();
        luggageField.setPromptText("Вес багажа");
        TextField purchaseDateField = new TextField();
        purchaseDateField.setPromptText("Дата покупки (ГГГГ-ММ-ДД)");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button buyTicketBtn = new Button("Купить билет");
        buyTicketBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        buyTicketBtn.setOnAction(_ -> {
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
                buyTicket(connection,
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

    private VBox createTicketsForYearWithPriceTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Билеты за год с ценой меньше");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField yearField = new TextField();
        yearField.setPromptText("Год (например, 2024)");
        TextField priceField = new TextField();
        priceField.setPromptText("Максимальная цена");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button filterBtn = new Button("Показать билеты");
        filterBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(60);

        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        priceCol.setPrefWidth(100);

        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);

        filterBtn.setOnAction(_ -> {
            String year = yearField.getText().trim();
            String maxPrice = priceField.getText().trim();
            if (year.isEmpty() || maxPrice.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите год и максимальную цену!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = getTicketsForYearWithPrice(connection, Integer.parseInt(year),
                        Double.parseDouble(maxPrice));
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("price"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, yearField, priceField, errorLabel, filterBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);

        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createAddClientTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Добавление клиента");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Фамилия");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Имя");
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Отчество");
        TextField birthDateField = new TextField();
        birthDateField.setPromptText("Дата рождения (ГГГГ-ММ-ДД)");
        TextField passportField = new TextField();
        passportField.setPromptText("Серия паспорта");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button addClientBtn = new Button("Добавить клиента");
        addClientBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        addClientBtn.setOnAction(_ -> {
            errorLabel.setText("");
            boolean valid = true;
            StringBuilder errors = new StringBuilder();
            if (emailField.getText().isBlank()) {
                valid = false;
                errors.append("Требуется email. ");
                emailField.setStyle("-fx-border-color: red;");
            } else
                emailField.setStyle("");
            if (lastNameField.getText().isBlank()) {
                valid = false;
                errors.append("Требуется фамилия. ");
                lastNameField.setStyle("-fx-border-color: red;");
            } else
                lastNameField.setStyle("");
            if (firstNameField.getText().isBlank()) {
                valid = false;
                errors.append("Требуется имя. ");
                firstNameField.setStyle("-fx-border-color: red;");
            } else
                firstNameField.setStyle("");
            if (passportField.getText().isBlank() || !passportField.getText().matches("\\d+")) {
                valid = false;
                errors.append("Серия паспорта должна быть числом. ");
                passportField.setStyle("-fx-border-color: red;");
            } else
                passportField.setStyle("");
            if (birthDateField.getText().isBlank() || !birthDateField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                valid = false;
                errors.append("Дата должна быть в формате ГГГГ-ММ-ДД. ");
                birthDateField.setStyle("-fx-border-color: red;");
            } else
                birthDateField.setStyle("");
            // middleName не обязательное
            if (!valid) {
                errorLabel.setText(errors.toString());
                return;
            }
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                addClient(connection,
                        lastNameField.getText(),
                        firstNameField.getText(),
                        middleNameField.getText(),
                        Long.parseLong(passportField.getText()),
                        java.sql.Date.valueOf(birthDateField.getText()),
                        emailField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент успешно добавлен!");
                emailField.clear();
                lastNameField.clear();
                firstNameField.clear();
                middleNameField.clear();
                birthDateField.clear();
                passportField.clear();
            } catch (SQLException ex1) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить клиента: " + ex1.getMessage());
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

    private VBox createTicketsByClientTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Билеты клиента");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Введите email клиента:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email клиента");
        Button searchBtn = new Button("Показать билеты");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(60);
        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        priceCol.setPrefWidth(100);
        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);

        searchBtn.setOnAction(_ -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите email клиента!");
                return;
            }
            // Простая валидация email
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Некорректный формат email!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT tickets.id, tickets.price\n" +
                        "FROM maritime_booking.tickets\n" +
                        "JOIN maritime_booking.customers ON tickets.email = customers.email\n" +
                        "WHERE customers.email = ?\n" +
                        "ORDER BY tickets.id";
                var ps = connection.prepareStatement(sql);
                ps.setString(1, email);
                var rs = ps.executeQuery();
                while (rs.next()) {
                    data.add(FXCollections.observableArrayList(rs.getString("id"), rs.getString("price")));
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, label, emailField, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);

        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createClientsByMealTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Клиенты по питанию");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Выберите тип питания:");
        ComboBox<String> mealTypeCombo = new ComboBox<>();
        mealTypeCombo.getItems().addAll("full_board", "breakfast", "half_board", "no_meals");
        mealTypeCombo.setPromptText("Тип питания");
        Button searchBtn = new Button("Показать клиентов");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        emailCol.setPrefWidth(200);
        TableColumn<ObservableList<String>, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        nameCol.setPrefWidth(150);
        table.getColumns().add(emailCol);
        table.getColumns().add(nameCol);

        searchBtn.setOnAction(_ -> {
            String mealType = mealTypeCombo.getValue();
            if (mealType == null || mealType.isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите тип питания!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT customers.email, customers.first_name\n" +
                        "FROM maritime_booking.customers\n" +
                        "JOIN maritime_booking.tickets ON customers.email = tickets.email\n" +
                        "WHERE tickets.meal_type = ?";
                var ps = connection.prepareStatement(sql);
                ps.setString(1, mealType);
                var rs = ps.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("email"));
                    row.add(rs.getString("first_name"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, label, mealTypeCombo, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);

        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private ScrollPane wrapWithScroll(VBox vbox) {
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private javafx.scene.Node createUniversalTableTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        ComboBox<String> tablesCombo = new ComboBox<>();
        TableView<ObservableList<String>> tableView = new TableView<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var meta = connection.getMetaData();
            var rs = meta.getTables(null, "maritime_booking", "%", new String[] { "TABLE" });
            while (rs.next()) {
                tablesCombo.getItems().add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось получить список таблиц: " + e.getMessage());
        }

        tablesCombo.setOnAction(_ -> {
            String tableName = tablesCombo.getValue();
            if (tableName == null)
                return;

            tableView.getColumns().clear();
            tableView.getItems().clear();

            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT * FROM maritime_booking." + tableName + " LIMIT 100");
                var rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    final int colIndex = i - 1;
                    TableColumn<ObservableList<String>, String> col = new TableColumn<>(rsmd.getColumnName(i));
                    col.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(colIndex)));
                    tableView.getColumns().add(col);
                }

                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getString(i));
                    }
                    tableView.getItems().add(row);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
            }
        });

        vbox.getChildren().addAll(new Label("Выберите таблицу:"), tablesCombo, tableView);
        return wrapWithScroll(vbox);
    }

    private VBox createCompletedVoyagesTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Завершённые рейсы с BREAKFAST");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Выберите статус рейса:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("completed", "active", "cancelled");
        statusCombo.setPromptText("Статус рейса");

        Label mealLabel = new Label("Выберите тип питания:");
        ComboBox<String> mealCombo = new ComboBox<>();
        mealCombo.getItems().addAll("full_board", "breakfast", "half_board", "no_meals");
        mealCombo.setPromptText("Тип питания");

        Button searchBtn = new Button("Показать рейсы");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        statusCol.setPrefWidth(120);
        table.getColumns().add(idCol);
        table.getColumns().add(statusCol);
        searchBtn.setOnAction(_ -> {
            String status = statusCombo.getValue();
            String meal = mealCombo.getValue();
            if (status == null || meal == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите статус рейса и тип питания!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT DISTINCT voyages.id, voyages.status\n" +
                        "FROM maritime_booking.voyages\n" +
                        "JOIN maritime_booking.tickets ON tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id\n"
                        +
                        "JOIN maritime_booking.customers ON tickets.email = customers.email\n" +
                        "WHERE voyages.status = ? AND tickets.meal_type = ?";
                var ps = connection.prepareStatement(sql);
                ps.setString(1, status);
                ps.setString(2, meal);
                var rs = ps.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("status"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, statusLabel, statusCombo, mealLabel, mealCombo, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createInsuredTicketsFromCountryTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Билеты с страховкой из страны");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Введите страну (на англ.):");
        TextField countryField = new TextField();
        countryField.setPromptText("Страна");
        Button searchBtn = new Button("Показать билеты");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        priceCol.setPrefWidth(120);
        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);
        searchBtn.setOnAction(_ -> {
            String country = countryField.getText().trim();
            if (country.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите страну!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT tickets.id, tickets.price\n" +
                        "FROM maritime_booking.tickets\n" +
                        "JOIN maritime_booking.voyages ON tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id\n"
                        +
                        "JOIN maritime_booking.voyage_stages ON voyage_stages.voyage_id = voyages.id AND voyage_stages.vessel_id = voyages.vessel_id\n"
                        +
                        "JOIN maritime_booking.ports ON voyage_stages.departure_port_id = ports.un_locode\n" +
                        "WHERE ports.country = ? AND tickets.insurance = true AND voyage_stages.stop_number = 1";
                var ps = connection.prepareStatement(sql);
                ps.setString(1, country.toLowerCase());
                var rs = ps.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("price"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, label, countryField, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private javafx.scene.Node createAddClientAndTicketTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Добавить клиента и билет");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Добавить клиента и билет:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Фамилия");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Имя");
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Отчество");
        TextField birthDateField = new TextField();
        birthDateField.setPromptText("Дата рождения (ГГГГ-ММ-ДД)");
        TextField passportField = new TextField();
        passportField.setPromptText("Серия паспорта (10 цифр)");

        // Выпадающие списки для ID
        Label voyageLabel = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("ID рейса");

        Label vesselLabel = new Label("Выберите судно:");
        ComboBox<String> vesselCombo = new ComboBox<>();
        vesselCombo.setPromptText("IMO судна");

        Label cabinLabel = new Label("Выберите каюту:");
        ComboBox<String> cabinCombo = new ComboBox<>();
        cabinCombo.setPromptText("ID каюты");

        // Загрузка данных в ComboBox
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            // Загрузка рейсов
            var rs = connection.createStatement().executeQuery(
                    "SELECT id, vessel_id FROM maritime_booking.voyages WHERE status = 'active'");
            while (rs.next()) {
                voyageCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") + ")");
            }

            // Загрузка судов
            rs = connection.createStatement().executeQuery(
                    "SELECT imo, name FROM maritime_booking.vessels");
            while (rs.next()) {
                vesselCombo.getItems().add(rs.getString("imo") + " (" + rs.getString("name") + ")");
            }

            // Загрузка кают
            rs = connection.createStatement().executeQuery(
                    "SELECT id, vessel_id, category, capacity, window_view FROM maritime_booking.cabins");
            while (rs.next()) {
                cabinCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                        ", категория: " + rs.getString("category") +
                        ", вместимость: " + rs.getString("capacity") +
                        ", окно: " + (rs.getBoolean("window_view") ? "да" : "нет") + ")");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }

        // Билет
        TextField priceField = new TextField();
        priceField.setPromptText("Цена");
        ComboBox<String> paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("card", "cash");
        paymentMethodCombo.setPromptText("Способ оплаты");
        ComboBox<String> mealTypeCombo = new ComboBox<>();
        mealTypeCombo.getItems().addAll("no_meals", "breakfast", "half_board", "full_board", "all_inclusive",
                "ultra_all_inclusive");
        mealTypeCombo.setPromptText("Тип питания");
        CheckBox insuranceCheck = new CheckBox("Страховка");
        TextField luggageField = new TextField();
        luggageField.setPromptText("Вес багажа (кг)");
        TextField purchaseDateField = new TextField();
        purchaseDateField.setPromptText("Дата покупки (ГГГГ-ММ-ДД)");
        Button addBtn = new Button("Добавить клиента и билет");
        addBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(250);
        addBtn.setOnAction(_ -> {
            // Валидация
            if (emailField.getText().isBlank()
                    || !emailField.getText().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный email!");
                return;
            }
            if (lastNameField.getText().isBlank() || firstNameField.getText().isBlank()
                    || passportField.getText().isBlank() || !passportField.getText().matches("^[1-9][0-9]{9}$")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Заполните ФИО и серию паспорта (10 цифр)!");
                return;
            }
            if (birthDateField.getText().isBlank() || !birthDateField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Дата рождения должна быть в формате ГГГГ-ММ-ДД!");
                return;
            }
            if (voyageCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите рейс!");
                return;
            }
            if (vesselCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите судно!");
                return;
            }
            if (cabinCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите каюту!");
                return;
            }
            if (priceField.getText().isBlank() || !priceField.getText().matches("\\d+(\\.\\d+)?")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Цена должна быть числом!");
                return;
            }
            if (paymentMethodCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите способ оплаты!");
                return;
            }
            if (mealTypeCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите тип питания!");
                return;
            }
            if (luggageField.getText().isBlank() || !luggageField.getText().matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Вес багажа должен быть целым числом!");
                return;
            }
            if (purchaseDateField.getText().isBlank() || !purchaseDateField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Дата покупки должна быть в формате ГГГГ-ММ-ДД!");
                return;
            }

            // Извлечение ID из выбранных значений
            String voyageId = voyageCombo.getValue().split(" ")[0];
            String vesselId = vesselCombo.getValue().split(" ")[0];
            String cabinId = cabinCombo.getValue().split(" ")[0];

            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                connection.setAutoCommit(false);
                try {
                    String sql1 = "INSERT INTO maritime_booking.customers (email, last_name, first_name, middle_name, birth_date, passport_series) VALUES (?, ?, ?, ?, ?, ?)";
                    var ps1 = connection.prepareStatement(sql1);
                    ps1.setString(1, emailField.getText());
                    ps1.setString(2, lastNameField.getText());
                    ps1.setString(3, firstNameField.getText());
                    ps1.setString(4, middleNameField.getText());
                    ps1.setDate(5, java.sql.Date.valueOf(birthDateField.getText()));
                    ps1.setString(6, passportField.getText());
                    ps1.executeUpdate();
                    String sql2 = "INSERT INTO maritime_booking.tickets (email, voyage_id, vessel_id, cabin_id, price, payment_method, meal_type, insurance, luggage_weight, purchase_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    var ps2 = connection.prepareStatement(sql2);
                    ps2.setString(1, emailField.getText());
                    ps2.setInt(2, Integer.parseInt(voyageId));
                    ps2.setString(3, vesselId);
                    ps2.setInt(4, Integer.parseInt(cabinId));
                    ps2.setDouble(5, Double.parseDouble(priceField.getText()));
                    ps2.setString(6, paymentMethodCombo.getValue());
                    ps2.setString(7, mealTypeCombo.getValue());
                    ps2.setBoolean(8, insuranceCheck.isSelected());
                    ps2.setInt(9, Integer.parseInt(luggageField.getText()));
                    ps2.setDate(10, java.sql.Date.valueOf(purchaseDateField.getText()));
                    ps2.executeUpdate();
                    connection.commit();
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент и билет успешно добавлены!");
                    // Показываем всю таблицу customers
                    showTable("SELECT * FROM maritime_booking.customers", table);
                } catch (SQLException ex) {
                    connection.rollback();
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при добавлении: " + ex.getMessage());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка подключения: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, label, emailField, lastNameField, firstNameField, middleNameField, birthDateField,
                passportField, voyageLabel, voyageCombo, vesselLabel, vesselCombo, cabinLabel, cabinCombo, priceField,
                paymentMethodCombo, mealTypeCombo, insuranceCheck, luggageField, purchaseDateField, addBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        VBox formWrapper = new VBox(form);
        formWrapper.setAlignment(Pos.CENTER);
        formWrapper.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane(formWrapper);
        scrollPane.setFitToWidth(true);
        // scrollPane.setStyle("-fx-background: #222;"); // убираем, чтобы фон был как у
        // темы
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, scrollPane, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createDeleteVoyageTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Удалить рейс и всё связанное");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Введите ID рейса и ID судна для удаления:");
        TextField voyageIdField = new TextField();
        voyageIdField.setPromptText("ID рейса");
        TextField vesselIdField = new TextField();
        vesselIdField.setPromptText("ID судна");
        Button deleteBtn = new Button("Удалить всё связанное");
        deleteBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #E53935; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        deleteBtn.setOnAction(_ -> {
            String voyageId = voyageIdField.getText().trim();
            String vesselId = vesselIdField.getText().trim();
            if (voyageId.isEmpty() || vesselId.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите оба значения!");
                return;
            }
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                connection.setAutoCommit(false);
                try {
                    String sql1 = "DELETE FROM maritime_booking.tickets WHERE voyage_id = ? AND vessel_id = ?";
                    var ps1 = connection.prepareStatement(sql1);
                    ps1.setInt(1, Integer.parseInt(voyageId));
                    ps1.setString(2, vesselId);
                    ps1.executeUpdate();
                    String sql2 = "DELETE FROM maritime_booking.voyage_stages WHERE voyage_id = ? AND vessel_id = ?";
                    var ps2 = connection.prepareStatement(sql2);
                    ps2.setInt(1, Integer.parseInt(voyageId));
                    ps2.setString(2, vesselId);
                    ps2.executeUpdate();
                    String sql3 = "DELETE FROM maritime_booking.voyages WHERE id = ? AND vessel_id = ?";
                    var ps3 = connection.prepareStatement(sql3);
                    ps3.setInt(1, Integer.parseInt(voyageId));
                    ps3.setString(2, vesselId);
                    ps3.executeUpdate();
                    connection.commit();
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Рейс и всё связанное удалено!");
                    // Показываем всю таблицу voyages
                    showTable("SELECT * FROM maritime_booking.voyages", table);
                } catch (SQLException ex) {
                    connection.rollback();
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при удалении: " + ex.getMessage());
                }
                // Проверка, что удалено
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                String sqlCheck = "SELECT * FROM maritime_booking.voyages WHERE id = ? AND vessel_id = ?";
                var psCheck = connection.prepareStatement(sqlCheck);
                psCheck.setInt(1, Integer.parseInt(voyageId));
                psCheck.setString(2, vesselId);
                var rs = psCheck.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("vessel_id"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка подключения: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, label, voyageIdField, vesselIdField, deleteBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private javafx.scene.Node createLuggagePriceUpdateTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Корректировка цены багажа");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label weightLabel = new Label("Минимальный вес багажа (кг):");
        TextField weightField = new TextField("15");
        weightField.setPromptText("Вес");
        Label dateLabel = new Label("Дата до (YYYY-MM-DD):");
        TextField dateField = new TextField("2025-04-16");
        dateField.setPromptText("Дата");
        Button updateBtn = new Button("Обновить цены");
        updateBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        updateBtn.setOnAction(_ -> {
            String minWeight = weightField.getText().trim();
            String date = dateField.getText().trim();
            if (minWeight.isEmpty() || !minWeight.matches("\\d+(\\.\\d+)?")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный вес!");
                return;
            }
            if (date.isEmpty() || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректную дату!");
                return;
            }
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "UPDATE maritime_booking.tickets SET price = price + (luggage_weight - ?) * 1000 WHERE purchase_date < CAST(? AS date) AND luggage_weight > ? AND EXISTS (SELECT 1 FROM maritime_booking.voyages WHERE tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id AND voyages.status = 'active') AND EXISTS (SELECT 1 FROM maritime_booking.customers WHERE tickets.email = customers.email)";
                var ps = connection.prepareStatement(sql);
                ps.setDouble(1, Double.parseDouble(minWeight));
                ps.setString(2, date);
                ps.setDouble(3, Double.parseDouble(minWeight));
                ps.executeUpdate();
                // Показываем всю таблицу tickets
                showTable("SELECT * FROM maritime_booking.tickets", table);
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Цены обновлены!");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка обновления: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, weightLabel, weightField, dateLabel, dateField, updateBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createVoyageRouteTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Маршрут рейса");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Введите ID рейса:");
        TextField voyageIdField = new TextField();
        voyageIdField.setPromptText("ID рейса");
        Button searchBtn = new Button("Показать маршрут");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        // Основная таблица (все поля)
        String[] columnNames = {
                "ID рейса", "ID судна", "Название судна", "Статус рейса", "№ остановки",
                "Код порта отправления", "Порт отправления", "Город отправления", "Страна отправления",
                "Дата отправления",
                "Код порта прибытия", "Порт прибытия", "Город прибытия", "Страна прибытия", "Дата прибытия"
        };
        for (String colName : columnNames) {
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
            final int idx = java.util.Arrays.asList(columnNames).indexOf(colName);
            col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(idx)));
            col.setPrefWidth(120);
            table.getColumns().add(col);
        }

        // Дополнительная таблица маршрута по городам
        Label routeLabel = new Label("Маршрут по городам:");
        TableView<ObservableList<String>> routeTable = new TableView<>();
        // routeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        routeTable.setPrefHeight(200);
        TableColumn<ObservableList<String>, String> stopCol = new TableColumn<>("№ остановки");
        stopCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        stopCol.setPrefWidth(100);
        TableColumn<ObservableList<String>, String> depCityCol = new TableColumn<>("Город отправления");
        depCityCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        depCityCol.setPrefWidth(180);
        TableColumn<ObservableList<String>, String> arrCityCol = new TableColumn<>("Город прибытия");
        arrCityCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        arrCityCol.setPrefWidth(180);
        routeTable.getColumns().add(stopCol);
        routeTable.getColumns().add(depCityCol);
        routeTable.getColumns().add(arrCityCol);

        searchBtn.setOnAction(_ -> {
            String voyageId = voyageIdField.getText().trim();
            if (voyageId.isEmpty() || !voyageId.matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный ID рейса!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT v.id AS voyage_id, v.vessel_id, ves.name AS vessel_name, v.status AS voyage_status, "
                        +
                        "vs.stop_number, dp.un_locode AS departure_port_code, dp.name AS departure_port_name, " +
                        "dp.city AS departure_port_city, dp.country AS departure_port_country, vs.departure_datetime, "
                        +
                        "ap.un_locode AS arrival_port_code, ap.name AS arrival_port_name, ap.city AS arrival_port_city, "
                        +
                        "ap.country AS arrival_port_country, vs.arrival_datetime " +
                        "FROM maritime_booking.voyages v " +
                        "JOIN maritime_booking.vessels ves ON v.vessel_id = ves.imo " +
                        "JOIN maritime_booking.voyage_stages vs ON v.id = vs.voyage_id AND v.vessel_id = vs.vessel_id "
                        +
                        "JOIN maritime_booking.ports dp ON vs.departure_port_id = dp.un_locode " +
                        "JOIN maritime_booking.ports ap ON vs.arrival_port_id = ap.un_locode " +
                        "WHERE v.id = ? " +
                        "ORDER BY v.id, vs.stop_number";
                var ps = connection.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(voyageId));
                var rs = ps.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("voyage_id"));
                    row.add(rs.getString("vessel_id"));
                    row.add(rs.getString("vessel_name"));
                    row.add(rs.getString("voyage_status"));
                    row.add(rs.getString("stop_number"));
                    row.add(rs.getString("departure_port_code"));
                    row.add(rs.getString("departure_port_name"));
                    row.add(rs.getString("departure_port_city"));
                    row.add(rs.getString("departure_port_country"));
                    row.add(rs.getString("departure_datetime"));
                    row.add(rs.getString("arrival_port_code"));
                    row.add(rs.getString("arrival_port_name"));
                    row.add(rs.getString("arrival_port_city"));
                    row.add(rs.getString("arrival_port_country"));
                    row.add(rs.getString("arrival_datetime"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }

            // Загружаем маршрут по городам
            ObservableList<ObservableList<String>> routeData = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT vs.stop_number, dp.city AS departure_city, ap.city AS arrival_city\n" +
                        "FROM maritime_booking.voyage_stages vs\n" +
                        "JOIN maritime_booking.ports dp ON vs.departure_port_id = dp.un_locode\n" +
                        "JOIN maritime_booking.ports ap ON vs.arrival_port_id = ap.un_locode\n" +
                        "WHERE vs.voyage_id = ?\n" +
                        "ORDER BY vs.stop_number";
                var ps = connection.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(voyageId));
                var rs = ps.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("stop_number"));
                    row.add(rs.getString("departure_city"));
                    row.add(rs.getString("arrival_city"));
                    routeData.add(row);
                }
                routeTable.setItems(routeData);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить маршрут: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, label, voyageIdField, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table, routeLabel, routeTable);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createTicketSalesTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Продажи билетов");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label yearLabel = new Label("Введите год:");
        TextField yearField = new TextField("2024");
        yearField.setPromptText("Год");
        Button searchBtn = new Button("Показать продажи");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> monthCol = new TableColumn<>("Месяц");
        monthCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        monthCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> depCol = new TableColumn<>("Страна отправления");
        depCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        depCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> arrCol = new TableColumn<>("Страна прибытия");
        arrCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        arrCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> cntCol = new TableColumn<>("Кол-во билетов");
        cntCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));
        cntCol.setPrefWidth(100);
        TableColumn<ObservableList<String>, String> revCol = new TableColumn<>("Выручка");
        revCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(4)));
        revCol.setPrefWidth(100);
        TableColumn<ObservableList<String>, String> avgCol = new TableColumn<>("Средний чек");
        avgCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(5)));
        avgCol.setPrefWidth(100);
        TableColumn<ObservableList<String>, String> insCol = new TableColumn<>("% страховки");
        insCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(6)));
        insCol.setPrefWidth(100);
        table.getColumns().add(monthCol);
        table.getColumns().add(depCol);
        table.getColumns().add(arrCol);
        table.getColumns().add(cntCol);
        table.getColumns().add(revCol);
        table.getColumns().add(avgCol);
        table.getColumns().add(insCol);
        searchBtn.setOnAction(_ -> {
            String year = yearField.getText().trim();
            if (year.isEmpty() || !year.matches("\\d{4}")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный год!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT EXTRACT(MONTH FROM t.purchase_date) AS month, p1.country AS departure_country, p2.country AS arrival_country, COUNT(t.id) AS ticket_count, ROUND(SUM(t.price),0) AS total_revenue, ROUND(AVG(t.price), 0) AS avg_price, ROUND(SUM(CASE WHEN t.insurance = true THEN 1 ELSE 0 END) * 100.0 / COUNT(t.id), 0) AS insurance_percentage\n"
                        +
                        "FROM maritime_booking.tickets t\n" +
                        "JOIN maritime_booking.voyages v ON t.voyage_id = v.id AND t.vessel_id = v.vessel_id\n" +
                        "JOIN maritime_booking.voyage_stages vs ON v.id = vs.voyage_id AND v.vessel_id = vs.vessel_id\n"
                        +
                        "JOIN maritime_booking.ports p1 ON vs.departure_port_id = p1.un_locode\n" +
                        "JOIN maritime_booking.ports p2 ON vs.arrival_port_id = p2.un_locode\n" +
                        "WHERE t.purchase_date BETWEEN CAST(? AS date) AND CAST(? AS date)\n" +
                        "AND vs.stop_number = 1\n" +
                        "GROUP BY EXTRACT(MONTH FROM t.purchase_date), p1.country, p2.country\n" +
                        "ORDER BY total_revenue DESC";
                var ps = connection.prepareStatement(sql);
                ps.setString(1, year + "-01-01");
                ps.setString(2, year + "-12-31");
                var rs = ps.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= 7; i++) {
                        row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, yearLabel, yearField, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createAvgCheckTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Средний чек по клиентам");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Средний чек и количество билетов по клиентам:");
        Button searchBtn = new Button("Показать средний чек");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        emailCol.setPrefWidth(200);
        TableColumn<ObservableList<String>, String> cntCol = new TableColumn<>("Кол-во билетов");
        cntCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        cntCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> avgCol = new TableColumn<>("Средний чек");
        avgCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        avgCol.setPrefWidth(120);
        table.getColumns().add(emailCol);
        table.getColumns().add(cntCol);
        table.getColumns().add(avgCol);
        searchBtn.setOnAction(_ -> {
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT c.email, COUNT(t.id) AS tickets_cnt, AVG(t.price) AS avg_price\n" +
                        "FROM maritime_booking.customers c\n" +
                        "JOIN maritime_booking.tickets t ON c.email = t.email\n" +
                        "GROUP BY c.email\n" +
                        "ORDER BY avg_price DESC";
                var stmt = connection.createStatement();
                var rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("email"));
                    row.add(rs.getString("tickets_cnt"));
                    row.add(rs.getString("avg_price"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, label, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createTotalRevenueTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Выручка по маршрутам");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Общая выручка по маршрутам:");
        Button searchBtn = new Button("Показать выручку");
        searchBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> depCol = new TableColumn<>("Порт отправления");
        depCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        depCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> arrCol = new TableColumn<>("Порт прибытия");
        arrCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        arrCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> revCol = new TableColumn<>("Выручка");
        revCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        revCol.setPrefWidth(120);
        table.getColumns().add(depCol);
        table.getColumns().add(arrCol);
        table.getColumns().add(revCol);
        searchBtn.setOnAction(_ -> {
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT vs.departure_port_id, vs.arrival_port_id, SUM(t.price) AS total_revenue\n" +
                        "FROM maritime_booking.voyage_stages vs\n" +
                        "JOIN maritime_booking.tickets t ON vs.voyage_id = t.voyage_id AND vs.vessel_id = t.vessel_id\n"
                        +
                        "GROUP BY vs.departure_port_id, vs.arrival_port_id\n" +
                        "ORDER BY total_revenue DESC";
                var stmt = connection.createStatement();
                var rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("departure_port_id"));
                    row.add(rs.getString("arrival_port_id"));
                    row.add(rs.getString("total_revenue"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });
        VBox form = new VBox(10, label, searchBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);
        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
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

    private void showTable(String sql, TableView<ObservableList<String>> table) {
        table.getColumns().clear();
        table.getItems().clear();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery(sql);
            var rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rsmd.getColumnName(i));
                col.setCellValueFactory(
                        data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(colIndex)));
                table.getColumns().add(col);
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                table.getItems().add(row);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить таблицу: " + e.getMessage());
        }
    }

    private ResultSet getTicketsForYearWithPrice(Connection connection, int year, double price) throws SQLException {
        return connection.createStatement().executeQuery(
                "SELECT * FROM maritime_booking.tickets WHERE purchase_date BETWEEN    '" + year + "-01-01' AND '"
                        + year + "-12-31' AND price <= " + price + ";");
    }

    private void buyTicket(Connection connection, String email, int voyageId, String vesselId, int cabinId,
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

    private void addClient(Connection connection, String lastName, String firstName, String middleName,
            long passportSeries, java.sql.Date birthDate, String email) throws SQLException {
        if (lastName == null || lastName.isBlank() || firstName == null || firstName.isBlank() ||
                passportSeries <= 0 || birthDate == null || email == null || email.isBlank()) {
            throw new SQLException("Invalid input parameters.");
        }
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO maritime_booking.customers (email, last_name, first_name, middle_name, birth_date, passport_series) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING email",
                java.sql.Statement.RETURN_GENERATED_KEYS);
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
}