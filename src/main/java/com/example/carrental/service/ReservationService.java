package com.example.carrental.service;

import com.example.carrental.domain.*;
import com.example.carrental.exception.NoAvailabilityException;
import com.example.carrental.exception.ValidationException;
import com.example.carrental.repository.InventoryRepository;
import com.example.carrental.repository.ReservationRepository;
import com.example.carrental.util.IdGenerator;
import com.example.carrental.util.Preconditions;
import com.example.carrental.util.UuidIdGenerator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationService {
    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final AvailabilityService availabilityService;
    private final Clock clock;
    private final IdGenerator idGenerator;
    private final int maxHorizonDays = 365;

    public ReservationService(InventoryRepository inventoryRepository, ReservationRepository reservationRepository) {
        this(inventoryRepository, reservationRepository, Clock.systemDefaultZone(), new UuidIdGenerator());
    }

    public ReservationService(InventoryRepository inventoryRepository, ReservationRepository reservationRepository, Clock clock, IdGenerator idGenerator) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.availabilityService = new AvailabilityService(inventoryRepository, reservationRepository);
        this.clock = clock;
        this.idGenerator = idGenerator;
    }

    public Reservation reserve(ReservationRequest request) {
        validate(request);
        DateTimeRange range = DateTimeRange.of(request.startDateTime(), request.days());
        if (!availabilityService.isAvailable(request.carType(), range)) {
            throw new NoAvailabilityException("No availability for " + request.carType() + " in range " + range);
        }
        String id = idGenerator.generate();
        LocalDateTime createdAt = LocalDateTime.now(clock);
        Reservation reservation = new Reservation(id, request.carType(), range, createdAt);
        return reservationRepository.save(reservation);
    }

    public boolean cancel(String reservationId) {
        Preconditions.notNull(reservationId, "reservationId must not be null");
        return reservationRepository.deleteById(reservationId);
    }

    public boolean isAvailable(CarType type, LocalDateTime startDateTime, int days) {
        Preconditions.notNull(type, "carType must not be null");
        Preconditions.notNull(startDateTime, "startDateTime must not be null");
        if (days < 1) throw new ValidationException("days must be >= 1");
        return availabilityService.isAvailable(type, DateTimeRange.of(startDateTime, days));
    }

    private void validate(ReservationRequest request) {
        Preconditions.notNull(request, "request must not be null");
        Preconditions.notNull(request.carType(), "carType must not be null");
        Preconditions.notNull(request.startDateTime(), "startDateTime must not be null");
        if (request.days() < 1) throw new ValidationException("days must be >= 1");
        int total = inventoryRepository.totalFor(request.carType());
        if (total <= 0) throw new ValidationException("Inventory for type " + request.carType() + " must be > 0");
        LocalDate latest = LocalDate.now(clock).plusDays(maxHorizonDays);
        if (request.startDateTime().toLocalDate().isAfter(latest)) throw new ValidationException("startDateTime exceeds booking horizon");
        if (request.startDateTime().isBefore(LocalDateTime.now(clock))) throw new ValidationException("startDateTime must not be in the past");
    }
}
