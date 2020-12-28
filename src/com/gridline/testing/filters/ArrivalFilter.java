package com.gridline.testing.filters;

import com.gridline.testing.domain.Flight;
import com.gridline.testing.interfaces.FlightFilter;
import com.gridline.testing.utils.FilterOperator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class ArrivalFilter implements FlightFilter {
    private final FilterOperator filterOperator;
    private final LocalDateTime compareWithTime;

    public ArrivalFilter(LocalDateTime compareWithTime, FilterOperator filterOperator) {
        this.compareWithTime = compareWithTime;
        this.filterOperator = filterOperator;
    }

    @Override
    public List<Flight> filtrate(final List<Flight> flightList) {
        switch (filterOperator) {
            case EQ:
                return flightList.stream().filter(flight ->
                        flight.getSegments().stream().anyMatch(segment ->
                                segment.getArrivalDate().isEqual(compareWithTime)))
                        .collect(Collectors.toList());
            case GTE:
                return flightList.stream().filter(flight -> flight.getSegments().stream().anyMatch(segment -> segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC) >= compareWithTime.toEpochSecond(ZoneOffset.UTC))).collect(Collectors.toList());
            case GT:
                return flightList.stream().filter(flight -> flight.getSegments().stream().anyMatch(segment -> segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC) > compareWithTime.toEpochSecond(ZoneOffset.UTC))).collect(Collectors.toList());
            case LT:
                return flightList.stream().filter(flight -> flight.getSegments().stream().anyMatch(segment -> segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC) < compareWithTime.toEpochSecond(ZoneOffset.UTC))).collect(Collectors.toList());
            case LTE:
                return flightList.stream().filter(flight -> flight.getSegments().stream().anyMatch(segment -> segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC) <= compareWithTime.toEpochSecond(ZoneOffset.UTC))).collect(Collectors.toList());
            default:
                throw new IllegalStateException("Unexpected filterOperator: " + filterOperator);
        }
    }
}
