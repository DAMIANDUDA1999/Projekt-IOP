package pl.logistics.model;

import java.util.ArrayList;
import java.util.List;

public class Courier {
    private int id;
    private String name;
    private String vehicleType;
    private double maxCapacity;
    private List<Package> assignedPackages = new ArrayList<>();

    public Courier(int id, String name, String vehicleType, double maxCapacity) {
        this.id = id;
        this.name = name;
        this.vehicleType = vehicleType;
        this.maxCapacity = maxCapacity;
    }

    public void addPackage(Package p) {
        assignedPackages.add(p);
    }

    // Ta metoda jest kluczowa dla Twojej kolumny "Obciążenie" w TableView
    public double getCurrentLoad() {
        return Math.round(assignedPackages.stream()
                .mapToDouble(Package::getWeight).sum() * 100.0) / 100.0;
    }

    /**
     * Układa paczki od najbliższej do najdalszej (startując z punktu 0,0)
     */
    public void optimizeByDistance() {
        if (assignedPackages.isEmpty()) return;

        List<Package> optimized = new ArrayList<>();
        double currentX = 0, currentY = 0; // Start z bazy
        List<Package> toVisit = new ArrayList<>(assignedPackages);

        while (!toVisit.isEmpty()) {
            Package closest = null;
            double minDist = Double.MAX_VALUE;

            for (Package p : toVisit) {
                double dist = Math.sqrt(Math.pow(p.getX() - currentX, 2) + Math.pow(p.getY() - currentY, 2));
                if (dist < minDist) {
                    minDist = dist;
                    closest = p;
                }
            }

            optimized.add(closest);
            currentX = closest.getX();
            currentY = closest.getY();
            toVisit.remove(closest);
        }
        this.assignedPackages = optimized;
    }

    // Gettery
    public int getId() { return id; }
    public String getName() { return name; }
    public String getVehicleType() { return vehicleType; }
    public double getMaxCapacity() { return maxCapacity; }
    public List<Package> getAssignedPackages() { return new ArrayList<>(assignedPackages); }
}