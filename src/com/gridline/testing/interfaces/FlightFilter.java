package com.gridline.testing.interfaces;

import com.gridline.testing.domain.Flight;

import java.util.List;

public interface FlightFilter {
    List<Flight> filtrate(List<Flight> flightList);
}
