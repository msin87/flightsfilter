package com.gridline.testing.filter;

import com.gridline.testing.models.Flight;
import com.gridline.testing.models.Segment;
import com.gridline.testing.interfaces.Filter;
import com.gridline.testing.utils.FilterOperator;
import com.gridline.testing.utils.FlightFilterBehavior;
import com.gridline.testing.utils.FlightFilterType;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlightFilter implements Filter<List<Flight>> {
    private final Map<FilterOperator, Long> arrivalStatementsMap;
    private final Map<FilterOperator, Long> departureStatementsMap;
    private final Map<FilterOperator, Long> idleStatementsMap;
    private final FlightFilterBehavior behavior;
    private boolean allowInvalidFlights;

    public FlightFilter(Map<FilterOperator, Long> arrivalStatementsMap, Map<FilterOperator, Long> departureStatementsMap, Map<FilterOperator, Long> idleStatementsMap, FlightFilterBehavior behavior, boolean allowInvalidFlights) {
        this.arrivalStatementsMap = arrivalStatementsMap;
        this.departureStatementsMap = departureStatementsMap;
        this.idleStatementsMap = idleStatementsMap;
        this.allowInvalidFlights = allowInvalidFlights;
        this.behavior = behavior;
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
    private boolean isInvalidSegment(Segment segment){
        if (!allowInvalidFlights) {
            return segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC) < segment.getDepartureDate().toEpochSecond(ZoneOffset.UTC);
        }
        return false;
    }
    private boolean isPassedSegment(Segment segment, Long epochTimeToCompare, FilterOperator filterOperator, FlightFilterType filterType) {
        if (isInvalidSegment(segment))
            return false;
        return filterStatementResult(segment, epochTimeToCompare, filterOperator, filterType);
    }

    private boolean isPassedSegmentDiff(long diffEpochTime, long toleranceSeconds, FilterOperator filterOperator) {
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

    private List<Flight> conditionalFilter(final List<Flight> flightList, Map<FilterOperator, Long> conditionMap, FlightFilterType flightFilterType) {
        List<Flight> filteredList = new ArrayList<>(flightList);
        for (Map.Entry<FilterOperator, Long> conditions : conditionMap.entrySet()) {
            Stream<Flight> flightsStream = filteredList.parallelStream();
            switch (behavior) {
                case GET_ONLY_FILTERED_SEGMENTS:
                    filteredList = flightsStream.filter(flight ->
                            flight.getSegments().stream().
                                    anyMatch(segment ->
                                            isPassedSegment(segment, conditions.getValue(), conditions.getKey(), flightFilterType)))
                            .map(flight -> new Flight(flight.getSegments().stream()
                                    .filter(segment -> isPassedSegment(segment, conditions.getValue(), conditions.getKey(), flightFilterType)).collect(Collectors.toList())))
                            .collect(Collectors.toList());
                    break;
                case GET_ALL_SEGMENTS:
                    filteredList = flightsStream.filter(flight ->
                            flight.getSegments().stream().
                                    anyMatch(segment ->
                                            isPassedSegment(segment, conditions.getValue(), conditions.getKey(), flightFilterType)))
                            .collect(Collectors.toList());
                    break;
                default:
                    return Collections.emptyList();
            }
        }
        return filteredList;
    }

    private List<Flight> iddleFlightsFilter(final List<Flight> flightList, Map<FilterOperator, Long> conditionMap) {
        List<Flight> filteredList = new LinkedList<>(flightList);
        for (Map.Entry<FilterOperator, Long> conditions : conditionMap.entrySet()) {
            long toleranceSeconds = conditions.getValue();
            flightLoop:
            for (int i = 0; i < flightList.size(); i++) {
                boolean testPassed = false;
                Flight flight = flightList.get(i);
                List<Segment> segmentList = new ArrayList<>(flight.getSegments());
                if (segmentList.size() < 2) {
                    //noinspection SuspiciousListRemoveInLoop
                    filteredList.remove(flightList.get(i));
                    continue;
                }
                if (!allowInvalidFlights){
                    for (Segment segment : flight.getSegments()) {
                        if (isInvalidSegment(segment))
                            filteredList.remove(flightList.get(i));
                            continue flightLoop;
                    }
                }
                for (int nextSegmentIndex = 1; nextSegmentIndex <= (segmentList.size() - 1); nextSegmentIndex++) {
                    Segment currentSegment = segmentList.get(nextSegmentIndex - 1);
                    Segment nextSegment = segmentList.get(nextSegmentIndex);
                    long currentArrivalEpoch = currentSegment.getArrivalDate().toEpochSecond(ZoneOffset.UTC);
                    long nextDepartureEpoch = nextSegment.getDepartureDate().toEpochSecond(ZoneOffset.UTC);
                    testPassed |= isPassedSegmentDiff(nextDepartureEpoch - currentArrivalEpoch, toleranceSeconds, conditions.getKey());
                }
                if (!testPassed)
                    //noinspection SuspiciousListRemoveInLoop
                    filteredList.remove(flightList.get(i));
            }
        }
        return filteredList;
    }
    private List<Flight> invalidFlightsFilter(List<Flight> flightList){
        return flightList.stream().filter(flight -> flight.getSegments().stream().noneMatch(this::isInvalidSegment)).collect(Collectors.toList());
    }
    @Override
    public List<Flight> filtrate(final List<Flight> flightList) {
        List<Flight> filteredFlights = new ArrayList<>(flightList);
        if (!arrivalStatementsMap.isEmpty())
            filteredFlights = conditionalFilter(filteredFlights, arrivalStatementsMap, FlightFilterType.ARRIVAL);
        if (!departureStatementsMap.isEmpty())
            filteredFlights = conditionalFilter(filteredFlights, departureStatementsMap, FlightFilterType.DEPARTURE);
        if (!idleStatementsMap.isEmpty())
            filteredFlights = iddleFlightsFilter(filteredFlights, idleStatementsMap);
        if ((arrivalStatementsMap.isEmpty()&&departureStatementsMap.isEmpty()&&idleStatementsMap.isEmpty()) || !allowInvalidFlights)
            filteredFlights = invalidFlightsFilter(filteredFlights);
        return filteredFlights;
    }
}
