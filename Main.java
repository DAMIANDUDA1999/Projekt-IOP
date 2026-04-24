package pl.logistics;

import javafx.application.Application;
import pl.logistics.model.Courier;

/**
 * Główna klasa wejściowa systemu.
 * Naprawia błędy konstruktora poprzez podanie 4 wymaganych parametrów:
 * ID, Imię, Środek Transportu, Udźwig.
 */
public class Main {
    public static void main(String[] args) {
        // Logowanie startu systemu w konsoli
        System.out.println("=== LOGISTICS SYSTEM v4.0 STARTING ===");

        // Przykład poprawnego tworzenia obiektu (naprawia błąd "cannot be applied to given types")
        // Parametry: (int id, String name, String vehicleType, double maxCapacity)
        Courier testCourier = new Courier(0, "System Test", "Virtual", 1000.0);

        System.out.println("Weryfikacja modelu: Kurier " + testCourier.getName() +
                " korzysta z transportu: " + testCourier.getVehicleType());

        // Uruchomienie interfejsu graficznego (AppGUI)
        // To wywołanie przekazuje kontrolę do JavaFX
        Application.launch(AppGUI.class, args);
    }
}