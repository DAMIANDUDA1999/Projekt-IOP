package pl.logistics.model;

public class Package {
    private int id;
    private String itemName;
    private double weight;
    private String address;
    private double x, y;

    public Package(int id, String itemName, double weight, String address, double x, double y) {
        this.id = id;
        this.itemName = itemName;
        this.weight = weight;
        this.address = address;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public String getItemName() { return itemName; }
    public double getWeight() { return weight; }
    public String getAddress() { return address; }
    public double getX() { return x; }
    public double getY() { return y; }
}