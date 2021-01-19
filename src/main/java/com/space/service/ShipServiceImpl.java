package com.space.service;

import com.space.BadRequestException;
import com.space.NotFoundException;
import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {
    @Autowired
    private ShipRepository shipRepository;

    @Override
    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> ships = shipRepository.findAll();
        if (name != null) {
            ships.removeIf(ship -> !ship.getName().contains(name));
        }
        if (planet != null) {
            ships.removeIf(ship -> !ship.getPlanet().contains(planet));
        }
        if (shipType != null) {
            ships.removeIf(ship -> ship.getShipType() != shipType);
        }
        if (after != null) {
            ships.removeIf(ship -> ship.getProdDate().before(new Date(after)));
        }
        if (before != null) {
            ships.removeIf(ship -> ship.getProdDate().after(new Date(before)));
        }
        if (isUsed != null) {
            ships.removeIf(ship -> ship.getUsed() != isUsed);
        }
        if (minSpeed != null) {
            ships.removeIf(ship -> ship.getSpeed() < minSpeed);
        }
        if (maxSpeed != null) {
            ships.removeIf(ship -> ship.getSpeed() > maxSpeed);
        }
        if (minCrewSize != null) {
            ships.removeIf(ship -> ship.getCrewSize() < minCrewSize);
        }
        if (maxCrewSize != null) {
            ships.removeIf(ship -> ship.getCrewSize() > maxCrewSize);
        }
        if (minRating != null) {
            ships.removeIf(ship -> ship.getRating() < minRating);
        }
        if (maxRating != null) {
            ships.removeIf(ship -> ship.getRating() > maxRating);
        }
        return ships;
    }

    public List<Ship> prepareFilteredShips(final List<Ship> filteredShips, ShipOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;
        return filteredShips.stream()
                .sorted(getComparator(order))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    private Comparator<Ship> getComparator(ShipOrder order) {
        if (order == null) {
            return Comparator.comparing(Ship::getId);
        }
        Comparator<Ship> comparator = null;
        if (order.getFieldName().equals("id")) {
            comparator = Comparator.comparing(Ship::getId);
        } else if (order.getFieldName().equals("speed")) {
            comparator = Comparator.comparing(Ship::getSpeed);
        } else if (order.getFieldName().equals("prodDate")) {
            comparator = Comparator.comparing(Ship::getProdDate);
        } else if (order.getFieldName().equals("rating")) {
            comparator = Comparator.comparing(Ship::getRating);
        }
        return comparator;
    }

    @Override
    public void save(Ship ship) {
        if (ship == null) {
            throw new NotFoundException();
        }
        if (ship.getProdDate() == null) {
            throw new BadRequestException();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        if (ship.getName() == null || ship.getProdDate() == null || ship.getSpeed() == null ||
                ship.getPlanet() == null || ship.getCrewSize() == null || ship.getName().length() > 50 ||
                ship.getPlanet().length() > 50 || ship.getName().isEmpty() || ship.getPlanet().isEmpty() ||
                ship.getCrewSize() < 0 || ship.getCrewSize() > 9999 || ship.getSpeed() < 0.01 ||
                ship.getSpeed() > 0.99 || ship.getProdDate().getTime() < 0 ||
                calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019) {
            throw new BadRequestException();
        }
        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }
        ship.setSpeed(ship.getSpeed());
        ship.setRating();
        shipRepository.saveAndFlush(ship);
    }

//    private Boolean isEmptyShip(Ship ship) {
//        return ship.getName() != null || ship.getPlanet() != null || ship.getShipType() != null || ship.getUsed() != null ||
//                ship.getCrewSize() != null || ship.getSpeed() != null || ship.getProdDate() != null;
//    }

    @Override
    public Ship update(Long id, Ship newShip) {
        Ship editedShip = getByID(id);
        if (editedShip == null || newShip == null) {
            throw new NotFoundException();
        }
        if (newShip.getName() != null) {
            if (newShip.getName().isEmpty() || newShip.getName().length() > 50) {
                throw new BadRequestException();
            }
            editedShip.setName(newShip.getName());
        }
        if (newShip.getPlanet() != null) {
            if (newShip.getPlanet().isEmpty() || newShip.getPlanet().length() > 50) {
                throw new BadRequestException();
            }
            editedShip.setPlanet(newShip.getPlanet());
        }
        if (newShip.getShipType() != null) {
            editedShip.setShipType(newShip.getShipType());
        }
        if (newShip.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newShip.getProdDate());
            if (newShip.getProdDate().getTime() < 0 || calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019) {
                throw new BadRequestException();
            }
            editedShip.setProdDate(newShip.getProdDate());
        }
        if (newShip.getUsed() != null) {
            editedShip.setUsed(newShip.getUsed());
        }
        if (newShip.getSpeed() != null) {
            if (newShip.getSpeed() < 0.01 || newShip.getSpeed() > 0.99) {
                throw new BadRequestException();
            }
            editedShip.setSpeed(newShip.getSpeed());
        }
        if (newShip.getCrewSize() != null) {
            if (newShip.getCrewSize() < 0 || newShip.getCrewSize() > 9999) {
                throw new BadRequestException();
            }
            editedShip.setCrewSize(newShip.getCrewSize());
        }
        editedShip.setRating();
        return shipRepository.saveAndFlush(editedShip);
    }

    @Override
    public void delete(Long id) {
        Ship ship = getByID(id);
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
        shipRepository.delete(ship);
    }

    @Override
    public Ship getByID(Long id) {
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
        return shipRepository.findById(id).orElse(null);
    }

}
