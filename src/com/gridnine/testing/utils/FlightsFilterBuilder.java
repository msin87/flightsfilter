package com.gridnine.testing.utils;

import com.gridnine.testing.enums.FilterOperator;
import com.gridnine.testing.filter.FlightsFilter;

import java.util.EnumMap;
import java.util.Map;

/**
 * <code>{@link FlightsFilterBuilder}</code> is used to build instances of <code>{@link FlightsFilter}</code> from
 * target selection operators <code>arrival(), departure()>, idle()</code>,
 * comparison operators <code>eq(long), gt(long), gte(long), lt(long), lte(long)</code>,
 * optional operator <code>removeInvalidFlights()</code>.
 * <p>
 * Comparison operators can only follow target selection operators.
 * <p>
 * If none of the target selection operators are specified, an {@link IllegalStateException} will be thrown.
 * The optional <code>removeInvalidFlights()</code> operator can be used without target selection operators, but in this case
 * comparison operators cannot be used, otherwise an {@link IllegalStateException} will be thrown.
 * <p>
 * <u>Target selection operators description:</u>
 * All comparison conditions are specified by comparison operators. Operators can be combined.
 * <code>arrival()</code>: Compare the arrival time with the condition.
 * <code>departure()</code>: Compare the departure time with the condition.
 * <code>idle()</code>: Compare the idle time on the ground (the time between arrival and following departure) with the condition.
 * <p>
 * <u>Comparison operators:</u>
 * All operators can be combined. Operators take as input the time for comparison in milliseconds.
 * For targets <code>arrival()</code> and <code>departure()</code>, the time is relative to 1970-01-01 00: 00: 00.0 (epoch time).
 * Target <code>idle()</code> uses absolute idle time on the ground.
 * eq: "=", gt: ">", gte: ">=", lt: "<", lte: "<="
 *
 * @author Mikhail Sinelnikov
 */
public class FlightsFilterBuilder {
    private final static String ERROR_MESSAGE = "FlightsFilterBuilder: One of the following operators was not used before calling the method: arrival(), departure(), idle()";
    private final Map<FilterOperator, Long> arrivalStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> departureStatementsMap = new EnumMap<>(FilterOperator.class);
    private final Map<FilterOperator, Long> idleStatementsMap = new EnumMap<>(FilterOperator.class);
    private Map<FilterOperator, Long> targetStatementsMap;
    private boolean allowInvalidFlights = true;

    private void stateCheck() {
        if (targetStatementsMap == null)
            throw new IllegalStateException(ERROR_MESSAGE, new NullPointerException("targetStatementsMap is null"));
    }

    /**
     * Comparison operator, equivalent to mathematical "=".
     * Equivalent expression: selectedTargetTime == epochTime.
     *
     * @param epochTime time to compare with the time of the selected target
     * @return this
     */
    public FlightsFilterBuilder eq(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.EQ, epochTime);
        return this;
    }

    /**
     * Comparison operator, equivalent to mathematical ">=".
     * Equivalent expression: selectedTargetTime >= epochTime
     *
     * @param epochTime time to compare with the time of the selected target
     * @return this
     */
    public FlightsFilterBuilder gte(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.GTE, epochTime);
        return this;
    }

    /**
     * Comparison operator, equivalent to mathematical ">".
     * Equivalent expression: selectedTargetTime > epochTime
     *
     * @param epochTime time to compare with the time of the selected target
     * @return this
     */
    public FlightsFilterBuilder gt(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.GT, epochTime);
        return this;
    }

    /**
     * Comparison operator, equivalent to mathematical "<".
     * Equivalent expression: selectedTargetTime < epochTime
     *
     * @param epochTime time to compare with the time of the selected target
     * @return this
     */
    public FlightsFilterBuilder lt(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.LT, epochTime);
        return this;
    }

    /**
     * Comparison operator, equivalent to mathematical "<=".
     * Equivalent expression: selectedTargetTime <= epochTime
     *
     * @param epochTime time to compare with the time of the selected target
     * @return this
     */
    public FlightsFilterBuilder lte(long epochTime) {
        stateCheck();
        targetStatementsMap.put(FilterOperator.LTE, epochTime);
        return this;
    }

    /**
     * Target selection operator.
     * Selects target "idle time on the ground".
     *
     * @return this
     */
    public FlightsFilterBuilder idle() {
        targetStatementsMap = idleStatementsMap;
        return this;
    }

    /**
     * Optional operator.
     * Removes from the set those flights in whose segments the arrival time is less than the departure time
     *
     * @return this
     */
    public FlightsFilterBuilder removeInvalidFlights() {
        allowInvalidFlights = false;
        return this;
    }

    /**
     * Target selection operator.
     * Selects target "arrival time".
     *
     * @return this
     */
    public FlightsFilterBuilder arrival() {
        targetStatementsMap = arrivalStatementsMap;
        return this;
    }

    /**
     * Target selection operator.
     * Selects target "departure time".
     *
     * @return this
     */
    public FlightsFilterBuilder departure() {
        targetStatementsMap = departureStatementsMap;
        return this;
    }

    /**
     * Returns an instance of <code>{@link FlightsFilter}</code> created from the operators set
     * on this builder.
     *
     * @return FlightsFilter
     */
    public FlightsFilter build() {
        if (allowInvalidFlights && arrivalStatementsMap.isEmpty() && departureStatementsMap.isEmpty() && idleStatementsMap.isEmpty())
            throw new IllegalStateException(ERROR_MESSAGE, new NullPointerException("All statement maps is null"));
        return new FlightsFilter(arrivalStatementsMap, departureStatementsMap, idleStatementsMap, allowInvalidFlights);
    }

}
