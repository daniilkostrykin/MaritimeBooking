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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import java.util.Optional;

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

        Label voyageLabel = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("ID рейса");

        Label vesselLabel = new Label("Судно:");
        ComboBox<String> vesselCombo = new ComboBox<>();
        vesselCombo.setPromptText("IMO судна");
        vesselCombo.setDisable(true);

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
        luggageField.setPromptText("Вес багажа (кг)");
        TextField purchaseDateField = new TextField();
        purchaseDateField.setPromptText("Дата покупки (ГГГГ-ММ-ДД)");

        // Замена обычного Label на VBox со списком ошибок
        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));

        // Сохраняем полный список кают для повторного использования
        ObservableList<String> allCabins = FXCollections.observableArrayList();

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
                String cabinItem = rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                        ", категория: " + rs.getString("category") +
                        ", вместимость: " + rs.getString("capacity") +
                        ", окно: " + (rs.getBoolean("window_view") ? "да" : "нет") + ")";
                allCabins.add(cabinItem);
            }

            // Устанавливаем полный список кают изначально
            cabinCombo.setItems(FXCollections.observableArrayList(allCabins));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }

        voyageCombo.setOnAction(e -> {
            if (voyageCombo.getValue() != null) {
                String vesselId = voyageCombo.getValue().split("IMO: ")[1].replace(")", "");

                // Находим соответствующее судно и устанавливаем его
                vesselCombo.setValue(vesselCombo.getItems().stream()
                        .filter(item -> item.startsWith(vesselId))
                        .findFirst()
                        .orElse(null));

                // Автоматическая подстановка каюты при выборе рейса
                if (vesselCombo.getValue() != null) {
                    String cabinVesselId = vesselCombo.getValue().split(" ")[0];
                    ObservableList<String> filteredCabins = FXCollections.observableArrayList(
                            allCabins.stream()
                                    .filter(item -> item.contains("IMO: " + cabinVesselId))
                                    .collect(java.util.stream.Collectors.toList()));

                    cabinCombo.setItems(filteredCabins);
                    if (!filteredCabins.isEmpty()) {
                        cabinCombo.setValue(filteredCabins.get(0));
                    }
                }
            }
        });

        // Установка сегодняшней даты по умолчанию
        purchaseDateField.setText(java.time.LocalDate.now().toString());

        Button buyTicketBtn = new Button("Купить билет");

        buyTicketBtn.setOnAction(e -> {
            // Очищаем предыдущие ошибки и стили
            errorBox.getChildren().clear();
            emailField.setStyle("");
            voyageCombo.setStyle("");
            vesselCombo.setStyle("");
            cabinCombo.setStyle("");
            priceField.setStyle("");
            paymentMethodCombo.setStyle("");
            mealTypeCombo.setStyle("");
            luggageField.setStyle("");
            purchaseDateField.setStyle("");

            // Список для ошибок
            java.util.List<String> errors = new java.util.ArrayList<>();

            // Валидация email
            if (!Validator.isNotEmpty(emailField.getText())) {
                errors.add("• Необходимо указать Email клиента");
                emailField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isValidEmail(emailField.getText())) {
                errors.add("• Неверный формат Email");
                emailField.setStyle("-fx-border-color: red;");
            }

            // Проверка существования клиента
            if (Validator.isValidEmail(emailField.getText())) {
                try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                    if (!JDBCManager.checkCustomerExists(connection, emailField.getText())) {
                        errors.add("• Клиент с таким Email не найден в базе");
                        emailField.setStyle("-fx-border-color: red;");
                    }
                } catch (SQLException ex) {
                    errors.add("• Ошибка проверки клиента: " + ex.getMessage());
                }
            }

            // Валидация рейса
            if (voyageCombo.getValue() == null) {
                errors.add("• Необходимо выбрать рейс");
                voyageCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация судна
            if (vesselCombo.getValue() == null) {
                errors.add("• Необходимо выбрать судно");
                vesselCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация каюты
            if (cabinCombo.getValue() == null) {
                errors.add("• Необходимо выбрать каюту");
                cabinCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация цены
            if (!Validator.isNumeric(priceField.getText())) {
                errors.add("• Цена должна быть числом");
                priceField.setStyle("-fx-border-color: red;");
            }

            // Валидация способа оплаты
            if (paymentMethodCombo.getValue() == null) {
                errors.add("• Необходимо выбрать способ оплаты");
                paymentMethodCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация типа питания
            if (mealTypeCombo.getValue() == null) {
                errors.add("• Необходимо выбрать тип питания");
                mealTypeCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация веса багажа
            if (!Validator.isNumeric(luggageField.getText())) {
                errors.add("• Вес багажа должен быть числом");
                luggageField.setStyle("-fx-border-color: red;");
            }

            // Валидация даты покупки
            if (!Validator.isValidDate(purchaseDateField.getText())) {
                errors.add("• Дата должна быть в формате ГГГГ-ММ-ДД");
                purchaseDateField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isNotFutureDate(purchaseDateField.getText())) {
                errors.add("• Дата покупки не может быть в будущем");
                purchaseDateField.setStyle("-fx-border-color: red;");
            }

            // Если есть ошибки, показываем их и прерываем выполнение
            if (!errors.isEmpty()) {
                for (String error : errors) {
                    Label errorLabel = new Label(error);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
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
                        Integer.parseInt(luggageField.getText()),
                        purchaseDateField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Билет успешно куплен!");

                // Очищаем поля после успешной покупки, но оставляем без изменений списки
                emailField.clear();
                priceField.clear();
                paymentMethodCombo.setValue(null);
                mealTypeCombo.setValue(null);
                insuranceCheck.setSelected(false);
                luggageField.clear();
                purchaseDateField.setText(java.time.LocalDate.now().toString());

                // Не сбрасываем выбор рейса, судна и каюты для удобства при повторной покупке
                // voyageCombo.setValue(null);
                // vesselCombo.setValue(null);
                // cabinCombo.setValue(null);
            } catch (SQLException ex1) {
                Label errorLabel = new Label("• " + ex1.getMessage());
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
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
                errorBox,
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

        filterBtn.setOnAction(e -> {
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
        passportField.setPromptText("Серия паспорта (10 цифр)");

        // Замена обычного Label на VBox со списком ошибок
        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));

        // Установка текущей даты в поле даты рождения
        birthDateField.setText(java.time.LocalDate.now().toString());

        Button addClientBtn = new Button("Добавить клиента");

        addClientBtn.setOnAction(e -> {
            // Очищаем предыдущие ошибки и стили
            errorBox.getChildren().clear();
            emailField.setStyle("");
            lastNameField.setStyle("");
            firstNameField.setStyle("");
            middleNameField.setStyle("");
            birthDateField.setStyle("");
            passportField.setStyle("");

            // Список для ошибок
            java.util.List<String> errors = new java.util.ArrayList<>();

            // Валидация email
            if (!Validator.isNotEmpty(emailField.getText())) {
                errors.add("• Необходимо указать Email");
                emailField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isValidEmail(emailField.getText())) {
                errors.add("• Неверный формат Email");
                emailField.setStyle("-fx-border-color: red;");
            }

            // Проверка уникальности email
            if (Validator.isValidEmail(emailField.getText())) {
                try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                    if (JDBCManager.checkCustomerExists(connection, emailField.getText())) {
                        errors.add("• Клиент с таким Email уже существует");
                        emailField.setStyle("-fx-border-color: red;");
                    }
                } catch (SQLException ex) {
                    errors.add("• Ошибка проверки клиента: " + ex.getMessage());
                }
            }

            // Валидация фамилии
            if (!Validator.isNotEmpty(lastNameField.getText())) {
                errors.add("• Необходимо указать фамилию");
                lastNameField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isAlphabetic(lastNameField.getText())) {
                errors.add("• Фамилия должна содержать только буквы");
                lastNameField.setStyle("-fx-border-color: red;");
            }

            // Валидация имени
            if (!Validator.isNotEmpty(firstNameField.getText())) {
                errors.add("• Необходимо указать имя");
                firstNameField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isAlphabetic(firstNameField.getText())) {
                errors.add("• Имя должно содержать только буквы");
                firstNameField.setStyle("-fx-border-color: red;");
            }

            // Валидация отчества
            if (Validator.isNotEmpty(middleNameField.getText()) &&
                    !Validator.isAlphabetic(middleNameField.getText())) {
                errors.add("• Отчество должно содержать только буквы");
                middleNameField.setStyle("-fx-border-color: red;");
            } else {
                middleNameField.setStyle("");
            }

            // Валидация даты рождения
            if (!Validator.isValidDate(birthDateField.getText())) {
                errors.add("• Дата должна быть в формате ГГГГ-ММ-ДД");
                birthDateField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isNotFutureDate(birthDateField.getText())) {
                errors.add("• Дата рождения не может быть в будущем");
                birthDateField.setStyle("-fx-border-color: red;");
            }

            // Валидация паспорта - ровно 10 цифр
            if (!passportField.getText().matches("^\\d{10}$")) {
                errors.add("• Серия паспорта должна содержать ровно 10 цифр");
                passportField.setStyle("-fx-border-color: red;");
            }

            // Если есть ошибки, показываем их и прерываем выполнение
            if (!errors.isEmpty()) {
                for (String error : errors) {
                    Label errorLabel = new Label(error);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
                return;
            }

            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                JDBCManager.addClient(connection,
                        lastNameField.getText(),
                        firstNameField.getText(),
                        middleNameField.getText(),
                        Long.parseLong(passportField.getText()),
                        birthDateField.getText(),
                        emailField.getText());

                showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент успешно добавлен!");

                // Очищаем поля после успешного добавления
                emailField.clear();
                lastNameField.clear();
                firstNameField.clear();
                middleNameField.clear();
                birthDateField.setText(java.time.LocalDate.now().toString());
                passportField.clear();
            } catch (SQLException ex1) {
                Label errorLabel = new Label("• " + ex1.getMessage());
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
            }
        });

        VBox form = new VBox(10,
                emailField,
                lastNameField,
                firstNameField,
                middleNameField,
                birthDateField,
                passportField,
                errorBox,
                addClientBtn);
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

        emailCombo.setOnAction(e -> {
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

        mealTypeCombo.setOnAction(e -> {
            String mealType = mealTypeCombo.getValue();
            if (mealType == null) {
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getCustomersByMealType(connection, mealType);
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
            var rs = JDBCManager.getTablesMetadata(connection);
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
                var rs = JDBCManager.executeQuery(connection,
                        "SELECT * FROM maritime_booking." + tableName + " LIMIT 100");
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
        mealCombo.setPromptText("Тип питания");

        Label infoLabel = new Label("");
        infoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

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
                var rs = JDBCManager.getVoyagesByStatusAndMeal(connection, status, meal);
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("status"));
                    data.add(row);
                }
                table.setItems(data);

                if (data.isEmpty()) {
                    infoLabel.setText("Нет данных для выбранного статуса и типа питания");
                }

            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        };

        statusCombo.setOnAction(e -> {
            String status = statusCombo.getValue();
            if (status == null) {
                return;
            }

            mealCombo.getItems().clear();
            table.getItems().clear();

            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                boolean hasVoyages = JDBCManager.hasVoyagesWithStatus(connection, status);

                if (!hasVoyages) {
                    infoLabel.setText("Нет рейсов с таким статусом");
                    return;
                }

                var rs = JDBCManager.getAvailableMealTypesByStatus(connection, status);
                boolean hasMealTypes = false;

                while (rs.next()) {
                    hasMealTypes = true;
                    mealCombo.getItems().add(rs.getString("meal_type"));
                }

                if (hasMealTypes) {
                    mealCombo.setPromptText("Выберите тип питания");

                    if (mealCombo.getItems().size() == 1) {
                        mealCombo.setValue(mealCombo.getItems().get(0));
                    }
                } else {
                    mealCombo.setPromptText("Нет доступных типов питания");
                    infoLabel.setText("Для рейсов с таким статусом нет билетов с питанием");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить типы питания: " + ex.getMessage());
            }
        });

        mealCombo.setOnAction(e -> {
            if (mealCombo.getValue() != null) {
                updateTable.run();
            }
        });

        VBox form = new VBox(10, statusLabel, statusCombo, mealLabel, mealCombo, infoLabel);
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

        Label infoLabel = new Label("");
        infoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

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

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            boolean hasInsuredTickets = JDBCManager.hasInsuredTickets(connection);

            if (!hasInsuredTickets) {
                infoLabel.setText("В системе нет билетов со страховкой");
                countryCombo.setDisable(true);
                countryCombo.setPromptText("Нет доступных стран");
            } else {
                var rs = JDBCManager.getCountriesWithInsuredTickets(connection);
                boolean hasCountries = false;

                while (rs.next()) {
                    hasCountries = true;
                    countryCombo.getItems().add(rs.getString("country"));
                }

                if (hasCountries) {

                    if (countryCombo.getItems().size() == 1) {
                        countryCombo.setValue(countryCombo.getItems().get(0));
                    }
                } else {
                    infoLabel.setText("Нет стран с билетами со страховкой");
                    countryCombo.setDisable(true);
                    countryCombo.setPromptText("Нет доступных стран");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список стран: " + e.getMessage());
        }

        countryCombo.setOnAction(e -> {
            String country = countryCombo.getValue();
            if (country == null) {
                return;
            }

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getInsuredTicketsByCountry(connection, country);
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("price"));
                    data.add(row);
                }
                table.setItems(data);

                if (data.isEmpty()) {
                    infoLabel.setText("Нет билетов со страховкой для выбранной страны");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, label, countryCombo, infoLabel);
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

        // Установка текущей даты в поле даты рождения
        birthDateField.setText(java.time.LocalDate.now().toString());

        Label voyageLabel = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("ID рейса");

        Label vesselLabel = new Label("Судно:");
        ComboBox<String> vesselCombo = new ComboBox<>();
        vesselCombo.setPromptText("IMO судна");
        vesselCombo.setDisable(true);

        Label cabinLabel = new Label("Выберите каюту:");
        ComboBox<String> cabinCombo = new ComboBox<>();
        cabinCombo.setPromptText("ID каюты");

        // Замена обычного Label на VBox со списком ошибок
        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));

        // Сохраняем полный список кают для повторного использования
        ObservableList<String> allCabins = FXCollections.observableArrayList();

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
                String cabinItem = rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                        ", категория: " + rs.getString("category") +
                        ", вместимость: " + rs.getString("capacity") +
                        ", окно: " + (rs.getBoolean("window_view") ? "да" : "нет") + ")";
                allCabins.add(cabinItem);
                cabinCombo.getItems().add(cabinItem);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }

        voyageCombo.setOnAction(e -> {
            if (voyageCombo.getValue() != null) {
                String vesselId = voyageCombo.getValue().split("IMO: ")[1].replace(")", "");

                // Находим соответствующее судно и устанавливаем его
                vesselCombo.setValue(vesselCombo.getItems().stream()
                        .filter(item -> item.startsWith(vesselId))
                        .findFirst()
                        .orElse(null));

                // Автоматическая подстановка каюты при выборе рейса
                if (vesselCombo.getValue() != null) {
                    String cabinVesselId = vesselCombo.getValue().split(" ")[0];
                    ObservableList<String> filteredCabins = FXCollections.observableArrayList(
                            allCabins.stream()
                                    .filter(item -> item.contains("IMO: " + cabinVesselId))
                                    .collect(java.util.stream.Collectors.toList()));

                    cabinCombo.setItems(filteredCabins);
                    if (!filteredCabins.isEmpty()) {
                        cabinCombo.setValue(filteredCabins.get(0));
                    }
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
        // Установка текущей даты в поле даты покупки
        purchaseDateField.setText(java.time.LocalDate.now().toString());

        Button addBtn = new Button("Добавить клиента и билет");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(250);

        addBtn.setOnAction(e -> {
            // Очищаем предыдущие ошибки и стили
            errorBox.getChildren().clear();
            emailField.setStyle("");
            lastNameField.setStyle("");
            firstNameField.setStyle("");
            middleNameField.setStyle("");
            birthDateField.setStyle("");
            passportField.setStyle("");
            voyageCombo.setStyle("");
            vesselCombo.setStyle("");
            cabinCombo.setStyle("");
            priceField.setStyle("");
            paymentMethodCombo.setStyle("");
            mealTypeCombo.setStyle("");
            luggageField.setStyle("");
            purchaseDateField.setStyle("");

            // Список для ошибок
            java.util.List<String> errors = new java.util.ArrayList<>();

            // Валидация email
            if (!Validator.isNotEmpty(emailField.getText())) {
                errors.add("• Необходимо указать Email");
                emailField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isValidEmail(emailField.getText())) {
                errors.add("• Неверный формат Email");
                emailField.setStyle("-fx-border-color: red;");
            }

            // Проверка уникальности email
            if (Validator.isValidEmail(emailField.getText())) {
                try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                    if (JDBCManager.checkCustomerExists(connection, emailField.getText())) {
                        errors.add("• Клиент с таким Email уже существует");
                        emailField.setStyle("-fx-border-color: red;");
                    }
                } catch (SQLException ex) {
                    errors.add("• Ошибка проверки клиента: " + ex.getMessage());
                }
            }

            // Валидация фамилии
            if (!Validator.isNotEmpty(lastNameField.getText())) {
                errors.add("• Необходимо указать фамилию");
                lastNameField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isAlphabetic(lastNameField.getText())) {
                errors.add("• Фамилия должна содержать только буквы");
                lastNameField.setStyle("-fx-border-color: red;");
            }

            // Валидация имени
            if (!Validator.isNotEmpty(firstNameField.getText())) {
                errors.add("• Необходимо указать имя");
                firstNameField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isAlphabetic(firstNameField.getText())) {
                errors.add("• Имя должно содержать только буквы");
                firstNameField.setStyle("-fx-border-color: red;");
            }

            // Валидация отчества
            if (Validator.isNotEmpty(middleNameField.getText()) &&
                    !Validator.isAlphabetic(middleNameField.getText())) {
                errors.add("• Отчество должно содержать только буквы");
                middleNameField.setStyle("-fx-border-color: red;");
            } else {
                middleNameField.setStyle("");
            }

            // Валидация даты рождения
            if (!Validator.isValidDate(birthDateField.getText())) {
                errors.add("• Дата рождения должна быть в формате ГГГГ-ММ-ДД");
                birthDateField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isNotFutureDate(birthDateField.getText())) {
                errors.add("• Дата рождения не может быть в будущем");
                birthDateField.setStyle("-fx-border-color: red;");
            }

            // Валидация паспорта - ровно 10 цифр
            if (!passportField.getText().matches("^\\d{10}$")) {
                errors.add("• Серия паспорта должна содержать ровно 10 цифр");
                passportField.setStyle("-fx-border-color: red;");
            }

            // Валидация рейса
            if (voyageCombo.getValue() == null) {
                errors.add("• Необходимо выбрать рейс");
                voyageCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация судна
            if (vesselCombo.getValue() == null) {
                errors.add("• Необходимо выбрать судно");
                vesselCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация каюты
            if (cabinCombo.getValue() == null) {
                errors.add("• Необходимо выбрать каюту");
                cabinCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация цены
            if (!Validator.isNumeric(priceField.getText())) {
                errors.add("• Цена должна быть числом");
                priceField.setStyle("-fx-border-color: red;");
            }

            // Валидация способа оплаты
            if (paymentMethodCombo.getValue() == null) {
                errors.add("• Необходимо выбрать способ оплаты");
                paymentMethodCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация типа питания
            if (mealTypeCombo.getValue() == null) {
                errors.add("• Необходимо выбрать тип питания");
                mealTypeCombo.setStyle("-fx-border-color: red;");
            }

            // Валидация веса багажа
            if (!Validator.isInteger(luggageField.getText())) {
                errors.add("• Вес багажа должен быть целым числом");
                luggageField.setStyle("-fx-border-color: red;");
            }

            // Валидация даты покупки
            if (!Validator.isValidDate(purchaseDateField.getText())) {
                errors.add("• Дата покупки должна быть в формате ГГГГ-ММ-ДД");
                purchaseDateField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isNotFutureDate(purchaseDateField.getText())) {
                errors.add("• Дата покупки не может быть в будущем");
                purchaseDateField.setStyle("-fx-border-color: red;");
            }

            // Если есть ошибки, показываем их и прерываем выполнение
            if (!errors.isEmpty()) {
                for (String error : errors) {
                    Label errorLabel = new Label(error);
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
                return;
            }

            String voyageId = voyageCombo.getValue().split(" ")[0];
            String vesselId = vesselCombo.getValue().split(" ")[0];
            String cabinId = cabinCombo.getValue().split(" ")[0];

            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                try {
                    int ticketId = JDBCManager.addClientAndTicket(
                            connection,
                            emailField.getText(),
                            lastNameField.getText(),
                            firstNameField.getText(),
                            middleNameField.getText(),
                            birthDateField.getText(),
                            passportField.getText(),
                            Integer.parseInt(voyageId),
                            vesselId,
                            Integer.parseInt(cabinId),
                            Double.parseDouble(priceField.getText()),
                            paymentMethodCombo.getValue(),
                            mealTypeCombo.getValue(),
                            insuranceCheck.isSelected(),
                            Integer.parseInt(luggageField.getText()),
                            purchaseDateField.getText());

                    if (ticketId > 0) {
                        System.out.println("Inserted ticket with ID " + ticketId);
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент и билет успешно добавлены!");
                    showTable("SELECT * FROM maritime_booking.customers", table);

                    // Очищаем поля после успешного добавления
                    emailField.clear();
                    lastNameField.clear();
                    firstNameField.clear();
                    middleNameField.clear();
                    birthDateField.setText(java.time.LocalDate.now().toString());
                    passportField.clear();
                    priceField.clear();
                    paymentMethodCombo.setValue(null);
                    mealTypeCombo.setValue(null);
                    insuranceCheck.setSelected(false);
                    luggageField.clear();
                    purchaseDateField.setText(java.time.LocalDate.now().toString());

                    // Не сбрасываем выбор рейса, судна и каюты для удобства
                } catch (SQLException ex) {
                    Label errorLabel = new Label("• " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
            } catch (SQLException ex) {
                Label errorLabel = new Label("• " + ex.getMessage());
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
            }
        });

        VBox form = new VBox(10,
                label,
                emailField,
                lastNameField,
                firstNameField,
                middleNameField,
                birthDateField,
                passportField,
                voyageLabel,
                voyageCombo,
                vesselLabel,
                vesselCombo,
                cabinLabel,
                cabinCombo,
                priceField,
                paymentMethodCombo,
                mealTypeCombo,
                insuranceCheck,
                luggageField,
                purchaseDateField,
                errorBox,
                addBtn);

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

        // Замена обычного Label на VBox со списком ошибок
        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));

        // Создаем таблицы для отображения
        Label voyagesLabel = new Label("Таблица рейсов:");
        TableView<ObservableList<String>> voyagesTable = new TableView<>();
        voyagesTable.setPrefHeight(150);

        Label ticketsLabel = new Label("Таблица билетов выбранного рейса:");
        TableView<ObservableList<String>> ticketsTable = new TableView<>();
        ticketsTable.setPrefHeight(150);

        Label stagesLabel = new Label("Таблица этапов выбранного рейса:");
        TableView<ObservableList<String>> stagesTable = new TableView<>();
        stagesTable.setPrefHeight(150);

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
            Label errorLabel = new Label("• Не удалось загрузить данные: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            errorBox.getChildren().add(errorLabel);
        }

        voyageCombo.setOnAction(e -> {
            if (voyageCombo.getValue() != null) {
                String vesselIdFromVoyage = voyageCombo.getValue().split("IMO: ")[1].split(" -")[0];
                vesselCombo.setValue(vesselCombo.getItems().stream()
                        .filter(item -> item.startsWith(vesselIdFromVoyage))
                        .findFirst()
                        .orElse(null));

                // При выборе рейса сразу отображаем билеты и этапы этого рейса
                if (voyageCombo.getValue() != null && vesselCombo.getValue() != null) {
                    String voyageId = voyageCombo.getValue().split(" ")[0];
                    String vesselId = vesselCombo.getValue().split(" ")[0];

                    try (Connection _ = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                        showTable("SELECT * FROM maritime_booking.tickets WHERE voyage_id = " + voyageId +
                                " AND vessel_id = '" + vesselId + "'", ticketsTable);
                        showTable("SELECT * FROM maritime_booking.voyage_stages WHERE voyage_id = " + voyageId +
                                " AND vessel_id = '" + vesselId + "'", stagesTable);
                    } catch (SQLException ex) {
                        errorBox.getChildren().clear();
                        Label errorLabel = new Label("• Не удалось загрузить связанные данные: " + ex.getMessage());
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                        errorBox.getChildren().add(errorLabel);
                    }
                }
            }
        });

        Button deleteBtn = new Button("Удалить всё связанное");
        deleteBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #E53935; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        // Первоначальное заполнение таблицы рейсов
        showTable("SELECT * FROM maritime_booking.voyages", voyagesTable);

        deleteBtn.setOnAction(e -> {
            // Очищаем предыдущие ошибки
            errorBox.getChildren().clear();
            voyageCombo.setStyle("");
            vesselCombo.setStyle("");

            if (voyageCombo.getValue() == null || vesselCombo.getValue() == null) {
                Label errorLabel = new Label("• Выберите рейс и судно!");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);

                if (voyageCombo.getValue() == null) {
                    voyageCombo.setStyle("-fx-border-color: red;");
                }
                if (vesselCombo.getValue() == null) {
                    vesselCombo.setStyle("-fx-border-color: red;");
                }
                return;
            }

            String voyageId = voyageCombo.getValue().split(" ")[0];
            String vesselId = vesselCombo.getValue().split(" ")[0];

            // Подтверждение удаления
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение");
            confirmAlert.setHeaderText("Удаление рейса");
            confirmAlert.setContentText("Вы уверены, что хотите удалить рейс " + voyageId +
                    " и все связанные данные? Это действие нельзя отменить.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                    // Удаляем рейс
                    JDBCManager.deleteVoyage(connection, Integer.parseInt(voyageId), vesselId);

                    Label successLabel = new Label("• Рейс " + voyageId + " и всё связанное удалено!");
                    successLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                    errorBox.getChildren().add(successLabel);

                    // Очищаем значения комбобоксов
                    voyageCombo.setValue(null);
                    vesselCombo.setValue(null);

                    // Обновляем комбобокс рейсов
                    voyageCombo.getItems().clear();
                    var rs = JDBCManager.getVoyagesWithVessels(connection);
                    while (rs.next()) {
                        voyageCombo.getItems().add(rs.getString("id") + " (IMO: " + rs.getString("vessel_id") +
                                " - " + rs.getString("name") + ")");
                    }

                    // Обновляем таблицу рейсов
                    showTable("SELECT * FROM maritime_booking.voyages", voyagesTable);

                    // Проверяем реальное отсутствие данных в базе через SQL-запросы
                    showTable("SELECT * FROM maritime_booking.tickets WHERE voyage_id = " + voyageId +
                            " AND vessel_id = '" + vesselId + "'", ticketsTable);
                    showTable("SELECT * FROM maritime_booking.voyage_stages WHERE voyage_id = " + voyageId +
                            " AND vessel_id = '" + vesselId + "'", stagesTable);

                    ticketsLabel.setText("Таблица билетов выбранного рейса (после удаления)");
                    stagesLabel.setText("Таблица этапов выбранного рейса (после удаления)");
                } catch (SQLException ex) {
                    Label errorLabel = new Label("• Ошибка при удалении: " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
            }
        });

        VBox form = new VBox(10, label, voyageLabel, voyageCombo, vesselLabel, vesselCombo, errorBox, deleteBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);

        // Добавляем все компоненты в scrollPane для возможности прокрутки
        VBox tablesVBox = new VBox(10, voyagesLabel, voyagesTable, ticketsLabel, ticketsTable, stagesLabel,
                stagesTable);
        ScrollPane scrollPane = new ScrollPane(tablesVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);

        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, scrollPane);
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

        // Замена обычного Label на VBox со списком ошибок
        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));

        Button showFutureBtn = new Button("Показать будущие цены");
        Button updateBtn = new Button("Обновить цены");

        HBox buttonBox = new HBox(10, showFutureBtn, updateBtn);
        buttonBox.setAlignment(Pos.CENTER);

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(350);

        // Колонки добавляем один раз
        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID билета");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(100);
        TableColumn<ObservableList<String>, String> weightCol = new TableColumn<>("Вес багажа");
        weightCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        weightCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Текущая цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        priceCol.setPrefWidth(120);
        TableColumn<ObservableList<String>, String> futureCol = new TableColumn<>("Будущая обновленная цена");
        futureCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));
        futureCol.setPrefWidth(160);
        TableColumn<ObservableList<String>, String> dateCol = new TableColumn<>("Дата покупки");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(4)));
        dateCol.setPrefWidth(120);
        table.getColumns().add(idCol);
        table.getColumns().add(weightCol);
        table.getColumns().add(priceCol);
        table.getColumns().add(futureCol);
        table.getColumns().add(dateCol);

        showFutureBtn.setOnAction(e -> {
            // Очищаем предыдущие ошибки и сбрасываем стили полей
            errorBox.getChildren().clear();
            weightField.setStyle("");
            dateField.setStyle("");

            String minWeight = weightField.getText().trim();
            String date = dateField.getText().trim();

            boolean hasErrors = false;

            if (minWeight.isEmpty() || !minWeight.matches("\\d+(\\.\\d+)?")) {
                Label errorLabel = new Label("• Введите корректный вес!");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
                weightField.setStyle("-fx-border-color: red;");
                hasErrors = true;
            }

            if (date.isEmpty() || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Label errorLabel = new Label("• Введите корректную дату!");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
                dateField.setStyle("-fx-border-color: red;");
                hasErrors = true;
            }

            if (hasErrors) {
                return;
            }

            table.getItems().clear();
            futureCol.setText("Будущая обновленная цена");
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                var rs = JDBCManager.getFutureLuggagePrices(connection, Double.parseDouble(minWeight), date);
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                int rowCount = 0;
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("luggage_weight"));
                    row.add(rs.getString("price"));
                    row.add(rs.getString("updated_price"));
                    row.add(rs.getString("purchase_date"));
                    data.add(row);
                    rowCount++;
                }
                table.setItems(data);
                if (rowCount == 0) {
                    Label infoLabel = new Label("• Не найдено билетов по заданным условиям");
                    infoLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
                    errorBox.getChildren().add(infoLabel);
                }
            } catch (SQLException ex) {
                Label errorLabel = new Label("• Не удалось загрузить данные: " + ex.getMessage());
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
            }
        });

        updateBtn.setOnAction(e -> {
            // Очищаем предыдущие ошибки и сбрасываем стили полей
            errorBox.getChildren().clear();
            weightField.setStyle("");
            dateField.setStyle("");

            String minWeight = weightField.getText().trim();
            String date = dateField.getText().trim();

            boolean hasErrors = false;

            if (minWeight.isEmpty() || !minWeight.matches("\\d+(\\.\\d+)?")) {
                Label errorLabel = new Label("• Введите корректный вес!");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
                weightField.setStyle("-fx-border-color: red;");
                hasErrors = true;
            }

            if (date.isEmpty() || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Label errorLabel = new Label("• Введите корректную дату!");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
                dateField.setStyle("-fx-border-color: red;");
                hasErrors = true;
            }

            if (hasErrors) {
                return;
            }

            table.getItems().clear();
            futureCol.setText("Обновленная цена");
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                JDBCManager.updateLuggagePrice(connection, Double.parseDouble(minWeight), date);

                Label successLabel = new Label("• Цены успешно обновлены!");
                successLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                errorBox.getChildren().add(successLabel);

                var rs = JDBCManager.getUpdatedLuggageTickets(connection, Double.parseDouble(minWeight), date);
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                int rowCount = 0;
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("luggage_weight"));
                    row.add(rs.getString("price"));
                    row.add(rs.getString("price")); // После обновления текущая цена = обновленная
                    row.add(rs.getString("purchase_date"));
                    data.add(row);
                    rowCount++;
                }
                table.setItems(data);

                if (rowCount == 0) {
                    Label infoLabel = new Label("• Не найдено билетов, подходящих для обновления");
                    infoLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
                    errorBox.getChildren().add(infoLabel);
                }
            } catch (SQLException ex) {
                Label errorLabel = new Label("• Ошибка обновления: " + ex.getMessage());
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
            }
        });

        VBox form = new VBox(10, weightLabel, weightField, dateLabel, dateField, errorBox, buttonBox);
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
            var rs = JDBCManager.getVoyagesWithVesselDetails(connection);
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

        voyageCombo.setOnAction(e -> {
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

        yearCombo.setOnAction(e -> {
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
        searchBtn.setOnAction(e -> {
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
        searchBtn.setOnAction(e -> {
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
            var rs = JDBCManager.executeQuery(connection, sql);
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
