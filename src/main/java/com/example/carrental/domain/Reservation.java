package com.example.carrental.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Reservation {
    private final String id;
    private final CarType carType;
    private final DateTimeRange dateTimeRange;
    private final LocalDateTime createdAt;

    public Reservation(String id, CarType carType, DateTimeRange dateTimeRange, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.carType = Objects.requireNonNull(carType, "carType must not be null");
        this.dateTimeRange = Objects.requireNonNull(dateTimeRange, "dateTimeRange must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public String id() { return id; }
    public CarType carType() { return carType; }
    public DateTimeRange dateTimeRange() { return dateTimeRange; }
    public LocalDateTime createdAt() { return createdAt; }
}
