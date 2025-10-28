package com.example.carrental.domain;

import com.example.carrental.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

public final class ReservationRequest {
    private final CarType carType;
    private final LocalDateTime startDateTime;
    private final int days;

    public ReservationRequest(CarType carType, LocalDateTime startDateTime, int days) {
        this.carType = Objects.requireNonNull(carType, "carType must not be null");
        this.startDateTime = Objects.requireNonNull(startDateTime, "startDateTime must not be null");
        if (days < 1) throw new ValidationException("days must be >= 1");
        this.days = days;
    }

    public CarType carType() { return carType; }
    public LocalDateTime startDateTime() { return startDateTime; }
    public int days() { return days; }
}
