package pl.logistics.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pl.logistics.model.Package;
import java.util.List;

public class RouteCanvas extends Canvas {

    public RouteCanvas(double width, double height) {
        super(width, height);
        clear();
    }

    public void clear() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.web("#f8f9fa"));
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawRoute(List<Package> packages) {
        clear();
        if (packages == null || packages.isEmpty()) return;

        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        double margin = 50;

        // 1. Skalowanie (uwzględniamy bazę 0,0 i wszystkie paczki)
        double maxX = Math.max(10, packages.stream().mapToDouble(Package::getX).max().orElse(0));
        double maxY = Math.max(10, packages.stream().mapToDouble(Package::getY).max().orElse(0));

        java.util.function.Function<Double, Double> scaleX = (x) -> margin + (x / maxX) * (w - 2 * margin);
        java.util.function.Function<Double, Double> scaleY = (y) -> h - (margin + (y / maxY) * (h - 2 * margin));

        // 2. Obliczanie całkowitego dystansu
        double totalDist = calculateTotalDistance(packages);

        // 3. Rysowanie linii trasy
        gc.setStroke(Color.web("#3498db"));
        gc.setLineWidth(2.5);

        double prevX = scaleX.apply(0.0);
        double prevY = scaleY.apply(0.0);

        for (Package p : packages) {
            double curX = scaleX.apply(p.getX());
            double curY = scaleY.apply(p.getY());
            gc.strokeLine(prevX, prevY, curX, curY);
            prevX = curX;
            prevY = curY;
        }

        // 4. Rysowanie punktów i etykiet
        for (Package p : packages) {
            double px = scaleX.apply(p.getX());
            double py = scaleY.apply(p.getY());
            gc.setFill(Color.web("#e74c3c"));
            gc.fillOval(px - 6, py - 6, 12, 12);
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("System", FontWeight.BOLD, 12));
            gc.fillText("ID: " + p.getId(), px + 10, py - 5);
        }

        // 5. Rysowanie BAZY
        gc.setFill(Color.web("#27ae60"));
        gc.fillOval(scaleX.apply(0.0) - 10, scaleY.apply(0.0) - 10, 20, 20);
        gc.fillText("START (Baza)", scaleX.apply(0.0) + 15, scaleY.apply(0.0) + 5);

        // 6. WYŚWIETLANIE WYNIKU (Podsumowanie dystansu)
        drawDistanceInfo(gc, totalDist);
    }

    private double calculateTotalDistance(List<Package> packages) {
        double distance = 0;
        double currentX = 0;
        double currentY = 0;

        for (Package p : packages) {
            // Wzór euklidesowy: sqrt( (x2-x1)^2 + (y2-y1)^2 )
            distance += Math.sqrt(Math.pow(p.getX() - currentX, 2) + Math.pow(p.getY() - currentY, 2));
            currentX = p.getX();
            currentY = p.getY();
        }
        return distance;
    }

    private void drawDistanceInfo(GraphicsContext gc, double distance) {
        gc.setFill(Color.web("#2c3e50"));
        gc.fillRect(0, getHeight() - 30, getWidth(), 30); // Pasek na dole

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        String text = String.format("Całkowita długość trasy: %.2f jednostek", distance);
        gc.fillText(text, 15, getHeight() - 10);
    }
}