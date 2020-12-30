package com.gridnine.testing.utils;

import com.gridnine.testing.enums.FilterOperator;
import com.gridnine.testing.filter.FlightsFilter;

import java.util.EnumMap;
import java.util.Map;

public class FlightsFilterBuilder {
    private final Map<FilterOperator, Long> arrivalStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> departureStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> idleStatementsMap = new EnumMap<>(FilterOperator.class);
    private Map<FilterOperator, Long> targetStatementsMap;
    private boolean allowInvalidFlights = true;
    private boolean useParallelStream = false;

    public FlightsFilterBuilder eq(Long epochTime) {
        targetStatementsMap.put(FilterOperator.EQ, epochTime);
        return this;
    }

    public FlightsFilterBuilder gte(Long epochTime) {
        targetStatementsMap.put(FilterOperator.GTE, epochTime);
        return this;
    }

    public FlightsFilterBuilder gt(Long epochTime) {
        targetStatementsMap.put(FilterOperator.GT, epochTime);
        return this;
    }

    public FlightsFilterBuilder lt(Long epochTime) {
        targetStatementsMap.put(FilterOperator.LT, epochTime);
        return this;
    }

    public FlightsFilterBuilder lte(Long epochTime) {
        targetStatementsMap.put(FilterOperator.LTE, epochTime);
        return this;
    }

    public FlightsFilterBuilder idle() {
        targetStatementsMap = idleStatementsMap;
        return this;
    }


    public FlightsFilterBuilder removeInvalidFlights() {
        allowInvalidFlights = false;
        return this;
    }

    public FlightsFilterBuilder arrival() {
        targetStatementsMap = arrivalStatementsMap;
        return this;
    }

    public FlightsFilterBuilder departure() {
        targetStatementsMap = departureStatementsMap;
        return this;
    }
    public FlightsFilterBuilder doParallel() {
        this.useParallelStream = true;
        return this;
    }

    public FlightsFilter build() {
        if (allowInvalidFlights && arrivalStatementsMap.isEmpty() && departureStatementsMap.isEmpty() && idleStatementsMap.isEmpty())
            throw new NullPointerException("Wrong usage of FlightFilterBuilder");
        return new FlightsFilter(arrivalStatementsMap, departureStatementsMap, idleStatementsMap, allowInvalidFlights, useParallelStream);
    }

}
