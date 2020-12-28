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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrivalFilterTest {
    private final List<Flight> flightList = FlightsDao.getInstance(new FlightsRepository()).getAll();
    private final LocalDateTime compareTime = flightList.get(0).getSegments().get(0).getArrivalDate();
    @Test
    public void arrivalEqFilterTest(){
        int[] resultId = {0,1,4,5};
        SegmentFilter eqFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE,SegmentFilterBehavior.GET_ONLY_FILTERED_SEGMENTS).eq(compareTime);
        List<Flight> filteredFlights = eqFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i<resultId.length; i++){
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(),flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
        System.out.println(filteredFlights);
    }
    @Test
    public void arrivalGteFilterTest(){
        int[] resultId = {0,1,4,5};
        SegmentFilter gteFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE,SegmentFilterBehavior.GET_ALL_SEGMENTS).gte(compareTime).build();
        List<Flight> filteredFlights = gteFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i<resultId.length; i++){
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(),flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
    }
    @Test
    public void arrivalGtFilterTest(){
        int[] resultId = {1,4,5};
        SegmentFilter gtFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE, SegmentFilterBehavior.GET_ALL_SEGMENTS).gt(compareTime).build();
        List<Flight> filteredFlights = gtFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i<resultId.length; i++){
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(),flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
    }
    @Test
    public void arrivalLtFilterTest(){
        int[] resultId = {2,3};
        SegmentFilter ltFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE,SegmentFilterBehavior.GET_ALL_SEGMENTS).lt(compareTime).build();
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i<resultId.length; i++){
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(),flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
    }
    @Test
    public void arrivalLteFilterTest(){
        int[] resultId = {0,1,2,3,4,5};

        SegmentFilter ltFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE,SegmentFilterBehavior.GET_ALL_SEGMENTS).lte(compareTime).build();
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), resultId.length);
        for (int i = 0; i<resultId.length; i++){
            assertEquals(filteredFlights.get(i).getSegments().get(0).getArrivalDate(),flightList.get(resultId[i]).getSegments().get(0).getArrivalDate());
        }
    }
    @Test
    public void combineLtGtFilterTest(){
        SegmentFilter ltFilter = new SegmentFilterBuilder(SegmentFilterType.ARRIVE,SegmentFilterBehavior.GET_ONLY_FILTERED_SEGMENTS).lt(compareTime).gt(compareTime.minusHours(7)).build();
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        System.out.println(filteredFlights);
    }

}
