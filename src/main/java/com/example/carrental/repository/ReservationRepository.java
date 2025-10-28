package com.example.carrental.repository;

import com.example.carrental.domain.CarType;
import com.example.carrental.domain.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    List<Reservation> findByType(CarType type);
    Optional<Reservation> findById(String id);
    boolean deleteById(String id);
}
