-- 1. Tworzenie bazy danych (opcjonalne, jeśli już ją masz)
CREATE DATABASE IF NOT EXISTS logistics_db;
USE logistics_db;

-- 2. Usuwanie tabel w odpowiedniej kolejności (najpierw tabela z kluczem obcym)
DROP TABLE IF EXISTS packages;
DROP TABLE IF EXISTS couriers;

-- 3. Tworzenie tabeli kurierów
CREATE TABLE couriers (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          vehicle_type VARCHAR(50) NOT NULL,
                          max_capacity DOUBLE NOT NULL
) ENGINE=InnoDB;

-- 4. Tworzenie tabeli paczek
-- Relacja ON DELETE CASCADE sprawia, że usunięcie kuriera automatycznie czyści jego paczki (opcjonalnie)
-- W Twoim przypadku wolisz manualne przypisanie, więc zostawiamy standardowy klucz.
CREATE TABLE packages (
                          id INT PRIMARY KEY, -- Ręczne ID paczki zgodnie z Twoim formularzem
                          item_name VARCHAR(255) NOT NULL,
                          weight DOUBLE NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          x DOUBLE NOT NULL,
                          y DOUBLE NOT NULL,
                          courier_id INT,
                          FOREIGN KEY (courier_id) REFERENCES couriers(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 5. Wstawianie przykładowych kurierów
INSERT INTO couriers (name, vehicle_type, max_capacity) VALUES
                                                            ('Adam Kowalski', 'Samochód', 500.0),
                                                            ('Marek Nowak', 'Skuter', 50.0),
                                                            ('Anna Wiśniewska', 'Dostawczy', 1500.0);

-- 6. Wstawianie przykładowych paczek dla pierwszego kuriera (ID: 1)
-- Rozmieściłem punkty X i Y tak, abyś mógł przetestować optymalizację trasy (Canvas)
INSERT INTO packages (id, item_name, weight, address, x, y, courier_id) VALUES
                                                                            (101, 'Telewizor LED', 15.5, 'ul. Prosta 10, Warszawa', 120.0, 340.0, 1),
                                                                            (102, 'Książki', 2.0, 'ul. Krzywa 5, Warszawa', 50.0, 150.0, 1),
                                                                            (103, 'Karma dla psa', 10.0, 'ul. Nowa 1, Warszawa', 200.0, 50.0, 1),
                                                                            (104, 'Monitor 27 cali', 6.5, 'ul. Jasna 44, Warszawa', 300.0, 400.0, 1);

-- 7. Przykładowa paczka dla drugiego kuriera (ID: 2)
INSERT INTO packages (id, item_name, weight, address, x, y, courier_id) VALUES
    (201, 'Smartfon', 0.5, 'ul. Polna 2, Warszawa', 10.0, 20.0, 2);