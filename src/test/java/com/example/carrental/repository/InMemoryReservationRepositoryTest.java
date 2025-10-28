package com.example.carrental.repository;

import com.example.carrental.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryReservationRepositoryTest {

    @Test
    void saveAndFindByTypeWorks() {
        InMemoryReservationRepository repo = new InMemoryReservationRepository();
        Reservation r = new Reservation("id", CarType.SEDAN, DateTimeRange.of(LocalDateTime.of(2025,10,1,10,0), 1), LocalDateTime.now());
        repo.save(r);
        assertEquals(1, repo.findByType(CarType.SEDAN).size());
        assertEquals(0, repo.findByType(CarType.SUV).size());
    }
}
