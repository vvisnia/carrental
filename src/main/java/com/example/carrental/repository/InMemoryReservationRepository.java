package com.example.carrental.repository;

import com.example.carrental.domain.CarType;
import com.example.carrental.domain.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InMemoryReservationRepository implements ReservationRepository {
    private final CopyOnWriteArrayList<Reservation> storage = new CopyOnWriteArrayList<>();

    @Override
    public Reservation save(Reservation reservation) {
        storage.add(reservation);
        return reservation;
    }

    @Override
    public List<Reservation> findByType(CarType type) {
        return storage.stream().filter(r -> r.carType() == type).collect(Collectors.toList());
    }

    @Override
    public Optional<Reservation> findById(String id) {
        if (id == null) return Optional.empty();
        return storage.stream().filter(r -> r.id().equals(id)).findFirst();
    }

    @Override
    public boolean deleteById(String id) {
        return storage.removeIf(r -> r.id().equals(id));
    }
}
