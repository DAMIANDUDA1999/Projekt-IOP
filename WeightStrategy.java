package pl.logistics.strategy;

import pl.logistics.model.Package;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WeightStrategy implements RouteStrategy {
    @Override
    public List<Package> optimize(List<Package> packages) {
        return packages.stream()
                .sorted(Comparator.comparingDouble(Package::getWeight).reversed())
                .collect(Collectors.toList());
    }
}