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
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import javafx.scene.control.cell.PropertyValueFactory;

public class MaritimeBookingApp extends Application {
    private static final String PROTOCOL = "jdbc:postgresql://";
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

        Tab completedVoyagesTab = new Tab("Завершённые рейсы с BREAKFAST");
        completedVoyagesTab.setClosable(false);
        completedVoyagesTab.setContent(createCompletedVoyagesTab());

        Tab insuredTicketsFromCountryTab = new Tab("Билеты с страховкой из страны");
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

        Tab ticketSalesTab = new Tab("Продажи билетов 2024");
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

        Scene scene = new Scene(tabPane, 800, 600);
        // Подключаем тёмную тему
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private VBox createTicketsForYearWithPriceTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        // Фильтры
        TextField yearField = new TextField();
        yearField.setPromptText("Год (например, 2024)");
        TextField priceField = new TextField();
        priceField.setPromptText("Максимальная цена");

        Button filterBtn = new Button("Показать билеты");
        filterBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        TableView<Ticket> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Ticket, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);

        filterBtn.setOnAction(e -> {
            String year = yearField.getText().trim();
            String maxPrice = priceField.getText().trim();
            if (year.isEmpty() || maxPrice.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите год и максимальную цену!");
                return;
            }
            ObservableList<Ticket> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = jdbcRunner.getTicketsForYearWithPrice(connection, Integer.parseInt(year),
                        Double.parseDouble(maxPrice));
                while (rs.next()) {
                    data.add(new Ticket(
                            rs.getInt("id"),
                            rs.getDouble("price")));
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(
                new Label("Фильтр по году и максимальной цене:"),
                yearField, priceField, filterBtn, table);
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

    private VBox createTicketsByClientTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label label = new Label("Введите email клиента:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email клиента");
        Button searchBtn = new Button("Показать билеты");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        TableView<Ticket> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        TableColumn<Ticket, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);
        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);

        searchBtn.setOnAction(e -> {
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
            ObservableList<Ticket> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT tickets.id, tickets.price FROM maritime_booking.tickets JOIN maritime_booking.customers ON tickets.email = customers.email WHERE customers.email = ? ORDER BY tickets.id";
                var ps = connection.prepareStatement(sql);
                ps.setString(1, email);
                var rs = ps.executeQuery();
                while (rs.next()) {
                    data.add(new Ticket(rs.getInt("id"), rs.getDouble("price")));
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(label, emailField, searchBtn, table);
        return vbox;
    }

    private VBox createClientsByMealTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label label = new Label("Выберите тип питания:");
        ComboBox<String> mealTypeCombo = new ComboBox<>();
        mealTypeCombo.getItems().addAll("full_board", "breakfast", "half_board", "no_meals");
        mealTypeCombo.setPromptText("Тип питания");
        Button searchBtn = new Button("Показать клиентов");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        emailCol.setPrefWidth(200);
        TableColumn<ObservableList<String>, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        nameCol.setPrefWidth(150);
        table.getColumns().add(emailCol);
        table.getColumns().add(nameCol);

        searchBtn.setOnAction(e -> {
            String mealType = mealTypeCombo.getValue();
            if (mealType == null || mealType.isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите тип питания!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT customers.email, customers.first_name FROM maritime_booking.customers JOIN maritime_booking.tickets ON customers.email = tickets.email WHERE tickets.meal_type = ?";
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

        vbox.getChildren().addAll(label, mealTypeCombo, searchBtn, table);
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

        tablesCombo.setOnAction(event -> {
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
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label statusLabel = new Label("Выберите статус рейса:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("completed", "active", "cancelled");
        statusCombo.setPromptText("Статус рейса");

        Label mealLabel = new Label("Выберите тип питания:");
        ComboBox<String> mealCombo = new ComboBox<>();
        mealCombo.getItems().addAll("full_board", "breakfast", "half_board", "no_meals");
        mealCombo.setPromptText("Тип питания");

        Button searchBtn = new Button("Показать рейсы");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        statusCol.setPrefWidth(120);
        table.getColumns().add(idCol);
        table.getColumns().add(statusCol);
        searchBtn.setOnAction(e -> {
            String status = statusCombo.getValue();
            String meal = mealCombo.getValue();
            if (status == null || meal == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите статус рейса и тип питания!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT DISTINCT voyages.id, voyages.status FROM maritime_booking.voyages JOIN maritime_booking.tickets ON tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id JOIN maritime_booking.customers ON tickets.email = customers.email WHERE voyages.status = ? AND tickets.meal_type = ?";
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
        vbox.getChildren().addAll(statusLabel, statusCombo, mealLabel, mealCombo, searchBtn, table);
        return vbox;
    }

    private VBox createInsuredTicketsFromCountryTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        Label label = new Label("Введите страну (на англ.):");
        TextField countryField = new TextField();
        countryField.setPromptText("Страна");
        Button searchBtn = new Button("Показать билеты");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        priceCol.setPrefWidth(120);
        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);
        searchBtn.setOnAction(e -> {
            String country = countryField.getText().trim();
            if (country.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите страну!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT tickets.id, tickets.price FROM maritime_booking.tickets JOIN maritime_booking.voyages ON tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id JOIN maritime_booking.voyage_stages ON voyage_stages.voyage_id = voyages.id AND voyage_stages.vessel_id = voyages.vessel_id JOIN maritime_booking.ports ON voyage_stages.departure_port_id = ports.un_locode WHERE ports.country = ? AND tickets.insurance = true AND voyage_stages.stop_number = 1";
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
        vbox.getChildren().addAll(label, countryField, searchBtn, table);
        return vbox;
    }

    private javafx.scene.Node createAddClientAndTicketTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
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
        birthDateField.setPromptText("Дата рождения (YYYY-MM-DD)");
        TextField passportField = new TextField();
        passportField.setPromptText("Паспорт");
        // Билет
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
        ComboBox<String> mealTypeCombo = new ComboBox<>();
        mealTypeCombo.getItems().addAll("full_board", "breakfast", "half_board", "no_meals");
        mealTypeCombo.setPromptText("Тип питания");
        CheckBox insuranceCheck = new CheckBox("Страховка");
        TextField luggageField = new TextField();
        luggageField.setPromptText("Вес багажа");
        TextField purchaseDateField = new TextField();
        purchaseDateField.setPromptText("Дата покупки (YYYY-MM-DD)");
        Button addBtn = new Button("Добавить клиента и билет");
        addBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(250);
        addBtn.setOnAction(e -> {
            // Валидация
            if (emailField.getText().isBlank() || !emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный email!");
                return;
            }
            if (lastNameField.getText().isBlank() || firstNameField.getText().isBlank()
                    || passportField.getText().isBlank() || !passportField.getText().matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Заполните ФИО и паспорт!");
                return;
            }
            if (birthDateField.getText().isBlank() || !birthDateField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Дата рождения должна быть в формате YYYY-MM-DD!");
                return;
            }
            if (voyageIdField.getText().isBlank() || !voyageIdField.getText().matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "ID рейса должен быть числом!");
                return;
            }
            if (vesselIdField.getText().isBlank() || cabinIdField.getText().isBlank()
                    || !cabinIdField.getText().matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "ID судна и ID каюты обязательны!");
                return;
            }
            if (priceField.getText().isBlank() || !priceField.getText().matches("\\d+(\\.\\d+)?")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Цена должна быть числом!");
                return;
            }
            if (paymentMethodField.getText().isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Укажите способ оплаты!");
                return;
            }
            if (mealTypeCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите тип питания!");
                return;
            }
            if (luggageField.getText().isBlank() || !luggageField.getText().matches("\\d+(\\.\\d+)?")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Вес багажа должен быть числом!");
                return;
            }
            if (purchaseDateField.getText().isBlank() || !purchaseDateField.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Дата покупки должна быть в формате YYYY-MM-DD!");
                return;
            }
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                connection.setAutoCommit(false);
                try {
                    String sql1 = "INSERT INTO maritime_booking.customers (email, last_name, first_name, middle_name, birth_date, passport_series) VALUES (?, ?, ?, ?, ?, ?)";
                    var ps1 = connection.prepareStatement(sql1);
                    ps1.setString(1, emailField.getText());
                    ps1.setString(2, lastNameField.getText());
                    ps1.setString(3, firstNameField.getText());
                    ps1.setString(4, middleNameField.getText());
                    ps1.setString(5, birthDateField.getText());
                    ps1.setString(6, passportField.getText());
                    ps1.executeUpdate();
                    String sql2 = "INSERT INTO maritime_booking.tickets (email, voyage_id, vessel_id, cabin_id, price, payment_method, meal_type, insurance, luggage_weight, purchase_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    var ps2 = connection.prepareStatement(sql2);
                    ps2.setString(1, emailField.getText());
                    ps2.setInt(2, Integer.parseInt(voyageIdField.getText()));
                    ps2.setString(3, vesselIdField.getText());
                    ps2.setInt(4, Integer.parseInt(cabinIdField.getText()));
                    ps2.setDouble(5, Double.parseDouble(priceField.getText()));
                    ps2.setString(6, paymentMethodField.getText());
                    ps2.setString(7, mealTypeCombo.getValue());
                    ps2.setBoolean(8, insuranceCheck.isSelected());
                    ps2.setDouble(9, Double.parseDouble(luggageField.getText()));
                    ps2.setString(10, purchaseDateField.getText());
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
        vbox.getChildren().addAll(label, emailField, lastNameField, firstNameField, middleNameField, birthDateField,
                passportField, voyageIdField, vesselIdField, cabinIdField, priceField, paymentMethodField,
                mealTypeCombo, insuranceCheck, luggageField, purchaseDateField, addBtn, table);
        return wrapWithScroll(vbox);
    }

    private VBox createDeleteVoyageTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        Label label = new Label("Введите ID рейса и ID судна для удаления:");
        TextField voyageIdField = new TextField();
        voyageIdField.setPromptText("ID рейса");
        TextField vesselIdField = new TextField();
        vesselIdField.setPromptText("ID судна");
        Button deleteBtn = new Button("Удалить всё связанное");
        deleteBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #E53935; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        deleteBtn.setOnAction(e -> {
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
        vbox.getChildren().addAll(label, voyageIdField, vesselIdField, deleteBtn, table);
        return vbox;
    }

    private javafx.scene.Node createLuggagePriceUpdateTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        Label weightLabel = new Label("Минимальный вес багажа (кг):");
        TextField weightField = new TextField("15");
        weightField.setPromptText("Вес");
        Label dateLabel = new Label("Дата до (YYYY-MM-DD):");
        TextField dateField = new TextField("2025-04-16");
        dateField.setPromptText("Дата");
        Button updateBtn = new Button("Обновить цены");
        updateBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        updateBtn.setOnAction(e -> {
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
        vbox.getChildren().addAll(weightLabel, weightField, dateLabel, dateField, updateBtn, table);
        return wrapWithScroll(vbox);
    }

    private VBox createVoyageRouteTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        Label label = new Label("Введите ID рейса:");
        TextField voyageIdField = new TextField();
        voyageIdField.setPromptText("ID рейса");
        Button searchBtn = new Button("Показать маршрут");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        // Колонки (выводим только часть для примера)
        TableColumn<ObservableList<String>, String> voyageIdCol = new TableColumn<>("ID рейса");
        voyageIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        voyageIdCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> vesselCol = new TableColumn<>("Судно");
        vesselCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        vesselCol.setPrefWidth(100);
        TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));
        statusCol.setPrefWidth(100);
        table.getColumns().add(voyageIdCol);
        table.getColumns().add(vesselCol);
        table.getColumns().add(statusCol);
        searchBtn.setOnAction(e -> {
            String voyageId = voyageIdField.getText().trim();
            if (voyageId.isEmpty() || !voyageId.matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный ID рейса!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT v.id AS voyage_id, v.vessel_id, ves.name AS vessel_name, v.status AS voyage_status, vs.stop_number, dp.un_locode AS departure_port_code, dp.name AS departure_port_name, dp.city AS departure_port_city, dp.country AS departure_port_country, vs.departure_datetime, ap.un_locode AS arrival_port_code, ap.name AS arrival_port_name, ap.city AS arrival_port_city, ap.country AS arrival_port_country, vs.arrival_datetime, t.insurance FROM maritime_booking.voyages v JOIN maritime_booking.vessels ves ON v.vessel_id = ves.imo JOIN maritime_booking.voyage_stages vs ON v.id = vs.voyage_id AND v.vessel_id = vs.vessel_id JOIN maritime_booking.ports dp ON vs.departure_port_id = dp.un_locode JOIN maritime_booking.ports ap ON vs.arrival_port_id = ap.un_locode LEFT JOIN maritime_booking.tickets t ON v.id = t.voyage_id AND v.vessel_id = t.vessel_id WHERE v.id = ? ORDER BY v.id, vs.stop_number, t.id";
                var ps = connection.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(voyageId));
                var rs = ps.executeQuery();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("voyage_id"));
                    row.add(rs.getString("vessel_id"));
                    row.add(rs.getString("vessel_name"));
                    row.add(rs.getString("voyage_status"));
                    // ... можно добавить остальные поля по аналогии
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });
        vbox.getChildren().addAll(label, voyageIdField, searchBtn, table);
        return vbox;
    }

    private VBox createTicketSalesTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        Label yearLabel = new Label("Введите год:");
        TextField yearField = new TextField("2024");
        yearField.setPromptText("Год");
        Button searchBtn = new Button("Показать продажи");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        searchBtn.setOnAction(e -> {
            String year = yearField.getText().trim();
            if (year.isEmpty() || !year.matches("\\d{4}")) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный год!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT EXTRACT(MONTH FROM t.purchase_date) AS month, p1.country AS departure_country, p2.country AS arrival_country, COUNT(t.id) AS ticket_count, ROUND(SUM(t.price),0) AS total_revenue, ROUND(AVG(t.price), 0) AS avg_price, ROUND(SUM(CASE WHEN t.insurance = true THEN 1 ELSE 0 END) * 100.0 / COUNT(t.id), 0) AS insurance_percentage FROM maritime_booking.tickets t JOIN maritime_booking.voyages v ON t.voyage_id = v.id AND t.vessel_id = v.vessel_id JOIN maritime_booking.voyage_stages vs ON v.id = vs.voyage_id AND v.vessel_id = vs.vessel_id JOIN maritime_booking.ports p1 ON vs.departure_port_id = p1.un_locode JOIN maritime_booking.ports p2 ON vs.arrival_port_id = p2.un_locode WHERE t.purchase_date BETWEEN CAST(? AS date) AND CAST(? AS date) AND vs.stop_number = 1 GROUP BY EXTRACT(MONTH FROM t.purchase_date), p1.country, p2.country ORDER BY total_revenue DESC";
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
        vbox.getChildren().addAll(yearLabel, yearField, searchBtn, table);
        return vbox;
    }

    private VBox createAvgCheckTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        Button searchBtn = new Button("Показать средний чек");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        searchBtn.setOnAction(e -> {
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT c.email, COUNT(t.id) AS tickets_cnt, AVG(t.price) AS avg_price FROM maritime_booking.customers c JOIN maritime_booking.tickets t ON c.email = t.email GROUP BY c.email ORDER BY avg_price DESC";
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
        vbox.getChildren().addAll(new Label("Средний чек и количество билетов по клиентам:"), searchBtn, table);
        return vbox;
    }

    private VBox createTotalRevenueTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        Button searchBtn = new Button("Показать выручку");
        searchBtn.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        searchBtn.setOnAction(e -> {
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT vs.departure_port_id, vs.arrival_port_id, SUM(t.price) AS total_revenue FROM maritime_booking.voyage_stages vs JOIN maritime_booking.tickets t ON vs.voyage_id = t.voyage_id AND vs.vessel_id = t.vessel_id GROUP BY vs.departure_port_id, vs.arrival_port_id ORDER BY total_revenue DESC";
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
        vbox.getChildren().addAll(new Label("Общая выручка по маршрутам:"), searchBtn, table);
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
}