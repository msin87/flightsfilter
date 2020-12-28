package com.gridline.testing.filters;

import com.gridline.testing.domain.Flight;
import com.gridline.testing.domain.Segment;
import com.gridline.testing.interfaces.FlightFilter;
import com.gridline.testing.utils.FilterOperator;
import com.gridline.testing.utils.SegmentFilterBehavior;
import com.gridline.testing.utils.SegmentFilterType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SegmentFilter implements FlightFilter {
    private final SegmentFilterType segmentFilterType;
    private final Map<FilterOperator, LocalDateTime> conditionMap;
    private final SegmentFilterBehavior behavior;

    public SegmentFilter(Map<FilterOperator, LocalDateTime> conditionMap, SegmentFilterType segmentFilterType, SegmentFilterBehavior behavior) {
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
    private boolean testSegment(Segment segment, long compareTime, FilterOperator filterOperator){
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
            if (testSegment(segment,compareTime,filterOperator)){
                switch (behavior){
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

    @Override
    public List<Flight> filtrate(final List<Flight> flightList) {
        List<Flight> filteredList = new ArrayList<>();

        for (Map.Entry<FilterOperator, LocalDateTime> conditions : conditionMap.entrySet()) {
            for (Flight flight : flightList) {
                List<Segment> segments = filterSegment(flight.getSegments(),conditions.getValue().toEpochSecond(ZoneOffset.UTC),conditions.getKey());
                if (!segments.isEmpty())
                    filteredList.add(new Flight(segments));
            }
//            switch (conditions.getKey()) {
//                case EQ:
//                    filteredList = filteredList.stream().filter(flight ->
//                            flight.getSegments().stream()
//                                    .anyMatch(segment ->
//                                            getSegmentEpochByType(segment, segmentFilterType) == conditions.getValue().toEpochSecond(ZoneOffset.UTC)))
//                            .collect(Collectors.toList());
//                    break;
//                case GTE:
//                    filteredList = flightList.stream().filter(flight ->
//                            flight.getSegments().stream()
//                                    .anyMatch(segment ->
//                                            getSegmentEpochByType(segment, segmentFilterType) >= conditions.getValue()
//                                                    .toEpochSecond(ZoneOffset.UTC)))
//                            .collect(Collectors.toList());
//                    break;
//                case GT:
//                    filteredList = flightList.stream().filter(flight ->
//                            flight.getSegments().stream()
//                                    .anyMatch(segment ->
//                                            getSegmentEpochByType(segment, segmentFilterType) > conditions.getValue()
//                                                    .toEpochSecond(ZoneOffset.UTC)))
//                            .collect(Collectors.toList());
//                    break;
//                case LT:
//                    filteredList = flightList.stream().filter(flight ->
//                            flight.getSegments().stream()
//                                    .anyMatch(segment ->
//                                            getSegmentEpochByType(segment, segmentFilterType) < conditions.getValue()
//                                                    .toEpochSecond(ZoneOffset.UTC)))
//                            .collect(Collectors.toList());
//                    break;
//                case LTE:
//                    filteredList = flightList.stream().filter(flight ->
//                            flight.getSegments().stream()
//                                    .anyMatch(segment ->
//                                            getSegmentEpochByType(segment, segmentFilterType) <= conditions.getValue()
//                                                    .toEpochSecond(ZoneOffset.UTC)))
//                            .collect(Collectors.toList());
//                    break;
//                default:
//                    throw new IllegalStateException("Unexpected filterOperator: " + conditions.getKey());
//            }
        }
        return filteredList;
    }
}
