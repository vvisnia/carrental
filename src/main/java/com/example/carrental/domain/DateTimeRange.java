package com.example.carrental.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public final class DateTimeRange {
    private final LocalDateTime start;
    private final LocalDateTime end;

    private DateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static DateTimeRange of(LocalDateTime startInclusive, int days) {
        Objects.requireNonNull(startInclusive, "startInclusive must not be null");
        if (days < 1) throw new IllegalArgumentException("days must be >= 1");
        return new DateTimeRange(startInclusive, startInclusive.plusDays(days));
    }

    public LocalDateTime start() { return start; }
    public LocalDateTime end() { return end; }

    @Override public String toString() { return "[" + start + ", " + end + ")"; }
}
