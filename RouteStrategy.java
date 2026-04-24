package pl.logistics.strategy;

import pl.logistics.model.Package;
import java.util.List;

public interface RouteStrategy {
    List<Package> optimize(List<Package> packages);
}