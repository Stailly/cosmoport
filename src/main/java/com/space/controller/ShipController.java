package com.space.controller;

import com.space.BadRequestException;
import com.space.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    @Autowired
    private ShipServiceImpl service;

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    Ship getShip(@PathVariable Long id) {
        if (!isValid(id)) {
            throw new BadRequestException();
        }
        Ship ship;
        if ((ship = this.service.getByID(id)) == null) {
            throw new NotFoundException();
        }

        return ship;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    Ship saveShip(@RequestBody Ship ship) {
        service.save(ship);
        return ship;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    Ship updateShip(@PathVariable Long id, @RequestBody Ship ship) {
        if (!isValid(id)) {
            throw new BadRequestException();
        }
        return service.update(id, ship);
    }

    private Boolean isValid(Long id) {
        return id != null && id == Math.floor(id) && id > 0;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void deleteShip(@PathVariable Long id) {
        if (id == 0) {
            throw new BadRequestException();
        }
        if (!isValid(id)) {
            throw new NotFoundException();
        }
        service.delete(id);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Ship> getAllShips(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating,
                                  @RequestParam(required = false) ShipOrder order,
                                  @RequestParam(required = false) Integer pageNumber,
                                  @RequestParam(required = false) Integer pageSize) {

        List<Ship> list = service.getShipsList(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating,
                maxRating);
        return service.prepareFilteredShips(list, order, pageNumber, pageSize);
    }

    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Integer getShipsCount(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String planet,
                                 @RequestParam(required = false) ShipType shipType,
                                 @RequestParam(required = false) Long after,
                                 @RequestParam(required = false) Long before,
                                 @RequestParam(required = false) Boolean isUsed,
                                 @RequestParam(required = false) Double minSpeed,
                                 @RequestParam(required = false) Double maxSpeed,
                                 @RequestParam(required = false) Integer minCrewSize,
                                 @RequestParam(required = false) Integer maxCrewSize,
                                 @RequestParam(required = false) Double minRating,
                                 @RequestParam(required = false) Double maxRating) {
        return service.getShipsList(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }
}
