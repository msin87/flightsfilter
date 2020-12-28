package com.gridline.testing.utils;

import com.gridline.testing.filters.SegmentFilter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SegmentFilterBuilder {
    private final SegmentFilterType segmentFilterType;
    private final Map<FilterOperator, LocalDateTime> conditionMap;
    private final SegmentFilterBehavior behavior;

    public SegmentFilterBuilder(SegmentFilterType segmentFilterType, SegmentFilterBehavior behavior) {
        this.segmentFilterType = segmentFilterType;
        this.behavior = behavior;
        this.conditionMap = new HashMap<>();
    }

    public SegmentFilter eq(LocalDateTime localDateTime) {
        conditionMap.put(FilterOperator.EQ, LocalDateTime.from(localDateTime));
        return new SegmentFilter(conditionMap, segmentFilterType, behavior);
    }

    public SegmentFilterBuilder gte(LocalDateTime localDateTime) {
        conditionMap.put(FilterOperator.GTE, LocalDateTime.from(localDateTime));
        return this;
    }

    public SegmentFilterBuilder gt(LocalDateTime localDateTime) {
        conditionMap.put(FilterOperator.GT, LocalDateTime.from(localDateTime));
        return this;
    }

    public SegmentFilterBuilder lt(LocalDateTime localDateTime) {
        conditionMap.put(FilterOperator.LT, LocalDateTime.from(localDateTime));
        return this;
    }

    public SegmentFilterBuilder lte(LocalDateTime localDateTime) {
        conditionMap.put(FilterOperator.LTE, LocalDateTime.from(localDateTime));
        return this;
    }

    public SegmentFilter build() {
        return new SegmentFilter(conditionMap, segmentFilterType, behavior);
    }

}
