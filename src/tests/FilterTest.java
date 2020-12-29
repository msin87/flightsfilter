package tests;

import com.gridline.testing.domain.Flight;
import com.gridline.testing.filters.SegmentFilter;
import com.gridline.testing.utils.FilterOperator;
import com.gridline.testing.db.FlightsDao;
import com.gridline.testing.db.FlightsRepository;
import com.gridline.testing.utils.SegmentFilterBehavior;
import com.gridline.testing.utils.SegmentFilterBuilder;
import com.gridline.testing.utils.SegmentFilterType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterTest {
    private final List<Flight> flightList = FlightsDao.getInstance(new FlightsRepository()).getAll();
    private final long compareTime = flightList.get(0).getSegments().get(0).getArrivalDate().toEpochSecond(ZoneOffset.UTC);

    @Test
    public void arrivalEqFilterTest() {
        int[] resultId = {0, 1, 4, 5};
        SegmentFilter eqFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ONLY_FILTERED_SEGMENTS).eq(compareTime);
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
        SegmentFilter gteFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ALL_SEGMENTS).gte(compareTime).build();
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
        SegmentFilter gtFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ALL_SEGMENTS).gt(compareTime).build();
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
        SegmentFilter ltFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ALL_SEGMENTS).lt(compareTime).build();
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

        SegmentFilter ltFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ALL_SEGMENTS).lte(compareTime).build();
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i < resultId.length; i++) {
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(), flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
        System.out.println("arrivalLteFilterTest: " + filteredFlights);
    }

    @Test
    public void combineArriveFilterTest() {
        long ltTime = LocalDateTime.ofEpochSecond(compareTime, 0, ZoneOffset.UTC).plusHours(5).toEpochSecond(ZoneOffset.UTC);
        long gtTime = compareTime;
        SegmentFilter ltArriveFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ONLY_FILTERED_SEGMENTS).lt(ltTime).gt(gtTime).build();
        List<Flight> filteredFlights = ltArriveFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), 3);
        assertEquals(filteredFlights.get(0).getSegments().get(0).getArrivalDate(), flightList.get(1).getSegments().get(1).getArrivalDate());
        assertEquals(filteredFlights.get(1).getSegments().get(0).getArrivalDate(), flightList.get(4).getSegments().get(1).getArrivalDate());
        assertEquals(filteredFlights.get(2).getSegments().get(0).getArrivalDate(), flightList.get(5).getSegments().get(1).getArrivalDate());
        System.out.println("combineArriveFilterTest: " + filteredFlights);

        ltTime = compareTime;
        gtTime = LocalDateTime.ofEpochSecond(compareTime, 0, ZoneOffset.UTC).plusHours(5).toEpochSecond(ZoneOffset.UTC);
        SegmentFilter wrongArriveFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ONLY_FILTERED_SEGMENTS).lt(ltTime).gt(gtTime).build();
        filteredFlights = wrongArriveFilter.filtrate(flightList);
        assertTrue(filteredFlights.isEmpty());
    }

    @Test
    public void combineArriveDepartureFilterTest() {
        long gteHours = SegmentFilterBuilder.hoursToSeconds(2);
        SegmentFilter segmentFilter = new SegmentFilterBuilder().onGround().gte(gteHours).build();
        List<Flight> filteredFlights = segmentFilter.filtrate(flightList);
        assertEquals(filteredFlights.get(0), flightList.get(4));
        assertEquals(filteredFlights.get(1), flightList.get(5));
        System.out.println("combineArriveDepartureFilterTest gte=2: " + filteredFlights);

        gteHours = SegmentFilterBuilder.hoursToSeconds(1);
        long ltHours = SegmentFilterBuilder.hoursToSeconds(3);
        segmentFilter = new SegmentFilterBuilder().onGround().gt(gteHours).lt(ltHours).build();
        filteredFlights = segmentFilter.filtrate(flightList);
        assertEquals(filteredFlights.get(0), flightList.get(5));
        System.out.println("combineArriveDepartureFilterTest gte=1, lt=3: " + filteredFlights);

    }

}
