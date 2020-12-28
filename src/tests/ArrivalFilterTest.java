package tests;

import com.gridline.testing.domain.Flight;
import com.gridline.testing.filters.ArrivalFilter;
import com.gridline.testing.utils.FilterOperator;
import com.gridnine.testing.db.FlightsDao;
import com.gridnine.testing.db.FlightsRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrivalFilterTest {
    private final List<Flight> flightList = FlightsDao.getInstance(new FlightsRepository()).getAll();
    private final LocalDateTime compareTime = flightList.get(0).getSegments().get(0).getArrivalDate();
    @Test
    public void arrivalEqFilterTest(){
        ArrivalFilter eqFilter = new ArrivalFilter(compareTime, FilterOperator.EQ);
        List<Flight> filteredFlights = eqFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), 4);
        assertEquals(filteredFlights.get(0),flightList.get(0));
        assertEquals(filteredFlights.get(1),flightList.get(1));
        assertEquals(filteredFlights.get(2),flightList.get(4));
        assertEquals(filteredFlights.get(3),flightList.get(5));
    }
    @Test
    public void arrivalGteFilterTest(){
        ArrivalFilter gteFilter = new ArrivalFilter(compareTime, FilterOperator.GTE);
        List<Flight> filteredFlights = gteFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), 4);
        assertEquals(filteredFlights.get(0),flightList.get(0));
        assertEquals(filteredFlights.get(1),flightList.get(1));
        assertEquals(filteredFlights.get(2),flightList.get(4));
        assertEquals(filteredFlights.get(3),flightList.get(5));
    }
    @Test
    public void arrivalGtFilterTest(){
        ArrivalFilter gtFilter = new ArrivalFilter(compareTime, FilterOperator.GT);
        List<Flight> filteredFlights = gtFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), 3);
        assertEquals(filteredFlights.get(0),flightList.get(1));
        assertEquals(filteredFlights.get(1),flightList.get(4));
        assertEquals(filteredFlights.get(2),flightList.get(5));
    }
    @Test
    public void arrivalLtFilterTest(){
        ArrivalFilter ltFilter = new ArrivalFilter(compareTime, FilterOperator.LT);
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), 2);
        assertEquals(filteredFlights.get(0),flightList.get(2));
        assertEquals(filteredFlights.get(1),flightList.get(3));
    }
    @Test
    public void arrivalLteFilterTest(){
        ArrivalFilter ltFilter = new ArrivalFilter(compareTime, FilterOperator.LTE);
        List<Flight> filteredFlights = ltFilter.filtrate(flightList);
        assertEquals(filteredFlights.size(), 6);
    }
}
