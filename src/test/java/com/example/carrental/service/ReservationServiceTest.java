package com.example.carrental.service;

import com.example.carrental.domain.*;
import com.example.carrental.exception.NoAvailabilityException;
import com.example.carrental.exception.ValidationException;
import com.example.carrental.repository.*;
import com.example.carrental.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private ReservationService service;

    @BeforeEach
    void setUp() {
        Inventory inventory = new Inventory(2, 1, 1);
        InventoryRepository invRepo = new InMemoryInventoryRepository(inventory);
        ReservationRepository resRepo = new InMemoryReservationRepository();
        service = new ReservationService(invRepo, resRepo, Clock.fixed(Instant.parse("2025-10-01T00:00:00Z"), ZoneId.of("UTC")), () -> "x");
    }

    @Test
    void reserve_successWhenCapacitySufficient() {
        ReservationRequest req = new ReservationRequest(CarType.SEDAN, LocalDateTime.of(2025,10,10,9,0), 2);
        Reservation res1 = service.reserve(req);
        assertNotNull(res1.id());
    }

    @Test
    void reserve_failsWhenDaysLessThanOne() {
        assertThrows(ValidationException.class, () -> new ReservationRequest(CarType.SEDAN, LocalDateTime.now(), 0));
    }

    @Test
    void reserve_failsWhenInventoryZero() {
        InventoryRepository zeroInv = type -> 0;
        ReservationRepository repo = new InMemoryReservationRepository();
        ReservationService s = new ReservationService(zeroInv, repo, Clock.fixed(Instant.parse("2025-10-01T00:00:00Z"), ZoneId.of("UTC")), () -> "id");
        ReservationRequest req = new ReservationRequest(CarType.VAN, LocalDateTime.of(2025,10,2,10,0), 1);
        assertThrows(ValidationException.class, () -> s.reserve(req));
    }

    @Test
    void overlapAtHourLevelIsDetected() {
        LocalDateTime start = LocalDateTime.of(2025,10,20,10,0);
        service.reserve(new ReservationRequest(CarType.SEDAN, start, 1));
        service.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        assertThrows(NoAvailabilityException.class, () -> service.reserve(new ReservationRequest(CarType.SEDAN, start.plusHours(1), 1)));
        assertNotNull(service.reserve(new ReservationRequest(CarType.SEDAN, start.plusDays(1), 1)).id());
    }

    @Test
    void boundariesAllowTouchingAtEnd() {
        LocalDateTime start = LocalDateTime.of(2025,11,1,8,0);
        service.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        assertNotNull(service.reserve(new ReservationRequest(CarType.SEDAN, start.plusDays(2), 1)).id());
    }

    @Test
    void cancel_freesCapacity() {
        LocalDateTime start = LocalDateTime.of(2025,12,31,9,0);
        Reservation r1 = service.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        Reservation r2 = service.reserve(new ReservationRequest(CarType.SEDAN, start, 2));
        assertThrows(NoAvailabilityException.class, () -> service.reserve(new ReservationRequest(CarType.SEDAN, start, 1)));
        assertTrue(service.cancel(r1.id()));
        assertNotNull(service.reserve(new ReservationRequest(CarType.SEDAN, start, 1)).id());
        assertTrue(service.cancel(r2.id()));
    }

    @Test
    void isAvailable_exposedForClients() {
        LocalDateTime start = LocalDateTime.of(2025,10,5,13,30);
        assertTrue(service.isAvailable(CarType.SEDAN, start, 1));
    }

    @Test
    void createdAtAndIdDeterministicWithInjectedClockAndIdGenerator() {
        Inventory inv = new Inventory(1, 0, 0);
        InventoryRepository invRepo = new InMemoryInventoryRepository(inv);
        ReservationRepository resRepo = new InMemoryReservationRepository();
        Clock fixed = Clock.fixed(Instant.parse("2025-10-01T12:34:56Z"), ZoneId.of("UTC"));
        IdGenerator ids = () -> "fixed-id";
        ReservationService s = new ReservationService(invRepo, resRepo, fixed, ids);
        Reservation r = s.reserve(new ReservationRequest(CarType.SEDAN, LocalDateTime.of(2025,10,2,10,0), 1));
        assertEquals("fixed-id", r.id());
        assertEquals(LocalDateTime.of(2025,10,1,12,34,56), r.createdAt());
    }

    @Test
    void horizonAndNoPastValidation() {
        Inventory inv = new Inventory(1, 0, 0);
        InventoryRepository invRepo = new InMemoryInventoryRepository(inv);
        ReservationRepository resRepo = new InMemoryReservationRepository();
        Clock fixed = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));
        ReservationService s = new ReservationService(invRepo, resRepo, fixed, () -> "id");
        LocalDate tooFarDate = LocalDate.of(2026,1,2);
        assertThrows(ValidationException.class, () -> s.reserve(new ReservationRequest(CarType.SEDAN, tooFarDate.atStartOfDay(), 1)));
        assertThrows(ValidationException.class, () -> s.reserve(new ReservationRequest(CarType.SEDAN, LocalDateTime.of(2024,12,31,23,59), 1)));
    }

    @Test
    void differentTypesIndependent() {
        LocalDateTime start = LocalDateTime.of(2025,11,1,10,0);
        assertNotNull(service.reserve(new ReservationRequest(CarType.SUV, start, 2)).id());
        assertNotNull(service.reserve(new ReservationRequest(CarType.VAN, start, 2)).id());
    }
}
