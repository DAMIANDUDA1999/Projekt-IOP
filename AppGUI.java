package pl.logistics;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Kluczowy import dla kolumn
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pl.logistics.model.Courier;
import pl.logistics.model.Package;
import pl.logistics.view.RouteCanvas;

import java.sql.*;
import java.util.List;

public class AppGUI extends Application {
    private TableView<Courier> table = new TableView<>();
    private ObservableList<Courier> courierList = FXCollections.observableArrayList();

    private final String DB_URL = "jdbc:mysql://192.168.4.113:3306/logistics_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "password123";

    // Formularz Kuriera
    private TextField cName = new TextField() {{ setPromptText("Imię"); }};
    private TextField cVehicle = new TextField() {{ setPromptText("Pojazd"); }};
    private TextField cCap = new TextField() {{ setPromptText("Udźwig (kg)"); }};

    // Formularz Paczki
    private TextField pId = new TextField() {{ setPromptText("ID"); setPrefWidth(50); }};
    private TextField pItem = new TextField() {{ setPromptText("Towar"); }};
    private TextField pWeight = new TextField() {{ setPromptText("Waga"); setPrefWidth(60); }};
    private TextField pAddr = new TextField() {{ setPromptText("Adres"); }};
    private TextField pX = new TextField() {{ setPromptText("X"); setPrefWidth(40); }};
    private TextField pY = new TextField() {{ setPromptText("Y"); setPrefWidth(40); }};
    private TextField pCid = new TextField() {{ setPromptText("ID Kuriera"); setPrefWidth(70); }};

