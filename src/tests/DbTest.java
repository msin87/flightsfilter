package tests;

import com.gridnine.testing.db.FlightsDao;
import com.gridnine.testing.db.FlightsRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class DbTest {
    private FlightsDao flightsDao = FlightsDao.getInstance(new FlightsRepository());

    @Test
    public void getAllTest() {
        assertFalse(flightsDao.getAll().isEmpty());
        System.out.println(flightsDao.getAll());
    }
}
