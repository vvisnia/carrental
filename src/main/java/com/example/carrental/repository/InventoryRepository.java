package com.example.carrental.repository;

import com.example.carrental.domain.CarType;

public interface InventoryRepository {
    int totalFor(CarType type);
}
