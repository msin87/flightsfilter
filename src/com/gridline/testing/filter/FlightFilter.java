package com.gridline.testing.filter;

import com.gridline.testing.enums.FilterOperator;
import com.gridline.testing.enums.FlightFilterType;
import com.gridline.testing.interfaces.Filter;
import com.gridline.testing.models.Flight;
import com.gridline.testing.models.Segment;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FlightFilter implements Filter<List<Flight>> {
    private final Map<FilterOperator, Long> arrivalStatementsMap;
    private final Map<FilterOperator, Long> departureStatementsMap;
    private final Map<FilterOperator, Long> idleStatementsMap;
    private final boolean allowInvalidFlights;
    private boolean invalidFlightsRemoved = false;

    public FlightFilter(Map<FilterOperator, Long> arrivalStatementsMap, Map<FilterOperator, Long> departureStatementsMap, Map<FilterOperator, Long> idleStatementsMap, boolean allowInvalidFlights) {
        this.arrivalStatementsMap = arrivalStatementsMap;
        this.departureStatementsMap = departureStatementsMap;
        this.idleStatementsMap = idleStatementsMap;
        this.allowInvalidFlights = allowInvalidFlights;
    }

    private long getSegmentEpochByType(Segment segment, FlightFilterType flightFilterType) {
        switch (flightFilterType) {
            case ARRIVAL:
                return segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC);
            default:
            case DEPARTURE:
                return segment.getDepartureDate().toEpochSecond(ZoneOffset.UTC);
        }
    }

    private boolean filterStatementResult(Segment segment, long epochTimeToCompare, FilterOperator filterOperator, FlightFilterType flightFilterType) {
        switch (filterOperator) {
            case EQ:
                return getSegmentEpochByType(segment, flightFilterType) == epochTimeToCompare;
            case GTE:
                return getSegmentEpochByType(segment, flightFilterType) >= epochTimeToCompare;
            case GT:
                return getSegmentEpochByType(segment, flightFilterType) > epochTimeToCompare;
            case LT:
                return getSegmentEpochByType(segment, flightFilterType) < epochTimeToCompare;
            case LTE:
                return getSegmentEpochByType(segment, flightFilterType) <= epochTimeToCompare;
            default:
                return false;
        }
    }

    private boolean isValidSegment(Segment segment) {
        if (!(allowInvalidFlights || invalidFlightsRemoved)) {
            return segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC) < segment.getDepartureDate().toEpochSecond(ZoneOffset.UTC);
        }
        return true;
    }

    private boolean isValidFlight(Flight flight) {
        boolean isValid = false;
        if (allowInvalidFlights && invalidFlightsRemoved) {
            return true;
        }
        for (Segment segment : flight.getSegments()) {
            isValid |= isValidSegment(segment);
        }
        return isValid;
    }

    private boolean isPassedSegment(Segment segment, Long epochTimeToCompare, FilterOperator filterOperator, FlightFilterType filterType) {
        if (!isValidSegment(segment))
            return false;
        return filterStatementResult(segment, epochTimeToCompare, filterOperator, filterType);
    }

    private boolean isAllowedSegmentDiff(long diffEpochTime, long toleranceSeconds, FilterOperator filterOperator) {
        switch (filterOperator) {
            case EQ:
                return diffEpochTime == toleranceSeconds;
            case GTE:
                return diffEpochTime >= toleranceSeconds;
            case GT:
                return diffEpochTime > toleranceSeconds;
            case LT:
                return diffEpochTime < toleranceSeconds;
            case LTE:
                return diffEpochTime <= toleranceSeconds;
            default:
                return false;
        }
    }

    private boolean isAllowedFlightIdleTime(Flight flight, long toleranceSeconds, FilterOperator filterOperator) {
        boolean isPassed = false;
        List<Segment> segmentList = flight.getSegments();
        for (int nextSegmentIndex = 1; nextSegmentIndex <= (segmentList.size() - 1); nextSegmentIndex++) {
            Segment currentSegment = segmentList.get(nextSegmentIndex - 1);
            Segment nextSegment = segmentList.get(nextSegmentIndex);
            long currentArrivalEpoch = currentSegment.getArrivalDate().toEpochSecond(ZoneOffset.UTC);
            long nextDepartureEpoch = nextSegment.getDepartureDate().toEpochSecond(ZoneOffset.UTC);
            isPassed |= (isAllowedSegmentDiff(nextDepartureEpoch - currentArrivalEpoch, toleranceSeconds, filterOperator));
        }
        return isPassed;
    }

    private List<Flight> conditionalFilter(List<Flight> flightList, Map<FilterOperator, Long> conditionMap, FlightFilterType flightFilterType) {
        List<Flight> filteredList = new ArrayList<>(flightList);
        for (Map.Entry<FilterOperator, Long> conditions : conditionMap.entrySet()) {
            Stream<Flight> flightsStream = filteredList.parallelStream();
            filteredList = flightsStream.filter(flight ->
                    flight.getSegments().stream().
                            anyMatch(segment ->
                                    isPassedSegment(segment, conditions.getValue(), conditions.getKey(), flightFilterType)))
                    .collect(Collectors.toList());
        }
        invalidFlightsRemoved = true;
        return filteredList;
    }

    /**
     * todo: parallelStream
     * cause: не хватило времени на продумывание параллельного алгоритма.
     * Возможно решение лежит в области Thread'ов
     */
    private List<Flight> idleFlightsFilter(List<Flight> flightList, Map<FilterOperator, Long> conditionMap) {
        List<Flight> filteredList = new LinkedList<>(flightList);
        for (Map.Entry<FilterOperator, Long> conditions : conditionMap.entrySet()) {
            long toleranceSeconds = conditions.getValue();
            filteredList = filteredList.stream()
                    .filter(flight -> isAllowedFlightIdleTime(flight, toleranceSeconds, conditions.getKey()))
                    .filter(this::isValidFlight)
                    .collect(Collectors.toList());
        }
        invalidFlightsRemoved = true;
        return filteredList;
    }

    private List<Flight> invalidFlightsFilter(List<Flight> flightList) {
        List<Flight> filteredList = flightList.parallelStream().filter(flight -> flight.getSegments().stream().noneMatch(this::isValidSegment)).collect(Collectors.toList());
        invalidFlightsRemoved = true;
        return filteredList;
    }

    @Override
    public List<Flight> filtrate(final List<Flight> flightList) {
        List<Flight> filteredFlights = new ArrayList<>(flightList);
        if (!arrivalStatementsMap.isEmpty())
            filteredFlights = conditionalFilter(filteredFlights, arrivalStatementsMap, FlightFilterType.ARRIVAL);
        if (!departureStatementsMap.isEmpty())
            filteredFlights = conditionalFilter(filteredFlights, departureStatementsMap, FlightFilterType.DEPARTURE);
        if (!idleStatementsMap.isEmpty())
            filteredFlights = idleFlightsFilter(filteredFlights, idleStatementsMap);
        if ((arrivalStatementsMap.isEmpty() && departureStatementsMap.isEmpty() && idleStatementsMap.isEmpty()) || !allowInvalidFlights)
            filteredFlights = invalidFlightsFilter(filteredFlights);
        return filteredFlights;
    }
}
