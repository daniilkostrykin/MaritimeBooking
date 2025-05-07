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

        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);

        tabPane.setStyle("-fx-tab-min-width: 100px; -fx-tab-max-width: 200px; -fx-tab-min-height: 30px;");

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

        Tab insuredTicketsFromCountryTab = new Tab("Билеты со страховкой по стране");
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

        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Тёмная", "Светлая");
        themeCombo.setValue("Тёмная");

        HBox topBar = new HBox(themeCombo);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(8, 16, 8, 8));
        topBar.setSpacing(10);
        topBar.setStyle("-fx-background-color: #222;");

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());

        themeCombo.setOnAction(_ -> {
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

        Label voyageLabel = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("ID рейса");

        Label vesselLabel = new Label("Выберите судно:");
        ComboBox<String> vesselCombo = new ComboBox<>();
        vesselCombo.setPromptText("IMO судна");

        Label cabinLabel = new Label("Выберите каюту:");
        ComboBox<String> cabinCombo = new ComboBox<>();
        cabinCombo.setPromptText("ID каюты");

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
        luggageField.setPromptText("Вес багажа");
        TextField purchaseDateField = new TextField();
        purchaseDateField.setPromptText("Дата покупки (ГГГГ-ММ-ДД)");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var rs = JDBCManager.getActiveVoyages(connection);
            while (rs.next()) {
                voyageCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") + ")");
            }

            rs = JDBCManager.getVessels(connection);
            while (rs.next()) {
                vesselCombo.getItems().add(rs.getString("imo") + " (" + rs.getString("name") + ")");
            }

            rs = JDBCManager.getCabins(connection);
            while (rs.next()) {
                cabinCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                        ", категория: " + rs.getString("category") +
                        ", вместимость: " + rs.getString("capacity") +
                        ", окно: " + (rs.getBoolean("window_view") ? "да" : "нет") + ")");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }

        voyageCombo.setOnAction(_ -> {
            if (voyageCombo.getValue() != null) {
                String vesselId = voyageCombo.getValue().split("IMO: ")[1].replace(")", "");
                vesselCombo.setValue(vesselCombo.getItems().stream()
                        .filter(item -> item.startsWith(vesselId))
                        .findFirst()
                        .orElse(null));
            }
        });

        vesselCombo.setOnAction(e -> {
            if (vesselCombo.getValue() != null) {
                String vesselId = vesselCombo.getValue().split(" ")[0];
                cabinCombo.setItems(FXCollections.observableArrayList(
                        cabinCombo.getItems().stream()
                                .filter(item -> item.contains("IMO: " + vesselId))
                                .collect(java.util.stream.Collectors.toList())));
            }
        });

        Button buyTicketBtn = new Button("Купить билет");

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

            if (voyageCombo.getValue() == null) {
                valid = false;
                errors.append("Выберите рейс. ");
                voyageCombo.setStyle("-fx-border-color: red;");
            } else
                voyageCombo.setStyle("");

            if (vesselCombo.getValue() == null) {
                valid = false;
                errors.append("Выберите судно. ");
                vesselCombo.setStyle("-fx-border-color: red;");
            } else
                vesselCombo.setStyle("");

            if (cabinCombo.getValue() == null) {
                valid = false;
                errors.append("Выберите каюту. ");
                cabinCombo.setStyle("-fx-border-color: red;");
            } else
                cabinCombo.setStyle("");

            if (priceField.getText().isBlank() || !priceField.getText().matches("\\d+(\\.\\d+)?")) {
                valid = false;
                errors.append("Price must be a number. ");
                priceField.setStyle("-fx-border-color: red;");
            } else
                priceField.setStyle("");

            if (paymentMethodCombo.getValue() == null) {
                valid = false;
                errors.append("Выберите способ оплаты. ");
                paymentMethodCombo.setStyle("-fx-border-color: red;");
            } else
                paymentMethodCombo.setStyle("");

            if (mealTypeCombo.getValue() == null) {
                valid = false;
                errors.append("Выберите тип питания. ");
                mealTypeCombo.setStyle("-fx-border-color: red;");
            } else
                mealTypeCombo.setStyle("");

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
                String voyageId = voyageCombo.getValue().split(" ")[0];
                String vesselId = vesselCombo.getValue().split(" ")[0];
                String cabinId = cabinCombo.getValue().split(" ")[0];

                JDBCManager.buyTicket(connection,
                        emailField.getText(),
                        Integer.parseInt(voyageId),
                        vesselId,
                        Integer.parseInt(cabinId),
                        Double.parseDouble(priceField.getText()),
                        paymentMethodCombo.getValue(),
                        mealTypeCombo.getValue(),
                        insuranceCheck.isSelected(),
                        Double.parseDouble(luggageField.getText()),
                        purchaseDateField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket purchased successfully!");
                emailField.clear();
                voyageCombo.setValue(null);
                vesselCombo.setValue(null);
                cabinCombo.setValue(null);
                priceField.clear();
                paymentMethodCombo.setValue(null);
                mealTypeCombo.setValue(null);
                insuranceCheck.setSelected(false);
                luggageField.clear();
                purchaseDateField.clear();
            } catch (SQLException ex1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to purchase ticket: " + ex1.getMessage());
            }
        });

        VBox form = new VBox(10,
                emailField,
                voyageLabel, voyageCombo,
                vesselLabel, vesselCombo,
                cabinLabel, cabinCombo,
                priceField,
                paymentMethodCombo,
                mealTypeCombo,
                insuranceCheck,
                luggageField,
                purchaseDateField,
                errorLabel,
                buyTicketBtn);
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

        Label yearLabel = new Label("Выберите год:");
        ComboBox<String> yearCombo = new ComboBox<>();
        yearCombo.setPromptText("Год");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var rs = JDBCManager.getAvailableYears(connection);
            while (rs.next()) {
                yearCombo.getItems().add(rs.getString("year"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список лет: " + e.getMessage());
        }

        TextField priceField = new TextField();
        priceField.setPromptText("Максимальная цена");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button filterBtn = new Button("Показать билеты");

        TableView<ObservableList<String>> table = new TableView<>();
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
            String year = yearCombo.getValue();
            String maxPrice = priceField.getText().trim();
            if (year == null || maxPrice.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите год и введите максимальную цену!");
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getTicketsForYearWithPrice(connection, Integer.parseInt(year),
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

        VBox form = new VBox(10, yearLabel, yearCombo, priceField, errorLabel, filterBtn);
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
            if (!valid) {
                errorLabel.setText(errors.toString());
                return;
            }
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                JDBCManager.addClient(connection,
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

        Label label = new Label("Выберите клиента:");
        ComboBox<String> emailCombo = new ComboBox<>();
        emailCombo.setPromptText("Email клиента");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var rs = JDBCManager.getCustomersWithTickets(connection);
            while (rs.next()) {
                emailCombo.getItems().add(rs.getString("email") +
                        " (" + rs.getString("last_name") + " " + rs.getString("first_name") + ")");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список клиентов: " + e.getMessage());
        }

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(60);
        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        priceCol.setPrefWidth(100);
        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);

        emailCombo.setOnAction(_ -> {
            if (emailCombo.getValue() == null) {
                return;
            }

            String email = emailCombo.getValue().split(" \\(")[0];
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getCustomerTickets(connection, email);
                while (rs.next()) {
                    data.add(FXCollections.observableArrayList(rs.getString("id"), rs.getString("price")));
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, label, emailCombo);
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
        mealTypeCombo.getItems().addAll("no_meals", "breakfast", "half_board", "full_board", "all_inclusive",
                "ultra_all_inclusive");
        mealTypeCombo.setPromptText("Тип питания");

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        emailCol.setPrefWidth(200);
        TableColumn<ObservableList<String>, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        nameCol.setPrefWidth(150);
        table.getColumns().add(emailCol);
        table.getColumns().add(nameCol);

        mealTypeCombo.setOnAction(_ -> {
            String mealType = mealTypeCombo.getValue();
            if (mealType == null) {
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT customers.email, customers.first_name\n" +
                        "FROM maritime_booking.customers\n" +
                        "JOIN maritime_booking.tickets ON customers.email = tickets.email\n" +
                        "WHERE tickets.meal_type = ?\n" +
                        "ORDER BY customers.last_name, customers.first_name";
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

        VBox form = new VBox(10, label, mealTypeCombo);
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

        Label title = new Label("Рейсы по статусу и питанию");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Выберите статус рейса:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("active", "delayed", "completed", "cancelled", "postponed", "in_progress");
        statusCombo.setPromptText("Статус рейса");

        Label mealLabel = new Label("Выберите тип питания:");
        ComboBox<String> mealCombo = new ComboBox<>();
        mealCombo.getItems().addAll("no_meals", "breakfast", "half_board", "full_board", "all_inclusive",
                "ultra_all_inclusive");
        mealCombo.setPromptText("Тип питания");

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);
        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        statusCol.setPrefWidth(120);
        table.getColumns().add(idCol);
        table.getColumns().add(statusCol);

        Runnable updateTable = () -> {
            String status = statusCombo.getValue();
            String meal = mealCombo.getValue();
            if (status == null || meal == null) {
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                String sql = "SELECT DISTINCT voyages.id, voyages.status\n" +
                        "FROM maritime_booking.voyages\n" +
                        "JOIN maritime_booking.tickets ON tickets.voyage_id = voyages.id AND tickets.vessel_id = voyages.vessel_id\n"
                        +
                        "JOIN maritime_booking.customers ON tickets.email = customers.email\n" +
                        "WHERE voyages.status = ? AND tickets.meal_type = ?\n" +
                        "ORDER BY voyages.id";
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
        };

        statusCombo.setOnAction(_ -> updateTable.run());
        mealCombo.setOnAction(_ -> updateTable.run());

        VBox form = new VBox(10, statusLabel, statusCombo, mealLabel, mealCombo);
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

        Label title = new Label("Билеты со страховкой по стране");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Выберите страну:");
        ComboBox<String> countryCombo = new ComboBox<>();
        countryCombo.setPromptText("Страна");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var rs = JDBCManager.getCountriesWithInsuredTickets(connection);
            while (rs.next()) {
                countryCombo.getItems().add(rs.getString("country"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список стран: " + e.getMessage());
        }

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(80);
        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        priceCol.setPrefWidth(120);
        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);

        countryCombo.setOnAction(_ -> {
            String country = countryCombo.getValue();
            if (country == null) {
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
                        "JOIN maritime_booking.customers ON tickets.email = customers.email\n" +
                        "WHERE ports.country = ? AND tickets.insurance = true AND voyage_stages.stop_number = 1\n" +
                        "ORDER BY tickets.id";
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

        VBox form = new VBox(10, label, countryCombo);
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

        Label voyageLabel = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("ID рейса");

        Label vesselLabel = new Label("Выберите судно:");
        ComboBox<String> vesselCombo = new ComboBox<>();
        vesselCombo.setPromptText("IMO судна");

        Label cabinLabel = new Label("Выберите каюту:");
        ComboBox<String> cabinCombo = new ComboBox<>();
        cabinCombo.setPromptText("ID каюты");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var rs = JDBCManager.getActiveVoyages(connection);
            while (rs.next()) {
                voyageCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") + ")");
            }

            rs = JDBCManager.getVessels(connection);
            while (rs.next()) {
                vesselCombo.getItems().add(rs.getString("imo") + " (" + rs.getString("name") + ")");
            }

            rs = JDBCManager.getCabins(connection);
            while (rs.next()) {
                cabinCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                        ", категория: " + rs.getString("category") +
                        ", вместимость: " + rs.getString("capacity") +
                        ", окно: " + (rs.getBoolean("window_view") ? "да" : "нет") + ")");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }

        voyageCombo.setOnAction(_ -> {
            if (voyageCombo.getValue() != null) {
                String vesselId = voyageCombo.getValue().split("IMO: ")[1].replace(")", "");
                vesselCombo.setValue(vesselCombo.getItems().stream()
                        .filter(item -> item.startsWith(vesselId))
                        .findFirst()
                        .orElse(null));

                cabinCombo.setItems(FXCollections.observableArrayList(
                        cabinCombo.getItems().stream()
                                .filter(item -> item.contains("IMO: " + vesselId))
                                .collect(java.util.stream.Collectors.toList())));

                if (!cabinCombo.getItems().isEmpty()) {
                    cabinCombo.setValue(cabinCombo.getItems().get(0));
                }
            }
        });

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
        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(250);
        addBtn.setOnAction(_ -> {
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
                    String sql2 = "INSERT INTO maritime_booking.tickets (email, voyage_id, vessel_id, cabin_id, price, payment_method, meal_type, insurance, luggage_weight, purchase_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
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
                    ResultSet rs = ps2.executeQuery();
                    if (rs.next()) {
                        System.out.println("Inserted ticket with ID " + rs.getInt(1));
                    }
                    connection.commit();
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент и билет успешно добавлены!");
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

        Label label = new Label("Выберите рейс для удаления:");

        Label voyageLabel = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("ID рейса");

        Label vesselLabel = new Label("Выберите судно:");
        ComboBox<String> vesselCombo = new ComboBox<>();
        vesselCombo.setPromptText("IMO судна");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var rs = JDBCManager.getVoyagesWithVessels(connection);
            while (rs.next()) {
                voyageCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                        " - " + rs.getString("name") + ")");
            }

            rs = JDBCManager.getVessels(connection);
            while (rs.next()) {
                vesselCombo.getItems().add(rs.getString("imo") + " (" + rs.getString("name") + ")");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }

        voyageCombo.setOnAction(e -> {
            if (voyageCombo.getValue() != null) {
                String vesselId = voyageCombo.getValue().split("IMO: ")[1].split(" -")[0];
                vesselCombo.setValue(vesselCombo.getItems().stream()
                        .filter(item -> item.startsWith(vesselId))
                        .findFirst()
                        .orElse(null));
            }
        });

        Button deleteBtn = new Button("Удалить всё связанное");
        deleteBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #E53935; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(200);

        deleteBtn.setOnAction(_ -> {
            if (voyageCombo.getValue() == null || vesselCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите рейс и судно!");
                return;
            }

            String voyageId = voyageCombo.getValue().split(" ")[0];
            String vesselId = vesselCombo.getValue().split(" ")[0];

            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                JDBCManager.deleteVoyage(connection, Integer.parseInt(voyageId), vesselId);
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Рейс и всё связанное удалено!");

                voyageCombo.setValue(null);
                vesselCombo.setValue(null);

                voyageCombo.getItems().clear();
                var rs = JDBCManager.getVoyagesWithVessels(connection);
                while (rs.next()) {
                    voyageCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                            " - " + rs.getString("name") + ")");
                }

                showTable("SELECT * FROM maritime_booking.voyages", table);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при удалении: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, label, voyageLabel, voyageCombo, vesselLabel, vesselCombo, deleteBtn);
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
        TextField weightField = new TextField();
        weightField.setPromptText("Вес");
        Label dateLabel = new Label("Дата до (YYYY-MM-DD):");
        TextField dateField = new TextField();
        dateField.setPromptText("Дата");
        Button updateBtn = new Button("Обновить цены");
        TableView<ObservableList<String>> table = new TableView<>();
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
                JDBCManager.updateLuggagePrice(connection, Double.parseDouble(minWeight), date);
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

        Label label = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("Рейс");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            String sql = "SELECT v.id, v.vessel_id, vs.name as vessel_name, v.status " +
                    "FROM maritime_booking.voyages v " +
                    "JOIN maritime_booking.vessels vs ON v.vessel_id = vs.imo " +
                    "ORDER BY v.id DESC";

            var rs = connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                voyageCombo.getItems().add(String.format("%s (Судно: %s - %s, Статус: %s)",
                        rs.getString("id"),
                        rs.getString("vessel_id"),
                        rs.getString("vessel_name"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список рейсов: " + e.getMessage());
        }

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);

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

        Label routeLabel = new Label("Маршрут по городам:");
        TableView<ObservableList<String>> routeTable = new TableView<>();
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

        voyageCombo.setOnAction(_ -> {
            if (voyageCombo.getValue() == null) {
                return;
            }

            String voyageId = voyageCombo.getValue().split(" ")[0];
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getVoyageRoute(connection, Integer.parseInt(voyageId));
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

            ObservableList<ObservableList<String>> routeData = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getVoyageCities(connection, Integer.parseInt(voyageId));
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

        VBox form = new VBox(10, label, voyageCombo);
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

        Label yearLabel = new Label("Выберите год:");
        ComboBox<String> yearCombo = new ComboBox<>();
        yearCombo.setPromptText("Год");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            var rs = JDBCManager.getAvailableYears(connection);
            while (rs.next()) {
                yearCombo.getItems().add(rs.getString("year"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список лет: " + e.getMessage());
        }

        TableView<ObservableList<String>> table = new TableView<>();
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

        yearCombo.setOnAction(_ -> {
            String year = yearCombo.getValue();
            if (year == null) {
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getTicketSales(connection, year);
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

        VBox form = new VBox(10, yearLabel, yearCombo);
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
        TableView<ObservableList<String>> table = new TableView<>();
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
                var rs = JDBCManager.getAverageCheck(connection);
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
        TableView<ObservableList<String>> table = new TableView<>();
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
                var rs = JDBCManager.getTotalRevenue(connection);
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
}