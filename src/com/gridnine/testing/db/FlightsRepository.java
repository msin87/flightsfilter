package com.gridnine.testing.db;

import com.gridnine.testing.interfaces.DataBaseInterface;
import com.gridnine.testing.models.Flight;
import com.gridnine.testing.utils.FlightBuilder;

import java.util.List;

public class FlightsRepository implements DataBaseInterface<Flight> {
    private final List<Flight> flightList = FlightBuilder.createFlights();

    @Override
    public List<Flight> getAll() {
        return flightList;
    }
}
