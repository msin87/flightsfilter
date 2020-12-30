# FlightsFilter
## API
```Java
FlightsFilter flightsFilter = new FlightsFilterBuilder()
    .arrival().gte(gteArrivalEpochTime).lt(ltArrivalEpochTime)
    .departure().eq(departureEqEpochTime)
    .idle().gt(gtIddleTime)
    .removeInvalidFlights()
    .doParallel()
    .build();
List<Flight> filteredFlightList = flightsFilter.filter(flightList);
```
## Performance
All filtering operations can be parallel by using `doParallel()` operator

## Serialization/Deserialization
The filter can be easily serialized and deserialized through the builder by calling the required builder method. It is also possible to implement the method `fromJson()` if necessary.
