package com.gridnine.testing.utils;

import com.gridnine.testing.enums.FilterOperator;
import com.gridnine.testing.filter.FlightsFilter;

import java.util.EnumMap;
import java.util.Map;

public class FlightsFilterBuilder {
    private final static String ERROR_MESSAGE = "FlightsFilterBuilder: One of the following operators was not used before calling the method: arrival(), departure(), idle()";
    private final Map<FilterOperator, Long> arrivalStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> departureStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> idleStatementsMap = new EnumMap<>(FilterOperator.class);
    private Map<FilterOperator, Long> targetStatementsMap;
    private boolean allowInvalidFlights = true;
    private boolean useParallelStream = false;

    private void stateCheck(){
        if (targetStatementsMap==null)
            throw new IllegalStateException(ERROR_MESSAGE, new NullPointerException("targetStatementsMap is null"));
    }
    public FlightsFilterBuilder eq(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.EQ, epochTime);
        return this;
    }

    public FlightsFilterBuilder gte(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.GTE, epochTime);
        return this;
    }

    public FlightsFilterBuilder gt(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.GT, epochTime);
        return this;
    }

    public FlightsFilterBuilder lt(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.LT, epochTime);
        return this;
    }

    public FlightsFilterBuilder lte(long epochTime) {
        stateCheck();
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
            throw new IllegalStateException(ERROR_MESSAGE, new NullPointerException("All statement maps is null"));
        return new FlightsFilter(arrivalStatementsMap, departureStatementsMap, idleStatementsMap, allowInvalidFlights, useParallelStream);
    }

}
