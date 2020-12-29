package com.gridline.testing.filters;

import com.gridline.testing.domain.Flight;
import com.gridline.testing.domain.Segment;
import com.gridline.testing.interfaces.FlightFilter;
import com.gridline.testing.utils.FilterOperator;
import com.gridline.testing.utils.SegmentFilterBehavior;
import com.gridline.testing.utils.SegmentFilterType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SegmentFilter implements FlightFilter {
    private final SegmentFilterType segmentFilterType;
    private final Map<FilterOperator, Long> conditionMap;
    private final SegmentFilterBehavior behavior;

    public SegmentFilter(Map<FilterOperator, Long> conditionMap, SegmentFilterType segmentFilterType, SegmentFilterBehavior behavior) {
        this.conditionMap = conditionMap;
        this.segmentFilterType = segmentFilterType;
        this.behavior = behavior;
    }

    private long getSegmentEpochByType(Segment segment, SegmentFilterType segmentFilterType) {
        switch (segmentFilterType) {
            case ARRIVE:
                return segment.getArrivalDate().toEpochSecond(ZoneOffset.UTC);
            default:
            case DEPARTURE:
                return segment.getDepartureDate().toEpochSecond(ZoneOffset.UTC);
        }
    }

    private boolean testSegment(Segment segment, long compareTime, FilterOperator filterOperator) {
        switch (filterOperator) {
            case EQ:
                return getSegmentEpochByType(segment, segmentFilterType) == compareTime;
            case GTE:
                return getSegmentEpochByType(segment, segmentFilterType) >= compareTime;
            case GT:
                return getSegmentEpochByType(segment, segmentFilterType) > compareTime;
            case LT:
                return getSegmentEpochByType(segment, segmentFilterType) < compareTime;
            case LTE:
                return getSegmentEpochByType(segment, segmentFilterType) <= compareTime;
            default:
                return false;
        }
    }

    private List<Segment> filterSegment(List<Segment> segmentList, long compareTime, FilterOperator filterOperator) {
        List<Segment> resultList = new ArrayList<>();
        for (Segment segment : segmentList) {
            if (testSegment(segment, compareTime, filterOperator)) {
                switch (behavior) {
                    case GET_ONLY_FILTERED_SEGMENTS:
                        resultList.add(segment);
                        break;
                    case GET_ALL_SEGMENTS:
                        resultList.addAll(segmentList);
                        return resultList;
                }
            }
        }
        return resultList;
    }

    private boolean isPassedSegment(Segment segment, Long compareWithEpochTime, FilterOperator filterOperator, SegmentFilterType filterType) {
        switch (filterOperator) {
            case EQ:
                return getSegmentEpochByType(segment, filterType) == compareWithEpochTime;
            case GTE:
                return getSegmentEpochByType(segment, filterType) >= compareWithEpochTime;
            case GT:
                return getSegmentEpochByType(segment, filterType) > compareWithEpochTime;
            case LT:
                return getSegmentEpochByType(segment, filterType) < compareWithEpochTime;
            case LTE:
                return getSegmentEpochByType(segment, filterType) <= compareWithEpochTime;
            default:
                return false;
        }
    }
    private boolean isPassedSegmentDiff(long diffEpochTime, long toleranceSeconds, FilterOperator filterOperator){
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
    private  List<Flight> conditionalFilter(final List<Flight> flightList){
        List<Flight> filteredList = new ArrayList<>(flightList);
        for (Map.Entry<FilterOperator, Long> conditions : conditionMap.entrySet()) {
            Stream<Flight> flightsStream = filteredList.parallelStream();
            switch (behavior) {
                case GET_ONLY_FILTERED_SEGMENTS:
                    filteredList = flightsStream.filter(flight ->
                            flight.getSegments().stream().
                                    anyMatch(segment ->
                                            isPassedSegment(segment, conditions.getValue(), conditions.getKey(), segmentFilterType)))
                            .map(flight -> new Flight(flight.getSegments().stream()
                                    .filter(segment -> isPassedSegment(segment, conditions.getValue(), conditions.getKey(), segmentFilterType)).collect(Collectors.toList())))
                            .collect(Collectors.toList());
                    break;
                case GET_ALL_SEGMENTS:
                    filteredList = flightsStream.filter(flight ->
                            flight.getSegments().stream().
                                    anyMatch(segment ->
                                            isPassedSegment(segment, conditions.getValue(), conditions.getKey(), segmentFilterType)))
                            .collect(Collectors.toList());
                    break;
                default:
                    return Collections.emptyList();
            }
        }
        return filteredList;
    }
    private List<Flight> onGroundFilter(final List<Flight> flightList){
        List<Flight> filteredList = new LinkedList<>(flightList);
        conditionMap.remove(FilterOperator.ON_GROUND);
        for (Map.Entry<FilterOperator, Long> conditions : conditionMap.entrySet()){
            long toleranceSeconds = conditions.getValue();
            for (int i = 0; i < flightList.size(); i++) {
                boolean testPassed = false;
                Flight flight = flightList.get(i);
                List<Segment> segmentList = new ArrayList<>(flight.getSegments());
                if (segmentList.size()<2){
                    //noinspection SuspiciousListRemoveInLoop
                    filteredList.remove(flightList.get(i));
                    continue;
                }
                for (int nextSegmentIndex = 1; nextSegmentIndex <= (segmentList.size()-1); nextSegmentIndex++) {
                    Segment currentSegment = segmentList.get(nextSegmentIndex-1);
                    Segment nextSegment = segmentList.get(nextSegmentIndex);
                    long currentArrivalEpoch = currentSegment.getArrivalDate().toEpochSecond(ZoneOffset.UTC);
                    long nextDepartureEpoch = nextSegment.getDepartureDate().toEpochSecond(ZoneOffset.UTC);
                    testPassed|=isPassedSegmentDiff(nextDepartureEpoch-currentArrivalEpoch,toleranceSeconds,conditions.getKey());
                }
                if (!testPassed)
                    //noinspection SuspiciousListRemoveInLoop
                    filteredList.remove(flightList.get(i));
            }
        }
        return filteredList;
    }
    @Override
    public List<Flight> filtrate(final List<Flight> flightList) {
        if (!conditionMap.containsKey(FilterOperator.ON_GROUND))
            return conditionalFilter(flightList);
        return onGroundFilter(flightList);
    }
}
