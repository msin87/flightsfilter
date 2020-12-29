package com.gridline.testing.db;

import com.gridline.testing.models.Flight;
import com.gridline.testing.utils.FlightBuilder;

import java.util.List;

public class FlightsRepository implements DataBaseInterface<Flight> {
    private final List<Flight> flightList = FlightBuilder.createFlights();
    @Override
    public List<Flight> getAll() {
        return flightList;
    }
}
