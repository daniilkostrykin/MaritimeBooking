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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import java.util.Optional;
import org.hibernate.Session;
import java.util.List;
import org.example.util.HibernateUtil;
import org.example.entity.Ticket;
import org.example.entity.Voyage;

public class MaritimeBookingAppHibernate extends Application {
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

        Tab customQueryTab = new Tab("Произвольный SQL-запрос");
        customQueryTab.setClosable(false);
        customQueryTab.setContent(createCustomQueryTab());

        tabPane.getTabs().addAll(universalTab, ticketsForYearWithPriceTab,
                ticketsByClientTab, clientsByMealTab, completedVoyagesTab, insuredTicketsFromCountryTab,
                addClientAndTicketTab, deleteVoyageTab, luggagePriceUpdateTab, voyageRouteTab, ticketSalesTab,
                customQueryTab);

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

    private VBox createTicketsForYearWithPriceTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Билеты за год с ценой меньше");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label yearLabel = new Label("Выберите год:");
        ComboBox<String> yearCombo = new ComboBox<>();
        yearCombo.setPromptText("Год");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> years = HibernateManager.getAvailableYears(session);
            for (Object[] year : years) {
                yearCombo.getItems().add(year[0].toString());
            }
        } catch (Exception e) {
            errorLabel.setText("Не удалось загрузить список лет: " + e.getMessage());
        }

        TextField priceField = new TextField();
        priceField.setPromptText("Максимальная цена");

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
                errorLabel.setText("Выберите год и введите максимальную цену!");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(maxPrice);
                if (price < 0) {
                    errorLabel.setText("Максимальная цена не может быть отрицательной!");
                    return;
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Введите корректное число для максимальной цены!");
                return;
            }

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> tickets = HibernateManager.getTicketsForYearWithPrice(session,
                        Integer.parseInt(year), price);
                for (Object[] ticket : tickets) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(ticket[0].toString());
                    row.add(ticket[1].toString());
                    data.add(row);
                }
                table.setItems(data);
                if (data.isEmpty()) {
                    errorLabel.setText("Билетов с ценой меньше " + price + " за " + year + " год не найдено");
                } else {
                    errorLabel.setText("");
                }
            } catch (Exception ex) {
                errorLabel.setText("Не удалось загрузить данные: " + ex.getMessage());
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

    private VBox createTicketsByClientTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Билеты клиента");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label = new Label("Выберите клиента:");
        ComboBox<String> emailCombo = new ComboBox<>();
        emailCombo.setPromptText("Email клиента");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> customers = HibernateManager.getCustomersWithTickets(session);
            for (Object[] customer : customers) {
                emailCombo.getItems().add(customer[0] + " (" + customer[1] + " " + customer[2] + ")");
            }
        } catch (Exception e) {
            errorLabel.setText("Не удалось загрузить список клиентов: " + e.getMessage());
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
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> tickets = HibernateManager.getCustomerTickets(session, email);
                for (Object[] ticket : tickets) {
                    data.add(FXCollections.observableArrayList(ticket[0].toString(), ticket[1].toString()));
                }
                table.setItems(data);
                errorLabel.setText("");
            } catch (Exception ex) {
                errorLabel.setText("Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, label, emailCombo, errorLabel);
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

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

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
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> customers = HibernateManager.getCustomersByMealType(session,
                        Ticket.MealType.valueOf(mealType));
                for (Object[] customer : customers) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(customer[0].toString());
                    row.add(customer[1].toString());
                    data.add(row);
                }
                table.setItems(data);
                errorLabel.setText("");
            } catch (Exception ex) {
                errorLabel.setText("Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, label, mealTypeCombo, errorLabel);
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

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'maritime_booking'";
            List<?> tables = HibernateManager.executeCustomQuery(session, sql);
            for (Object table : tables) {
                tablesCombo.getItems().add(table.toString());
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
                String sql = "SELECT * FROM maritime_booking." + tableName + " LIMIT 100";
                List<Object[]> results = HibernateManager.executeCustomQuery(session, sql);

                if (!results.isEmpty()) {
                    Object[] firstRow = results.get(0);
                    for (int i = 0; i < firstRow.length; i++) {
                        final int colIndex = i;
                        TableColumn<ObservableList<String>, String> col = new TableColumn<>("Column " + (i + 1));
                        col.setCellValueFactory(
                                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(colIndex)));
                        tableView.getColumns().add(col);
                    }

                    ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                    for (Object[] row : results) {
                        ObservableList<String> rowData = FXCollections.observableArrayList();
                        for (Object value : row) {
                            rowData.add(value != null ? value.toString() : "");
                        }
                        data.add(rowData);
                    }
                    tableView.setItems(data);
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
        statusCombo.setPromptText("Статус рейса");

        Label mealLabel = new Label("Выберите тип питания:");
        ComboBox<String> mealCombo = new ComboBox<>();
        mealCombo.setPromptText("Тип питания");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

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

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> statuses = HibernateManager.getAvailableVoyageStatuses(session);
            for (Object status : statuses) {
                if (status instanceof Object[] arr) {
                    statusCombo.getItems().add(arr[0].toString());
                } else {
                    statusCombo.getItems().add(status.toString());
                }
            }
            if (statusCombo.getItems().isEmpty()) {
                errorLabel.setText("В базе данных нет рейсов");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Не удалось загрузить список статусов: " + e.getMessage());
        }

        Runnable updateTable = () -> {
            String status = statusCombo.getValue();
            String meal = mealCombo.getValue();
            if (status == null || meal == null) {
                return;
            }
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> voyages = HibernateManager.getVoyagesByStatusAndMeal(session, status,
                        meal);
                for (Object[] voyage : voyages) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(voyage[0].toString());
                    row.add(voyage[1].toString());
                    data.add(row);
                }
                table.setItems(data);
                errorLabel.setText("");

                if (data.isEmpty()) {
                    errorLabel.setText("Нет данных для выбранного статуса и типа питания");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Не удалось загрузить данные: " + ex.getMessage());
            }
        };

        statusCombo.setOnAction(e -> {
            String status = statusCombo.getValue();
            if (status == null) {
                return;
            }

            mealCombo.getItems().clear();
            table.getItems().clear();
            errorLabel.setText("");

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                boolean hasVoyages = HibernateManager.hasVoyagesWithStatus(session, status);

                if (!hasVoyages) {
                    errorLabel.setText("Нет рейсов с таким статусом");
                    return;
                }

                List<Object[]> mealTypes = HibernateManager.getAvailableMealTypesByStatus(session, status);
                boolean hasMealTypes = false;
                for (Object mealType : mealTypes) {
                    if (mealType instanceof Object[] arr) {
                        hasMealTypes = true;
                        mealCombo.getItems().add(arr[0].toString());
                    } else {
                        hasMealTypes = true;
                        mealCombo.getItems().add(mealType.toString());
                    }
                }
                if (hasMealTypes) {
                    mealCombo.setPromptText("Выберите тип питания");
                    if (mealCombo.getItems().size() == 1) {
                        mealCombo.setValue(mealCombo.getItems().get(0));
                    }
                } else {
                    mealCombo.setPromptText("Нет доступных типов питания");
                    errorLabel.setText("Для рейсов с таким статусом нет билетов с питанием");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Не удалось загрузить типы питания: " + ex.getMessage());
            }
        });

        mealCombo.setOnAction(e -> {
            if (mealCombo.getValue() != null) {
                updateTable.run();
            }
        });

        VBox form = new VBox(10, statusLabel, statusCombo, mealLabel, mealCombo, errorLabel);
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
    
        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));
    
        ObservableList<String> allCabins = FXCollections.observableArrayList();
    
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> voyages = HibernateManager.getActiveVoyages(session);
            for (Object[] voyage : voyages) {
                voyageCombo.getItems().add(voyage[0] + " (IMO: " + voyage[1] + ")");
            }
    
            List<Object[]> vessels = HibernateManager.getVessels(session);
            for (Object[] vessel : vessels) {
                vesselCombo.getItems().add(vessel[0] + " (" + vessel[1] + ")");
            }
    
            List<Object[]> cabins = HibernateManager.getCabins(session);
            for (Object[] cabin : cabins) {
                String cabinItem = cabin[0] + " (IMO: " + cabin[1] +
    ", категория: " + cabin[2] +
    ", вместимость: " + cabin[3] +
    ", окно: " + ((Boolean) cabin[4] ? "да" : "нет") + ")";
                allCabins.add(cabinItem);
            }
            cabinCombo.setItems(FXCollections.observableArrayList(allCabins));
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
        purchaseDateField.setText(java.time.LocalDate.now().toString());
    
        Button addBtn = new Button("Добавить клиента и билет");
        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(250);
    
        addBtn.setOnAction(e -> {
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
    
            java.util.List<String> errors = new java.util.ArrayList<>();
    
            if (!Validator.isNotEmpty(emailField.getText())) {
                errors.add("• Необходимо указать Email");
                emailField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isValidEmail(emailField.getText())) {
                errors.add("• Неверный формат Email");
                emailField.setStyle("-fx-border-color: red;");
            }
    
            if (Validator.isValidEmail(emailField.getText())) {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    if (HibernateManager.checkCustomerExists(session, emailField.getText())) {
                        errors.add("• Клиент с таким Email уже существует");
                        emailField.setStyle("-fx-border-color: red;");
                    }
                } catch (Exception ex) {
                    errors.add("• Ошибка проверки клиента: " + ex.getMessage());
                }
            }
    
            if (!Validator.isNotEmpty(lastNameField.getText())) {
                errors.add("• Необходимо указать фамилию");
                lastNameField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isAlphabetic(lastNameField.getText())) {
                errors.add("• Фамилия должна содержать только буквы");
                lastNameField.setStyle("-fx-border-color: red;");
            }
    
            if (!Validator.isNotEmpty(firstNameField.getText())) {
                errors.add("• Необходимо указать имя");
                firstNameField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isAlphabetic(firstNameField.getText())) {
                errors.add("• Имя должно содержать только буквы");
                firstNameField.setStyle("-fx-border-color: red;");
            }
    
            if (Validator.isNotEmpty(middleNameField.getText()) &&
                    !Validator.isAlphabetic(middleNameField.getText())) {
                errors.add("• Отчество должно содержать только буквы");
                middleNameField.setStyle("-fx-border-color: red;");
            } else {
                middleNameField.setStyle("");
            }
    
            if (!Validator.isValidDate(birthDateField.getText())) {
                errors.add("• Дата рождения должна быть в формате ГГГГ-ММ-ДД");
                birthDateField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isNotFutureDate(birthDateField.getText())) {
                errors.add("• Дата рождения не может быть в будущем");
                birthDateField.setStyle("-fx-border-color: red;");
            }
    
            if (!passportField.getText().matches("^\\d{10}$")) {
                errors.add("• Серия паспорта должна содержать ровно 10 цифр");
                passportField.setStyle("-fx-border-color: red;");
            }
    
            if (voyageCombo.getValue() == null) {
                errors.add("• Необходимо выбрать рейс");
                voyageCombo.setStyle("-fx-border-color: red;");
            }
    
            if (vesselCombo.getValue() == null) {
                errors.add("• Необходимо выбрать судно");
                vesselCombo.setStyle("-fx-border-color: red;");
            }
    
            if (cabinCombo.getValue() == null) {
                errors.add("• Необходимо выбрать каюту");
                cabinCombo.setStyle("-fx-border-color: red;");
            }
    
            if (!Validator.isNumeric(priceField.getText())) {
                errors.add("• Цена должна быть числом");
                priceField.setStyle("-fx-border-color: red;");
            }
    
            if (paymentMethodCombo.getValue() == null) {
                errors.add("• Необходимо выбрать способ оплаты");
                paymentMethodCombo.setStyle("-fx-border-color: red;");
            }
    
            if (mealTypeCombo.getValue() == null) {
                errors.add("• Необходимо выбрать тип питания");
                mealTypeCombo.setStyle("-fx-border-color: red;");
            }
    
            if (!Validator.isInteger(luggageField.getText())) {
                errors.add("• Вес багажа должен быть целым числом");
                luggageField.setStyle("-fx-border-color: red;");
            }
    
            if (!Validator.isValidDate(purchaseDateField.getText())) {
                errors.add("• Дата покупки должна быть в формате ГГГГ-ММ-ДД");
                purchaseDateField.setStyle("-fx-border-color: red;");
            } else if (!Validator.isNotFutureDate(purchaseDateField.getText())) {
                errors.add("• Дата покупки не может быть в будущем");
                purchaseDateField.setStyle("-fx-border-color: red;");
            }
    
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
    
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                try {
                    HibernateManager.addClientAndTicket(
                            session,
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
    
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Клиент и билет успешно добавлены!");
                    showTable("SELECT * FROM maritime_booking.customers", table);
    
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
    
                    voyageCombo.setValue(null);
                    vesselCombo.setValue(null);
                    cabinCombo.getItems().clear();
                } catch (Exception ex) {
                    Label errorLabel = new Label("• " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
            } catch (Exception ex) {
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

        Label voyageLabel = new Label("Выберите рейс:");
        ComboBox<String> voyageCombo = new ComboBox<>();
        voyageCombo.setPromptText("ID рейса");

        Label imoLabel = new Label("IMO судна:");
        Label imoValue = new Label();
        imoValue.setStyle("-fx-font-size: 14px;");

        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));

        Label voyagesLabel = new Label("Таблица рейсов:");
        TableView<ObservableList<String>> voyagesTable = new TableView<>();
        voyagesTable.setPrefHeight(150);

        Label ticketsLabel = new Label("Таблица билетов выбранного рейса:");
        TableView<ObservableList<String>> ticketsTable = new TableView<>();
        ticketsTable.setPrefHeight(150);

        Label stagesLabel = new Label("Таблица этапов выбранного рейса:");
        TableView<ObservableList<String>> stagesTable = new TableView<>();
        stagesTable.setPrefHeight(150);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Voyage> voyages = HibernateManager.getVoyagesWithVessels(session);
            for (Voyage voyage : voyages) {
                voyageCombo.getItems().add(
                        voyage.getId() + " - " + voyage.getVessel().getName() + " (IMO: " + voyage.getVesselId() + ")");
            }
        } catch (Exception e) {
            Label errorLabel = new Label("• Не удалось загрузить данные: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            errorBox.getChildren().add(errorLabel);
        }

        voyageCombo.setOnAction(e -> {
            errorBox.getChildren().clear();
            String voyageStr = voyageCombo.getValue();
            if (voyageStr != null) {
                String imo = voyageStr.substring(voyageStr.indexOf("IMO: ") + 5, voyageStr.indexOf(")"));
                imoValue.setText(imo);

                String voyageId = voyageStr.split(" ")[0];
                try (Session _ = HibernateUtil.getSessionFactory().openSession()) {
                    showTable("SELECT * FROM maritime_booking.tickets WHERE voyage_id = " + voyageId +
                            " AND vessel_id = '" + imo + "'", ticketsTable);
                    showTable("SELECT * FROM maritime_booking.voyage_stages WHERE voyage_id = " + voyageId +
                            " AND vessel_id = '" + imo + "'", stagesTable);
                } catch (Exception ex) {
                    Label errorLabel = new Label("• Не удалось загрузить связанные данные: " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
            } else {
                imoValue.setText("");
                ticketsTable.getItems().clear();
                stagesTable.getItems().clear();
            }
        });

        Button deleteBtn = new Button("Удалить всё связанное");
        deleteBtn.setStyle(
                "-fx-font-weight: bold; -fx-background-color: #E53935; -fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-background-radius: 8;");

        showTable("SELECT * FROM maritime_booking.voyages", voyagesTable);

        deleteBtn.setOnAction(e -> {
            errorBox.getChildren().clear();
            voyageCombo.setStyle("");

            String voyageStr = voyageCombo.getValue();
            if (voyageStr == null) {
                Label errorLabel = new Label("• Выберите рейс!");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
                voyageCombo.setStyle("-fx-border-color: red;");
                return;
            }

            String voyageId = voyageStr.split(" ")[0];
            String vesselId = imoValue.getText();

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение");
            confirmAlert.setHeaderText("Удаление рейса");
            confirmAlert.setContentText("Вы уверены, что хотите удалить рейс " + voyageId +
                    " и все связанные данные? Это действие нельзя отменить.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    HibernateManager.deleteVoyage(session, Integer.parseInt(voyageId), vesselId);

                    Label successLabel = new Label("• Рейс " + voyageId + " и всё связанное удалено!");
                    successLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                    errorBox.getChildren().add(successLabel);

                    voyageCombo.setValue(null);
                    imoValue.setText("");

                    voyageCombo.getItems().clear();
                    List<Voyage> voyages = HibernateManager.getVoyagesWithVessels(session);
                    for (Voyage voyage : voyages) {
                        voyageCombo.getItems().add(voyage.getId() + " - " + voyage.getVessel().getName() + " (IMO: "
                                + voyage.getVesselId() + ")");
                    }

                    showTable("SELECT * FROM maritime_booking.voyages", voyagesTable);
                    ticketsTable.getItems().clear();
                    stagesTable.getItems().clear();

                    ticketsLabel.setText("Таблица билетов выбранного рейса (после удаления)");
                    stagesLabel.setText("Таблица этапов выбранного рейса (после удаления)");
                } catch (Exception ex) {
                    Label errorLabel = new Label("• Ошибка при удалении: " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    errorBox.getChildren().add(errorLabel);
                }
            }
        });

        VBox form = new VBox(10, voyageLabel, voyageCombo, imoLabel, imoValue, errorBox, deleteBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);

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

        VBox errorBox = new VBox(5);
        errorBox.setAlignment(Pos.CENTER_LEFT);
        errorBox.setPadding(new Insets(5, 0, 5, 0));

        Button showFutureBtn = new Button("Показать будущие цены");
        Button updateBtn = new Button("Обновить цены");

        HBox buttonBox = new HBox(10, showFutureBtn, updateBtn);
        buttonBox.setAlignment(Pos.CENTER);

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(350);

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
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> tickets = HibernateManager.getFutureLuggagePrices(session,
                        Double.parseDouble(minWeight), date);
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                int rowCount = 0;
                for (Object[] ticket : tickets) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(ticket[0].toString());
                    row.add(ticket[1].toString());
                    row.add(ticket[2].toString());
                    row.add(ticket[3].toString());
                    row.add(ticket[4].toString());
                    data.add(row);
                    rowCount++;
                }
                table.setItems(data);
                if (rowCount == 0) {
                    Label infoLabel = new Label("• Не найдено билетов по заданным условиям");
                    infoLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
                    errorBox.getChildren().add(infoLabel);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Label errorLabel = new Label("• Не удалось загрузить данные: " + ex.getMessage());
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                errorBox.getChildren().add(errorLabel);
            }
        });

        updateBtn.setOnAction(e -> {
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
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                HibernateManager.updateLuggagePrice(session, Double.parseDouble(minWeight), date);

                Label successLabel = new Label("• Цены успешно обновлены!");
                successLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                errorBox.getChildren().add(successLabel);

                List<Object[]> tickets = HibernateManager.getUpdatedLuggageTickets(session,
                        Double.parseDouble(minWeight), date);
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                int rowCount = 0;
                for (Object[] ticket : tickets) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(ticket[0].toString());
                    row.add(ticket[1].toString());
                    row.add(ticket[2].toString());
                    row.add(ticket[2].toString());
                    row.add(ticket[3].toString());
                    data.add(row);
                    rowCount++;
                }
                table.setItems(data);

                if (rowCount == 0) {
                    Label infoLabel = new Label("• Не найдено билетов, подходящих для обновления");
                    infoLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
                    errorBox.getChildren().add(infoLabel);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> voyages = HibernateManager.getVoyagesWithVesselDetails(session);
            for (Object[] voyage : voyages) {
                voyageCombo.getItems().add(String.format("%s (Судно: %s - %s, Статус: %s)",
                        voyage[0], voyage[1], voyage[2], voyage[3]));
            }
        } catch (Exception e) {
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
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> routeData = HibernateManager.getVoyageRoute(session, Integer.parseInt(voyageId));
                for (Object[] row : routeData) {
                    ObservableList<String> tableRow = FXCollections.observableArrayList();
                    for (Object value : row) {
                        tableRow.add(value != null ? value.toString() : "");
                    }
                    data.add(tableRow);
                }
                table.setItems(data);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }

            ObservableList<ObservableList<String>> routeCitiesData = FXCollections.observableArrayList();
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                String sql = "SELECT vs.stop_number, dp.city AS departure_city, ap.city AS arrival_city " +
                        "FROM maritime_booking.voyage_stages vs " +
                        "JOIN maritime_booking.ports dp ON vs.departure_port_id = dp.un_locode " +
                        "JOIN maritime_booking.ports ap ON vs.arrival_port_id = ap.un_locode " +
                        "WHERE vs.voyage_id = :voyageId " +
                        "ORDER BY vs.stop_number";
                List<Object[]> citiesData = session.createNativeQuery(sql, Object[].class)
                        .setParameter("voyageId", Integer.parseInt(voyageId))
                        .getResultList();
                for (Object[] row : citiesData) {
                    ObservableList<String> cityRow = FXCollections.observableArrayList();
                    for (Object value : row) {
                        cityRow.add(value != null ? value.toString() : "");
                    }
                    routeCitiesData.add(cityRow);
                }
                routeTable.setItems(routeCitiesData);
            } catch (Exception ex) {
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

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> years = HibernateManager.getAvailableYears(session);
            for (Object[] year : years) {
                yearCombo.getItems().add(year[0].toString());
            }
        } catch (Exception e) {
            errorLabel.setText("Не удалось загрузить список лет: " + e.getMessage());
        }

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> monthCol = new TableColumn<>("Месяц");
        monthCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        monthCol.setPrefWidth(100);

        TableColumn<ObservableList<String>, String> countCol = new TableColumn<>("Количество билетов");
        countCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        countCol.setPrefWidth(150);

        TableColumn<ObservableList<String>, String> revenueCol = new TableColumn<>("Выручка");
        revenueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        revenueCol.setPrefWidth(150);

        table.getColumns().add(monthCol);
        table.getColumns().add(countCol);
        table.getColumns().add(revenueCol);

        yearCombo.setOnAction(e -> {
            String year = yearCombo.getValue();
            if (year == null) {
                errorLabel.setText("Выберите год!");
                return;
            }

            table.getItems().clear();

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> sales = HibernateManager.getTicketSales(session, year);
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                for (Object[] sale : sales) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(sale[0].toString());
                    row.add(sale[1].toString());
                    row.add(sale[2].toString());
                    data.add(row);
                }
                table.setItems(data);
                errorLabel.setText("");
            } catch (Exception ex) {
                errorLabel.setText("Ошибка загрузки данных: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, yearLabel, yearCombo, errorLabel);
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

        Label countryLabel = new Label("Выберите страну:");
        ComboBox<String> countryCombo = new ComboBox<>();
        countryCombo.setPromptText("Страна");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<String> countryNames = HibernateManager.getCountriesWithInsuredTickets(session);
            countryCombo.getItems().addAll(countryNames);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить список стран: " + e.getMessage());
        }

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<ObservableList<String>, String> idCol = new TableColumn<>("ID билета");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        idCol.setPrefWidth(100);

        TableColumn<ObservableList<String>, String> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        priceCol.setPrefWidth(100);

        table.getColumns().add(idCol);
        table.getColumns().add(priceCol);

        countryCombo.setOnAction(e -> {
            String country = countryCombo.getValue();
            if (country == null) {
                return;
            }

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> tickets = HibernateManager.getInsuredTicketsByCountry(session, country);
                for (Object[] ticket : tickets) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(ticket[0].toString());
                    row.add(ticket[1].toString());
                    data.add(row);
                }
                table.setItems(data);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10, countryLabel, countryCombo);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);

        vbox.getChildren().clear();
        vbox.getChildren().addAll(title, form, table);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createCustomQueryTab() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Произвольный SQL-запрос");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea queryArea = new TextArea();
        queryArea.setPromptText("Введите SQL-запрос...");
        queryArea.setPrefRowCount(5);
        queryArea.setWrapText(true);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button executeBtn = new Button("Выполнить запрос");

        TableView<ObservableList<String>> table = new TableView<>();
        table.setPrefHeight(400);

        executeBtn.setOnAction(e -> {
            String sqlQueryString = queryArea.getText().trim();
            if (sqlQueryString.isEmpty()) {
                errorLabel.setText("Введите SQL-запрос!");
                return;
            }

            if (!sqlQueryString.toLowerCase().startsWith("select")) {
                errorLabel.setText("Разрешены только SELECT SQL запросы!");
                return;
            }

            table.getColumns().clear();
            table.getItems().clear();
            errorLabel.setText("");

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Object[]> results = HibernateManager.executeCustomQuery(session, sqlQueryString);

                if (results != null && !results.isEmpty()) {
                    Object firstRow = results.get(0);
                    int columnCount;

                    if (firstRow.getClass().isArray()) {
                        columnCount = ((Object[]) firstRow).length;
                    } else {
                        columnCount = 1;
                    }

                    for (int i = 0; i < columnCount; i++) {
                        final int colIndex = i;
                        TableColumn<ObservableList<String>, String> col = new TableColumn<>("Column " + (i + 1));
                        col.setCellValueFactory(
                                data -> {
                                    if (data.getValue() != null && colIndex < data.getValue().size()) {
                                        return new javafx.beans.property.SimpleStringProperty(
                                                data.getValue().get(colIndex));
                                    }
                                    return new javafx.beans.property.SimpleStringProperty("");
                                });
                        col.setPrefWidth(120);
                        table.getColumns().add(col);
                    }

                    ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                    for (Object rowObject : results) {
                        ObservableList<String> rowData = FXCollections.observableArrayList();
                        if (rowObject.getClass().isArray()) {
                            for (Object value : (Object[]) rowObject) {
                                rowData.add(value != null ? value.toString() : "");
                            }
                        } else {
                            rowData.add(rowObject != null ? rowObject.toString() : "");
                        }
                        data.add(rowData);
                    }
                    table.setItems(data);
                }

                if (table.getItems().isEmpty()) {
                    errorLabel.setText(
                            "Запрос выполнен успешно, но не вернул данных или результат не является списком массивов объектов.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                String userMessage;
                String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
                if (msg.contains("syntax")) {
                    userMessage = "Ошибка синтаксиса SQL. Проверьте правильность запроса.";
                } else if (msg.contains("permission")) {
                    userMessage = "Недостаточно прав для выполнения запроса.";
                } else if (msg.contains("relation") || msg.contains("table") || msg.contains("column")) {
                    userMessage = "Указанная таблица, поле или связь не найдены.";
                } else if (msg.contains("timeout")) {
                    userMessage = "Время ожидания ответа от базы данных истекло.";
                } else if (msg.contains("connection")) {
                    userMessage = "Ошибка соединения с базой данных.";
                } else {
                    userMessage = "Ошибка выполнения SQL запроса: " + ex.getMessage();
                }
                errorLabel.setText(userMessage);
            }
        });

        VBox form = new VBox(10, queryArea, errorLabel, executeBtn);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(800);

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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> results = HibernateManager.executeCustomQuery(session, sql);
            if (!results.isEmpty()) {
                Object[] firstRow = results.get(0);
                for (int i = 0; i < firstRow.length; i++) {
                    final int colIndex = i;
                    TableColumn<ObservableList<String>, String> col = new TableColumn<>("Column " + (i + 1));
                    col.setCellValueFactory(
                            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(colIndex)));
                    table.getColumns().add(col);
                }

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                for (Object[] row : results) {
                    ObservableList<String> rowData = FXCollections.observableArrayList();
                    for (Object value : row) {
                        rowData.add(value != null ? value.toString() : "");
                    }
                    data.add(rowData);
                }
                table.setItems(data);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить таблицу: " + e.getMessage());
        }
    }
}