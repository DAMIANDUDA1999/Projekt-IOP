package pl.logistics;

import pl.logistics.model.Courier;
import java.util.ArrayList;
import java.util.List;

public class LogisticManager {
    private List<Courier> couriers = new ArrayList<>();

    public void addCourier(Courier courier) {
        couriers.add(courier);
    }

    public List<Courier> getCouriers() {
        return couriers;
    }
}