package com.gridline.testing.utils;

import com.gridline.testing.filters.SegmentFilter;

import java.util.EnumMap;
import java.util.Map;

public class SegmentFilterBuilder {
    private SegmentFilterType segmentFilterType;
    private final Map<FilterOperator, Long> conditionMap = new EnumMap<>(FilterOperator.class);
    private SegmentFilterBehavior behavior;

    public static long hoursToSeconds(int hours) {
        return hours * 60 * 60L;
    }

    public SegmentFilterBuilder(SegmentFilterType segmentFilterType, SegmentFilterBehavior behavior) {
        this.segmentFilterType = segmentFilterType;
        this.behavior = behavior;
    }

    public SegmentFilterBuilder() {
    }

    public SegmentFilter eq(Long epochTime) {
        if (behavior == null || segmentFilterType == null)
            return null;
        conditionMap.put(FilterOperator.EQ, epochTime);
        return new SegmentFilter(conditionMap, segmentFilterType, behavior);
    }

    public SegmentFilterBuilder gte(Long epochTime) {
        if (behavior == null || segmentFilterType == null)
            return null;
        conditionMap.put(FilterOperator.GTE, epochTime);
        return this;
    }

    public SegmentFilterBuilder gt(Long epochTime) {
        if (behavior == null || segmentFilterType == null)
            return null;
        conditionMap.put(FilterOperator.GT, epochTime);
        return this;
    }

    public SegmentFilterBuilder lt(Long epochTime) {
        if (behavior == null || segmentFilterType == null)
            return null;
        conditionMap.put(FilterOperator.LT, epochTime);
        return this;
    }

    public SegmentFilterBuilder lte(Long epochTime) {
        if (behavior == null || segmentFilterType == null)
            return null;
        conditionMap.put(FilterOperator.LTE, epochTime);
        return this;
    }

    public SegmentFilterBuilder onGround() {
        segmentFilterType = SegmentFilterType.ARRIVE;
        behavior = SegmentFilterBehavior.GET_ALL_SEGMENTS;
        conditionMap.put(FilterOperator.ON_GROUND, null);
        return this;
    }

    public SegmentFilter build() {
        return new SegmentFilter(conditionMap, segmentFilterType, behavior);
    }

}
