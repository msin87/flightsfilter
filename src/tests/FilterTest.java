package tests;

import com.gridnine.testing.models.Flight;
import com.gridnine.testing.filter.FlightFilter;
import com.gridnine.testing.db.FlightsDao;
import com.gridnine.testing.db.FlightsRepository;
import com.gridnine.testing.utils.FlightFilterBuilder;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilterTest {
    private final List<Flight> flightList = FlightsDao.getInstance(new FlightsRepository()).getAll();
    private final long threeDaysFromNowTime = flightList.get(0).getSegments().get(0).getArrivalDate().toEpochSecond(ZoneOffset.UTC);

    @Test
    public void arrivalEqFilterTest() {
        int[] resultId = {0, 1, 4, 5};
        FlightFilter eqFilter = new FlightFilterBuilder().arrival().eq(threeDaysFromNowTime);
        List<Flight> filteredFlights = eqFilter.filtrate(flightList);
        assertEquals(resultId.length, filteredFlights.size());
        for (int i = 0; i < resultId.length; i++) {
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(), flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
        System.out.println("arrivalEqFilterTest: " + filteredFlights);
    }

    @Test
    public void arrivalGteFilterTest() {
        int[] resultId = {0, 1, 4, 5};
        FlightFilter gteFilter = new FlightFilterBuilder().arrival().gte(threeDaysFromNowTime).build();
        List<Flight> filteredFlights = gteFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i < resultId.length; i++) {
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(), flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
        System.out.println("arrivalGteFilterTest: " + filteredFlights);
    }

    @Test
    public void arrivalGtFilterTest() {
        int[] resultId = {1, 4, 5};
        FlightFilter gtFilter = new FlightFilterBuilder().arrival().gt(threeDaysFromNowTime).build();
        List<Flight> filteredFlights = gtFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i < resultId.length; i++) {
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(), flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
        System.out.println("arrivalGtFilterTest: " + filteredFlights);
    }

    @Test
    public void arrivalLtFilterTest() {
        int[] resultId = {2, 3};
        FlightFilter ltFilter = new FlightFilterBuilder().arrival().lt(threeDaysFromNowTime).build();
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i < resultId.length; i++) {
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(), flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
        System.out.println("arrivalLtFilterTest: " + filteredFlights);
    }

    @Test
    public void arrivalLteFilterTest() {
        int[] resultId = {0, 1, 2, 3, 4, 5};

        FlightFilter ltFilter = new FlightFilterBuilder().arrival().lte(threeDaysFromNowTime).build();
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i < resultId.length; i++) {
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(), flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
        System.out.println("arrivalLteFilterTest: " + filteredFlights);
    }

    @Test
    public void combineArriveFilterTest() {
        long ltTime = LocalDateTime.ofEpochSecond(threeDaysFromNowTime, 0, ZoneOffset.UTC).plusHours(5).toEpochSecond(ZoneOffset.UTC);
        long gtTime = threeDaysFromNowTime;
        FlightFilter ltArriveFilter = new FlightFilterBuilder().arrival().lt(ltTime).gt(gtTime).build();
        List<Flight> filteredFlights = ltArriveFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), 3);
        assertEquals(filteredFlights.get(0).getSegments().get(1).getArrivalDate(), flightList.get(1).getSegments().get(1).getArrivalDate());
        assertEquals(filteredFlights.get(1).getSegments().get(1).getArrivalDate(), flightList.get(4).getSegments().get(1).getArrivalDate());
        assertEquals(filteredFlights.get(2).getSegments().get(1).getArrivalDate(), flightList.get(5).getSegments().get(1).getArrivalDate());
        System.out.println("combineArriveFilterTest: " + filteredFlights);

        ltTime = threeDaysFromNowTime;
        gtTime = LocalDateTime.ofEpochSecond(threeDaysFromNowTime, 0, ZoneOffset.UTC).plusHours(5).toEpochSecond(ZoneOffset.UTC);
        FlightFilter wrongArriveFilter = new FlightFilterBuilder().arrival().lt(ltTime).gt(gtTime).build();
        filteredFlights = wrongArriveFilter.filtrate(flightList);
        assertTrue(filteredFlights.isEmpty());
    }

    @Test
    public void idleFilterTest() {
        long gteHours = Duration.ofHours(2).toSeconds();
        FlightFilter flightFilter = new FlightFilterBuilder().idle().gte(gteHours).build();
        List<Flight> filteredFlights = flightFilter.filtrate(flightList);
        assertEquals(filteredFlights.get(0), flightList.get(4));
        assertEquals(filteredFlights.get(1), flightList.get(5));
        System.out.println("combineArriveDepartureFilterTest gte=2: " + filteredFlights);

        gteHours = Duration.ofHours(1).toSeconds();
        long ltHours = Duration.ofHours(3).toSeconds();
        flightFilter = new FlightFilterBuilder().idle().gt(gteHours).lt(ltHours).build();
        filteredFlights = flightFilter.filtrate(flightList);
        assertEquals(filteredFlights.get(0), flightList.get(5));
        System.out.println("combineArriveDepartureFilterTest gte=1, lt=3: " + filteredFlights);
    }

    @Test
    public void combineArriveDepartureTest() {
        long iddleGteTime = Duration.ofHours(2).toSeconds();
        FlightFilter flightFilter = new FlightFilterBuilder()
                .departure().gte(threeDaysFromNowTime + Duration.ofHours(2).toSeconds()).lt(threeDaysFromNowTime + Duration.ofHours(6).toSeconds())
                .arrival().gt(threeDaysFromNowTime + Duration.ofHours(4).toSeconds())
                .idle().gte(iddleGteTime)
                .build();
        List<Flight> filteredFlights = flightFilter.filtrate(flightList);
        assertEquals(filteredFlights.get(0), flightList.get(5));
    }
    @Test void removeOnlyInvalidFlightsTest(){
        FlightFilter flightFilter = new FlightFilterBuilder().removeInvalidFlights().build();
        List<Flight> filteredFlightList = flightFilter.filtrate(flightList);
        assertFalse(filteredFlightList.contains(flightList.get(3)));

    }

}
