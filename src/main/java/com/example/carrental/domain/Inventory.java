package com.example.carrental.domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Inventory {
    private final Map<CarType, Integer> totals = new EnumMap<>(CarType.class);

    public Inventory(int sedans, int suvs, int vans) {
        totals.put(CarType.SEDAN, requireNonNegative(sedans, "sedans"));
        totals.put(CarType.SUV, requireNonNegative(suvs, "suvs"));
        totals.put(CarType.VAN, requireNonNegative(vans, "vans"));
    }

    public int totalFor(CarType type) {
        return totals.getOrDefault(Objects.requireNonNull(type), 0);
    }

    private int requireNonNegative(int value, String field) {
        if (value < 0) throw new IllegalArgumentException(field + " must be >= 0");
        return value;
    }

    public Map<CarType, Integer> asMap() { return Map.copyOf(totals); }
}
