package com.example.carrental.service;

import com.example.carrental.domain.CarType;
import com.example.carrental.domain.DateTimeRange;
import com.example.carrental.domain.Reservation;
import com.example.carrental.repository.InventoryRepository;
import com.example.carrental.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AvailabilityService {
    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;

    public AvailabilityService(InventoryRepository inventoryRepository, ReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    public boolean isAvailable(CarType type, DateTimeRange candidate) {
        int total = inventoryRepository.totalFor(type);
        if (total <= 0) return false;
        List<Reservation> existing = reservationRepository.findByType(type);
        List<Point> points = new ArrayList<>();
        for (Reservation r : existing) {
            LocalDateTime s = r.dateTimeRange().start();
            LocalDateTime e = r.dateTimeRange().end();
            if (overlaps(candidate.start(), candidate.end(), s, e)) {
                points.add(new Point(max(candidate.start(), s), +1));
                points.add(new Point(min(candidate.end(), e), -1));
            }
        }
        points.add(new Point(candidate.start(), +1));
        points.add(new Point(candidate.end(), -1));
        points.sort(Comparator.<Point, LocalDateTime>comparing(p -> p.time).thenComparingInt(p -> p.delta));
        int cur = 0;
        int max = 0;
        for (Point p : points) {
            cur += p.delta;
            if (cur > max) max = cur;
            if (max > total) return false;
        }
        return true;
    }

    private boolean overlaps(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }
    private LocalDateTime max(LocalDateTime a, LocalDateTime b) { return a.isAfter(b) ? a : b; }
    private LocalDateTime min(LocalDateTime a, LocalDateTime b) { return a.isBefore(b) ? a : b; }

    private static class Point {
        final LocalDateTime time;
        final int delta;
        Point(LocalDateTime time, int delta) { this.time = time; this.delta = delta; }
    }
}
