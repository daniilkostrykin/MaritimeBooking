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
import org.example.dao.*;
import org.example.entity.*;
import org.example.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MaritimeBookingAppHib extends Application {
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
        primaryStage.setTitle("Maritime Booking System With Hibernate");

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

        // Использую Hibernate DAO вместо JDBC
        VoyageDAO voyageDAO = new VoyageDAO();
        VesselDAO vesselDAO = new VesselDAO();
        CabinDAO cabinDAO = new CabinDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        TicketDAO ticketDAO = new TicketDAO();

        // Получаю активные рейсы через VoyageDAO
        List<Voyage> activeVoyages = voyageDAO.findByStatus("active");
        for (Voyage voyage : activeVoyages) {
            voyageCombo.getItems().add(voyage.getId() + " (IMO: " + voyage.getVesselId() + ")");
        }

        // Получаю суда через VesselDAO
        List<Vessel> vessels = vesselDAO.findAll();
        for (Vessel vessel : vessels) {
            vesselCombo.getItems().add(vessel.getImo() + " (" + vessel.getName() + ")");
        }

        // Получаю каюты через CabinDAO
        List<Cabin> cabins = cabinDAO.findAll();
        for (Cabin cabin : cabins) {
            cabinCombo.getItems().add(cabin.getId() + " (IMO: " + cabin.getVesselId() +
                    ", категория: " + cabin.getCategory() +
                    ", вместимость: " + cabin.getCapacity() +
                    ", окно: " + (cabin.getWindowView() ? "да" : "нет") + ")");
        }

        voyageCombo.setOnAction(e -> {
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

            if (voyageCombo.getValue() == null) {
                valid = false;
                errors.append("Выберите рейс. ");
                voyageCombo.setStyle("-fx-border-color: red;");
            } else
                voyageCombo.setStyle("");

            if (cabinCombo.getValue() == null) {
                valid = false;
                errors.append("Выберите каюту. ");
                cabinCombo.setStyle("-fx-border-color: red;");
            } else
                cabinCombo.setStyle("");

            if (priceField.getText().isBlank()) {
                valid = false;
                errors.append("Цена required. ");
                priceField.setStyle("-fx-border-color: red;");
            } else
                priceField.setStyle("");

            if (paymentMethodCombo.getValue() == null) {
                valid = false;
                errors.append("Метод оплаты required. ");
                paymentMethodCombo.setStyle("-fx-border-color: red;");
            } else
                paymentMethodCombo.setStyle("");

            if (mealTypeCombo.getValue() == null) {
                valid = false;
                errors.append("Тип питания required. ");
                mealTypeCombo.setStyle("-fx-border-color: red;");
            } else
                mealTypeCombo.setStyle("");

            if (luggageField.getText().isBlank()) {
                valid = false;
                errors.append("Вес багажа required. ");
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

            try {
                String voyageId = voyageCombo.getValue().split(" ")[0];
                String vesselId = vesselCombo.getValue().split(" ")[0];
                String cabinId = cabinCombo.getValue().split(" ")[0];
                String email = emailField.getText();

                // Получаем объекты для связей
                Customer customer = customerDAO.findById(email);
                if (customer == null) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Клиент с указанным email не найден!");
                    return;
                }

                VoyageId voyageIdObj = new VoyageId();
                voyageIdObj.setId(Long.parseLong(voyageId));
                voyageIdObj.setVesselId(vesselId);


                // Создаем новый билет
                Ticket ticket = new Ticket();
                ticket.setVoyageId(Long.parseLong(voyageId));
                ticket.setVesselId(vesselId);
                ticket.setCabinId(Long.parseLong(cabinId));
                ticket.setCustomer(customer);
                ticket.setPrice(BigDecimal.valueOf(Double.parseDouble(priceField.getText())));

                // Устанавливаем тип оплаты
                Ticket.PaymentMethod paymentMethod = Ticket.PaymentMethod.valueOf(paymentMethodCombo.getValue());
                ticket.setPaymentMethod(paymentMethod);

                // Устанавливаем тип питания
                Ticket.MealType mealType = Ticket.MealType.valueOf(mealTypeCombo.getValue());
                ticket.setMealType(mealType);

                ticket.setInsurance(insuranceCheck.isSelected());
                ticket.setLuggageWeight(Integer.parseInt(luggageField.getText()));
                ticket.setPurchaseDate(LocalDate.parse(purchaseDateField.getText()));

                // Сохраняем билет
                ticketDAO.save(ticket);

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
            } catch (Exception ex1) {
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

        // Используем TicketDAO для получения доступных лет
        TicketDAO ticketDAO = new TicketDAO();
        try {
            List<Integer> years = ticketDAO.findDistinctYears();
            for (Integer year : years) {
                yearCombo.getItems().add(year.toString());
            }
        } catch (Exception e) {
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

            // Используем TicketDAO для поиска билетов
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try {
                List<Ticket> tickets = ticketDAO.findTicketsForYearWithMaxPrice(
                        Integer.parseInt(year),
                        Double.parseDouble(maxPrice));

                for (Ticket ticket : tickets) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(ticket.getId().toString());
                    row.add(ticket.getPrice().toString());
                    data.add(row);
                }
                table.setItems(data);
            } catch (Exception ex) {
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

        // Используем CustomerDAO для добавления клиента
        CustomerDAO customerDAO = new CustomerDAO();

        addClientBtn.setOnAction(e -> {
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

            try {
                // Создаем нового клиента
                Customer customer = new Customer();
                customer.setEmail(emailField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setFirstName(firstNameField.getText());
                customer.setMiddleName(middleNameField.getText());
                customer.setBirthDate(LocalDate.parse(birthDateField.getText()));
                customer.setPassportSeries(passportField.getText());

                // Сохраняем клиента
                customerDAO.save(customer);

                showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент успешно добавлен!");
                emailField.clear();
                lastNameField.clear();
                firstNameField.clear();
                middleNameField.clear();
                birthDateField.clear();
                passportField.clear();
            } catch (Exception ex1) {
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

        // Используем CustomerDAO для получения списка клиентов с билетами
        CustomerDAO customerDAO = new CustomerDAO();
        try {
            List<Customer> customers = customerDAO.findCustomersWithTickets();
            for (Customer customer : customers) {
                emailCombo.getItems().add(customer.getEmail() +
                        " (" + customer.getLastName() + " " + customer.getFirstName() + ")");
            }
        } catch (Exception e) {
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

        // Используем TicketDAO для получения билетов клиента
        TicketDAO ticketDAO = new TicketDAO();

        emailCombo.setOnAction(e -> {
            if (emailCombo.getValue() == null) {
                return;
            }

            String email = emailCombo.getValue().split(" \\(")[0];
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try {
                List<Ticket> tickets = ticketDAO.findByCustomerEmail(email);

                for (Ticket ticket : tickets) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(ticket.getId().toString());
                    row.add(ticket.getPrice().toString());
                    data.add(row);
                }
                table.setItems(data);
            } catch (Exception ex) {
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

        // Используем CustomerDAO для получения клиентов по типу питания
        CustomerDAO customerDAO = new CustomerDAO();

        mealTypeCombo.setOnAction(e -> {
            String mealType = mealTypeCombo.getValue();
            if (mealType == null) {
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try {
                List<Customer> customers = customerDAO.findByMealType(mealType);

                for (Customer customer : customers) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(customer.getEmail());
                    row.add(customer.getFirstName());
                    data.add(row);
                }
                table.setItems(data);
            } catch (Exception ex) {
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

        // Получаем список таблиц через HibernateUtil и метаданные
        try (Session _ = HibernateUtil.getSessionFactory().openSession()) {
            // Получаем список классов-сущностей
            String[] entities = { "Customer", "Vessel", "Voyage", "Cabin", "Ticket", "Port", "VoyageStage" };
            for (String entity : entities) {
                tablesCombo.getItems().add(entity.toLowerCase() + "s"); // Добавляем имя таблицы
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось получить список таблиц: " + e.getMessage());
        }

        tablesCombo.setOnAction(event -> {
            String tableName = tablesCombo.getValue();
            if (tableName == null)
                return;

            tableView.getColumns().clear();
            tableView.getItems().clear();

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                // Используем нативный SQL-запрос через Hibernate
                String query = "SELECT * FROM maritime_booking." + tableName + " LIMIT 100";
                List<Object[]> results = session.createNativeQuery(query, Object[].class).list();

                if (results.isEmpty()) {
                    return;
                }

                // Получаем метаданные для создания колонок
                java.sql.ResultSet rs = session.doReturningWork(conn -> {
                    java.sql.Statement stmt = conn.createStatement();
                    return stmt.executeQuery("SELECT * FROM maritime_booking." + tableName + " LIMIT 0");
                });

                java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                // Создаем колонки
                for (int i = 1; i <= columnCount; i++) {
                    final int colIndex = i - 1;
                    TableColumn<ObservableList<String>, String> col = new TableColumn<>(rsmd.getColumnName(i));
                    col.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(colIndex)));
                    tableView.getColumns().add(col);
                }

                // Заполняем данными
                for (Object[] row : results) {
                    ObservableList<String> rowData = FXCollections.observableArrayList();
                    for (Object cell : row) {
                        rowData.add(cell == null ? "" : cell.toString());
                    }
                    tableView.getItems().add(rowData);
                }
            } catch (Exception e) {
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

        // Используем VoyageDAO для получения рейсов
        VoyageDAO voyageDAO = new VoyageDAO();

        Runnable updateTable = () -> {
            String status = statusCombo.getValue();
            String meal = mealCombo.getValue();
            if (status == null || meal == null) {
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try {
                List<Voyage> voyages = voyageDAO.findByStatusAndMealType(status, meal);

                for (Voyage voyage : voyages) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(voyage.getId().toString());
                    row.add(voyage.getStatus().toString());
                    data.add(row);
                }
                table.setItems(data);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        };

        statusCombo.setOnAction(e -> updateTable.run());
        mealCombo.setOnAction(e -> updateTable.run());

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

        // Используем PortDAO для получения списка стран
        PortDAO portDAO = new PortDAO();
        try {
            List<String> countries = portDAO.getCountriesWithInsuredTickets();
            for (String country : countries) {
                countryCombo.getItems().add(country);
            }
        } catch (Exception e) {
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

        // Используем TicketDAO для получения билетов по стране
        TicketDAO ticketDAO = new TicketDAO();

        countryCombo.setOnAction(e -> {
            String country = countryCombo.getValue();
            if (country == null) {
                return;
            }

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try {
                List<Ticket> tickets = ticketDAO.findInsuredTicketsByDepartureCountry(country);

                for (Ticket ticket : tickets) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(ticket.getId().toString());
                    row.add(ticket.getPrice().toString());
                    data.add(row);
                }
                table.setItems(data);
            } catch (Exception ex) {
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

        // Используем VoyageDAO, VesselDAO и CabinDAO вместо прямых запросов
        VoyageDAO voyageDAO = new VoyageDAO();
        VesselDAO vesselDAO = new VesselDAO();
        CabinDAO cabinDAO = new CabinDAO();

        try {
            // Получаем активные рейсы
            List<Voyage> activeVoyages = voyageDAO.findByStatus("active");
            for (Voyage voyage : activeVoyages) {
                voyageCombo.getItems().add(voyage.getId() + " (IMO: " + voyage.getVesselId() + ")");
            }

            // Получаем список судов
            List<Vessel> vessels = vesselDAO.findAll();
            for (Vessel vessel : vessels) {
                vesselCombo.getItems().add(vessel.getImo() + " (" + vessel.getName() + ")");
            }

            // Получаем список кают
            List<Cabin> cabins = cabinDAO.findAll();
            for (Cabin cabin : cabins) {
                cabinCombo.getItems().add(cabin.getId() + " (IMO: " + cabin.getVessel().getImo() +
                        ", категория: " + cabin.getCategory() +
                        ", вместимость: " + cabin.getCapacity() +
                        ", окно: " + (cabin.getWindowView() ? "да" : "нет") + ")");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }

        voyageCombo.setOnAction(e -> {
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

        // Используем DAO для добавления клиента и билета
        CustomerDAO customerDAO = new CustomerDAO();
        TicketDAO ticketDAO = new TicketDAO();

        addBtn.setOnAction(e -> {
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

            try {
                // Создаем объект клиента
                Customer customer = new Customer();
                customer.setEmail(emailField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setFirstName(firstNameField.getText());
                customer.setMiddleName(middleNameField.getText());
                customer.setBirthDate(LocalDate.parse(birthDateField.getText()));
                customer.setPassportSeries(passportField.getText());

                // Создаем объект билета
                Ticket ticket = new Ticket();
                ticket.setCustomer(customer);
                // Получаем рейс
                Voyage voyage = voyageDAO.findById(Long.parseLong(voyageId), vesselId);
                ticket.setVoyage(voyage);
                ticket.setVoyageId(voyage.getId());
                ticket.setVesselId(vesselId);
                ticket.setCabinId(Long.parseLong(cabinId));
                ticket.setPrice(new BigDecimal(priceField.getText()));
                ticket.setPaymentMethod(Ticket.PaymentMethod.valueOf(paymentMethodCombo.getValue()));
                ticket.setMealType(Ticket.MealType.valueOf(mealTypeCombo.getValue()));
                ticket.setInsurance(insuranceCheck.isSelected());
                ticket.setLuggageWeight(Integer.parseInt(luggageField.getText()));
                ticket.setPurchaseDate(LocalDate.parse(purchaseDateField.getText()));

                // Сохраняем клиента и билет
                customerDAO.save(customer);
                ticketDAO.save(ticket);

                showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент и билет успешно добавлены!");

                // Обновляем таблицу данных для отображения через нативный запрос
                Session session = HibernateUtil.getSessionFactory().openSession();
                String sql = "SELECT * FROM maritime_booking.customers";
                List<Object[]> results = session.createNativeQuery(sql, Object[].class).list();

                // Очищаем и настраиваем колонки таблицы если необходимо
                if (table.getColumns().isEmpty()) {
                    TableColumn<ObservableList<String>, String> emailCol = new TableColumn<>("Email");
                    emailCol.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
                    TableColumn<ObservableList<String>, String> lastNameCol = new TableColumn<>("Фамилия");
                    lastNameCol.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
                    TableColumn<ObservableList<String>, String> firstNameCol = new TableColumn<>("Имя");
                    firstNameCol.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));

                    table.getColumns().addAll(emailCol, lastNameCol, firstNameCol);
                }

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                for (Object[] row : results) {
                    ObservableList<String> tableRow = FXCollections.observableArrayList();
                    for (Object cell : row) {
                        tableRow.add(cell == null ? "" : cell.toString());
                    }
                    data.add(tableRow);
                }
                table.setItems(data);
                session.close();

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при добавлении: " + ex.getMessage());
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

        // Используем VoyageDAO и VesselDAO вместо прямых запросов
        VoyageDAO voyageDAO = new VoyageDAO();
        VesselDAO vesselDAO = new VesselDAO();

        try {
            // Получаем рейсы с судами
            List<Object[]> voyagesWithVessels = voyageDAO.getVoyageWithVessels();
            for (Object[] row : voyagesWithVessels) {
                Long id = (Long) row[0];
                String vesselId = (String) row[1];
                String vesselName = (String) row[2];
                voyageCombo.getItems().add(id + " (IMO: " + vesselId + " - " + vesselName + ")");
            }

            // Получаем список судов
            List<Vessel> vessels = vesselDAO.findAll();
            for (Vessel vessel : vessels) {
                vesselCombo.getItems().add(vessel.getImo() + " (" + vessel.getName() + ")");
            }
        } catch (Exception e) {
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

        deleteBtn.setOnAction(e -> {
            if (voyageCombo.getValue() == null || vesselCombo.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Выберите рейс и судно!");
                return;
            }

            String voyageId = voyageCombo.getValue().split(" ")[0];
            String vesselId = vesselCombo.getValue().split(" ")[0];

            try {
                // Удаляем рейс и всё связанное с ним через DAO
                // Для каскадного удаления можно использовать Hibernate аннотации в сущностях,
                // либо удалять вручную в правильном порядке
                voyageDAO.delete(Long.parseLong(voyageId), vesselId);

                showAlert(Alert.AlertType.INFORMATION, "Успех", "Рейс и всё связанное удалено!");

                // Обновляем список рейсов
                voyageCombo.setValue(null);
                vesselCombo.setValue(null);
                voyageCombo.getItems().clear();

                List<Object[]> updatedVoyagesWithVessels = voyageDAO.getVoyageWithVessels();
                for (Object[] row : updatedVoyagesWithVessels) {
                    Long id = (Long) row[0];
                    String vId = (String) row[1];
                    String vName = (String) row[2];
                    voyageCombo.getItems().add(id + " (IMO: " + vId + " - " + vName + ")");
                }

                // Обновляем таблицу через Hibernate
                Session session = HibernateUtil.getSessionFactory().openSession();
                List<Object[]> results = session
                        .createNativeQuery("SELECT * FROM maritime_booking.voyages", Object[].class).list();

                // Если таблица пустая, настраиваем колонки
                if (table.getColumns().isEmpty()) {
                    TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID");
                    idCol.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
                    TableColumn<ObservableList<String>, String> vesselCol = new TableColumn<>("Судно (IMO)");
                    vesselCol.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
                    TableColumn<ObservableList<String>, String> statusCol = new TableColumn<>("Статус");
                    statusCol.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));

                    table.getColumns().addAll(idCol, vesselCol, statusCol);
                }

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                for (Object[] row : results) {
                    ObservableList<String> tableRow = FXCollections.observableArrayList();
                    for (Object cell : row) {
                        tableRow.add(cell == null ? "" : cell.toString());
                    }
                    data.add(tableRow);
                }
                table.setItems(data);
                session.close();

            } catch (Exception ex) {
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

        // Используем TicketDAO

        showFutureBtn.setOnAction(e -> {
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
            table.getItems().clear();
            futureCol.setText("Будущая обновленная цена");

            try {

                try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                    String sql = "SELECT t.id, t.luggage_weight, t.price, " +
                            "t.price + (t.luggage_weight - ?) * 1000 as updated_price, t.purchase_date " +
                            "FROM maritime_booking.tickets t " +
                            "WHERE t.purchase_date < CAST(? AS date) AND t.luggage_weight > ? " +
                            "AND EXISTS (SELECT 1 FROM maritime_booking.voyages v " +
                            "WHERE t.voyage_id = v.id AND t.vessel_id = v.vessel_id AND v.status = 'active') " +
                            "AND EXISTS (SELECT 1 FROM maritime_booking.customers c WHERE t.email = c.email) " +
                            "ORDER BY t.id";
                    var ps = connection.prepareStatement(sql);
                    ps.setDouble(1, Double.parseDouble(minWeight));
                    ps.setString(2, date);
                    ps.setDouble(3, Double.parseDouble(minWeight));
                    var rs = ps.executeQuery();
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
                        showAlert(Alert.AlertType.INFORMATION, "Нет билетов",
                                "Не найдено билетов по заданным условиям.");
                    }
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Некорректная дата: " + ex.getMessage());
            }
        });

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
            table.getItems().clear();
            futureCol.setText("Обновленная цена");
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
                JDBCManager.updateLuggagePrice(connection, Double.parseDouble(minWeight), date);
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Цены обновлены!");
                String sql = "SELECT id, luggage_weight, price, purchase_date " +
                        "FROM maritime_booking.tickets " +
                        "WHERE purchase_date < CAST(? AS date) AND luggage_weight > ? " +
                        "AND EXISTS (SELECT 1 FROM maritime_booking.voyages v " +
                        "WHERE tickets.voyage_id = v.id AND tickets.vessel_id = v.vessel_id AND v.status = 'active') " +
                        "AND EXISTS (SELECT 1 FROM maritime_booking.customers c WHERE tickets.email = c.email) " +
                        "ORDER BY id";
                var ps = connection.prepareStatement(sql);
                ps.setString(1, date);
                ps.setDouble(2, Double.parseDouble(minWeight));
                var rs = ps.executeQuery();
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("luggage_weight"));
                    row.add(rs.getString("price"));
                    row.add(rs.getString("price")); // После обновления текущая цена = обновленная
                    row.add(rs.getString("purchase_date"));
                    data.add(row);
                }
                table.setItems(data);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка обновления: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, weightLabel, weightField, dateLabel, dateField, buttonBox);
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