    @Override
    public void start(Stage primaryStage) {
        setupTable();
        loadDataFromDB();

        Button btnAddC = new Button("Dodaj Kuriera") {{ setOnAction(e -> handleAddCourier()); }};
        Button btnAddP = new Button("Dodaj Paczkę") {{ setOnAction(e -> handleAddPackage()); }};
        Button btnDetails = new Button("Pokaż Trasę") {{ setOnAction(e -> handleShowDetails()); }};
        Button btnDelC = new Button("Usuń Kuriera") {{ setOnAction(e -> handleDeleteCourier()); }};

        VBox layout = new VBox(15,
                new Label("AKTYWNA FLOTA:"), table,
                new HBox(10, btnDetails, btnDelC),
                new Separator(),
                new Label("DODAJ KURIERA:"), new HBox(10, cName, cVehicle, cCap, btnAddC),
                new Label("NADAJ PACZKĘ:"), new HBox(5, pId, pItem, pWeight, pAddr, pX, pY, pCid, btnAddP)
        );
        layout.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(layout, 1250, 800));
        primaryStage.setTitle("System Logistyczny - Dashboard");
        primaryStage.show();
    }

    private void setupTable() {
        TableColumn<Courier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id")); // LINIĘ 68 POPRAWIONO TUTAJ

        TableColumn<Courier, String> nameCol = new TableColumn<>("Kurier");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Courier, String> vCol = new TableColumn<>("Pojazd");
        vCol.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));

        TableColumn<Courier, Double> loadCol = new TableColumn<>("Obciążenie");
        loadCol.setCellValueFactory(new PropertyValueFactory<>("currentLoad"));

        TableColumn<Courier, Double> maxCol = new TableColumn<>("Limit");
        maxCol.setCellValueFactory(new PropertyValueFactory<>("maxCapacity"));

        table.getColumns().setAll(idCol, nameCol, vCol, loadCol, maxCol);
        table.setItems(courierList);
    }

    private void loadDataFromDB() {
        courierList.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            ResultSet rsC = conn.createStatement().executeQuery("SELECT * FROM couriers");
            while (rsC.next()) {
                Courier c = new Courier(rsC.getInt("id"), rsC.getString("name"), rsC.getString("vehicle_type"), rsC.getDouble("max_capacity"));
                PreparedStatement psP = conn.prepareStatement("SELECT * FROM packages WHERE courier_id = ?");
                psP.setInt(1, c.getId());
                ResultSet rsP = psP.executeQuery();
                while (rsP.next()) {
                    c.addPackage(new Package(
                            rsP.getInt("id"),
                            rsP.getString("item_name"),
                            rsP.getDouble("weight"),
                            rsP.getString("address"),
                            rsP.getDouble("x"),
                            rsP.getDouble("y")
                    ));
                }
                courierList.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleAddCourier() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO couriers (name, vehicle_type, max_capacity) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cName.getText());
            ps.setString(2, cVehicle.getText());
            ps.setDouble(3, Double.parseDouble(cCap.getText()));
            ps.executeUpdate();
            loadDataFromDB();
            cName.clear(); cVehicle.clear(); cCap.clear();
        } catch (Exception e) { showAlert("Błąd", "Nieprawidłowe dane kuriera."); }
    }

    private void handleAddPackage() {
        try {
            int cid = Integer.parseInt(pCid.getText());
            Courier target = courierList.stream().filter(c -> c.getId() == cid).findFirst().orElse(null);

            if (target == null) {
                showAlert("Błąd", "Nie znaleziono kuriera o ID: " + cid);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO packages (id, item_name, weight, address, x, y, courier_id) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(pId.getText()));
                ps.setString(2, pItem.getText());
                ps.setDouble(3, Double.parseDouble(pWeight.getText()));
                ps.setString(4, pAddr.getText());
                ps.setDouble(5, Double.parseDouble(pX.getText()));
                ps.setDouble(6, Double.parseDouble(pY.getText()));
                ps.setInt(7, cid);
                ps.executeUpdate();
                loadDataFromDB();
                pId.clear(); pItem.clear(); pWeight.clear(); pAddr.clear(); pX.clear(); pY.clear(); pCid.clear();
            }
        } catch (Exception e) { showAlert("Błąd", "Sprawdź pola paczki. ID, Waga, X, Y muszą być liczbami."); }
    }

    private void handleShowDetails() {
        Courier s = table.getSelectionModel().getSelectedItem();
        if (s == null) return;

        Stage sub = new Stage();
        RouteCanvas canvas = new RouteCanvas(600, 400);
        ListView<String> lv = new ListView<>();
        lv.setPrefHeight(200);

        // Odświeżanie widoku
        Runnable refresh = () -> {
            lv.getItems().clear();
            // Ponownie wczytujemy dane z bazy, aby mieć pewność co do aktualności
            loadDataFromDB();
            // Szukamy aktualnego obiektu kuriera po ID (bo loadData go odświeżył)
            Courier updatedCourier = courierList.stream()
                    .filter(c -> c.getId() == s.getId())
                    .findFirst().orElse(s);

            List<Package> pkgs = updatedCourier.getAssignedPackages();
            for (Package p : pkgs) {
                lv.getItems().add(String.format("ID:%d | %s", p.getId(), p.getItemName()));
            }
            canvas.drawRoute(pkgs);
        };

        // LOGIKA USUWANIA PACZKI
        Button btnDeleteP = new Button("Usuń zaznaczoną paczkę");
        btnDeleteP.setStyle("-fx-base: #e74c3c;"); // Czerwony przycisk
        btnDeleteP.setOnAction(e -> {
            String selected = lv.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            // Wyciągamy ID z tekstu "ID:123 | Nazwa"
            int packageId = Integer.parseInt(selected.split(" ")[0].replace("ID:", ""));

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "DELETE FROM packages WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, packageId);
                ps.executeUpdate();

                refresh.run(); // Odśwież okno detali i mapę
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button btnOpt = new Button("Optymalizuj Trasę");
        btnOpt.setOnAction(e -> { s.optimizeByDistance(); refresh.run(); });

        refresh.run();

        VBox layout = new VBox(10,
                new Label("Mapa Trasy:"), canvas,
                new Label("Lista paczek:"), lv,
                new HBox(10, btnOpt, btnDeleteP)
        );
        layout.setPadding(new Insets(15));
        sub.setScene(new Scene(layout));
        sub.setTitle("Zarządzanie paczkami: " + s.getName());
        sub.show();
    }

    private void handleDeleteCourier() {
        Courier toDelete = table.getSelectionModel().getSelectedItem();
        if (toDelete == null) return;

        // 1. Znajdź innego kuriera, który przejmie paczki
        Courier successor = courierList.stream()
                .filter(c -> c.getId() != toDelete.getId())
                .findFirst()
                .orElse(null);

        if (successor == null && !toDelete.getAssignedPackages().isEmpty()) {
            showAlert("Błąd", "Nie można usunąć jedynego kuriera, który ma przypisane paczki! Dodaj najpierw kogoś innego.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false); // Rozpoczynamy transakcję

            try {
                if (successor != null) {
                    // 2. Przerzuć paczki na następcę w bazie danych
                    String updateSql = "UPDATE packages SET courier_id = ? WHERE courier_id = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                    psUpdate.setInt(1, successor.getId());
                    psUpdate.setInt(2, toDelete.getId());
                    psUpdate.executeUpdate();
                }

                // 3. Usuń kuriera
                String deleteSql = "DELETE FROM couriers WHERE id = ?";
                PreparedStatement psDelete = conn.prepareStatement(deleteSql);
                psDelete.setInt(1, toDelete.getId());
                psDelete.executeUpdate();

                conn.commit(); // Zatwierdź zmiany

                String msg = (successor != null) ?
                        "Kurier usunięty. Paczki przejął: " + successor.getName() :
                        "Kurier usunięty.";

                loadDataFromDB();
                showAlert("Sukces", msg);

            } catch (SQLException ex) {
                conn.rollback(); // W razie błędu cofnij wszystko
                throw ex;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd", "Problem z bazą danych podczas usuwania.");
        }
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}