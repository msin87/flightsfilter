package com.gridline.testing.filters;

import com.gridline.testing.domain.Flight;
import com.gridline.testing.interfaces.FlightFilter;

import java.util.List;

public class DepartureFilter implements FlightFilter {

    @Override
    public List<Flight> filtrate(List<Flight> flightList) {
        return null;
    }
}
