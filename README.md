# FlightsFilter
## API
```Java
FlightsFilter flightsFilter = new FlightsFilterBuilder()
    .arrival().gte(gteArrivalEpochTime).lt(ltArrivalEpochTime)
    .departure().eq(departureEqEpochTime)
    .idle().gt(gtIddleTime)
    .removeInvalidFlights()
    .build();
List<Flight> filteredFlightList = flightsFilter.filter(hugeFlightList);
filteredFlightList = flightsFilter.doSequential().filter(smallFlightList);
filteredFlightList = flightsFilter.doParallel().filter(anotherHugeFlightList);
```
## Performance
All filtering operations can be parallel by using `doParallel()` operator of `FlightsFilterBuilder`.
It is also possible to switch on the fly between parallel and sequential execution for the filter instance. If the filter received a small data set, then it makes sense to switch to sequential execution, otherwise to parallel execution.

## Serialization/Deserialization
The filter can be easily serialized and deserialized through the builder by calling the required builder method. It is also possible to implement the method `fromJson()` if necessary.
