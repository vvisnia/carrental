package com.example.carrental.repository;

import com.example.carrental.domain.CarType;
import com.example.carrental.domain.Inventory;

public class InMemoryInventoryRepository implements InventoryRepository {
    private final Inventory inventory;

    public InMemoryInventoryRepository(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public int totalFor(CarType type) {
        return inventory.totalFor(type);
    }
}
