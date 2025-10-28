# Car Reservation POC (Date and Time)
Goal: Reserve a car of a given type (SEDAN, SUV, VAN) for a chosen start date and time and a number of days, with limited fleet size and overlap-aware availability checks.
Design overview
- Domain – CarType, DateTimeRange, Reservation, ReservationRequest, Inventory.
- Repositories – In-memory storage, replaceable by DB-backed repos.
- Services – AvailabilityService for checks using a sweep-line overlap algorithm, ReservationService for orchestration and validation.
- Exceptions – Clear English messages.
- Utilities – Preconditions, IdGenerator.
Assumptions
- Inventory is defined per CarType.
- Start is LocalDateTime, end is start.plusDays(days), range is [start, end).
- IDs are strings; default UUID generator.
- Max booking horizon: 365 days from Clock; past start times are rejected.
How to run
mvn -q -DskipTests=false test
