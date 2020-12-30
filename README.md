# FlightsFilter
## API
```Java
FlightsFilter flightsFilter = new FlightsFilterBuilder()
    .arrival().gte(gteArrivalEpochTime).lt(ltArrivalEpochTime)
    .departure().eq(departureEqEpochTime)
    .idle().gt(gtIddleTime)
    .removeInvalidFlights()
    .build();
List<Flight> filteredFlightList = flightsFilter.filtrate(flightList);
```
## Performance
The filtering performance of idle flights is low due to the need for sequential segment comparison, which makes it impossible to easily implement parallel filtering.
All other filtering operations are based on `parallelStream`

## Serialization/Deserialization
The filter can be easily serialized and deserialized through the builder by calling the required builder method. It is also possible to implement the method `fromJson()` if necessary.
