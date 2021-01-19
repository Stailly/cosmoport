package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {
    List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                            Boolean isUsed, Double minSpeed, Double maxSpeed,
                            Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating);

    void save(Ship ship);

    Ship update(Long id, Ship ship);

    void delete(Long id);

    Ship getByID(Long id);

}
