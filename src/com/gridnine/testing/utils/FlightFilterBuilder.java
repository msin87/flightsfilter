package com.gridnine.testing.utils;

import com.gridnine.testing.enums.FilterOperator;
import com.gridnine.testing.filter.FlightFilter;

import java.util.EnumMap;
import java.util.Map;

public class FlightFilterBuilder {
    private final Map<FilterOperator, Long> arrivalStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> departureStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> idleStatementsMap = new EnumMap<>(FilterOperator.class);
    private Map<FilterOperator, Long> targetStatementsMap;
    private boolean allowInvalidFlights = true;

    public FlightFilter eq(Long epochTime) {
        targetStatementsMap.put(FilterOperator.EQ, epochTime);
        return new FlightFilter(arrivalStatementsMap, departureStatementsMap, idleStatementsMap, allowInvalidFlights);
    }

    public FlightFilterBuilder gte(Long epochTime) {
        targetStatementsMap.put(FilterOperator.GTE, epochTime);
        return this;
    }

    public FlightFilterBuilder gt(Long epochTime) {
        targetStatementsMap.put(FilterOperator.GT, epochTime);
        return this;
    }

    public FlightFilterBuilder lt(Long epochTime) {
        targetStatementsMap.put(FilterOperator.LT, epochTime);
        return this;
    }

    public FlightFilterBuilder lte(Long epochTime) {
        targetStatementsMap.put(FilterOperator.LTE, epochTime);
        return this;
    }

    public FlightFilterBuilder idle() {
        targetStatementsMap = idleStatementsMap;
        return this;
    }


    public FlightFilterBuilder removeInvalidFlights() {
        allowInvalidFlights = false;
        return this;
    }

    public FlightFilterBuilder arrival() {
        targetStatementsMap = arrivalStatementsMap;
        return this;
    }

    public FlightFilterBuilder departure() {
        targetStatementsMap = departureStatementsMap;
        return this;
    }

    public FlightFilter build() {
        if (allowInvalidFlights && arrivalStatementsMap.isEmpty() && departureStatementsMap.isEmpty() && idleStatementsMap.isEmpty())
            throw new NullPointerException("Wrong usage of FlightFilterBuilder");
        return new FlightFilter(arrivalStatementsMap, departureStatementsMap, idleStatementsMap, allowInvalidFlights);
    }

}
