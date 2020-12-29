package com.gridline.testing;

import com.gridline.testing.db.FlightsDao;
import com.gridline.testing.db.FlightsRepository;
import com.gridline.testing.models.Flight;
import com.gridline.testing.filter.FlightFilter;
import com.gridline.testing.utils.FlightFilterBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static String listToString(List<?> list) {
        return list.stream().map(element -> element.toString() + "\r\n").collect(Collectors.joining());
    }

    public static void main(String[] args) {
        long currentEpoch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        List<Flight> flightList = FlightsDao.getInstance(new FlightsRepository()).getAll();
        FlightFilter currentTimeFlightsFilter = new FlightFilterBuilder().departure().lt(currentEpoch).build();
        FlightFilter invalidFlightsFilter = new FlightFilterBuilder().removeInvalidFlights().build();
        FlightFilter idleFlightsFilter = new FlightFilterBuilder().idle().gt(FlightFilterBuilder.hoursToSeconds(2)).build();
        System.out.println("[Текущее время: " + LocalDateTime.now() + "]");
        System.out.println("--------------------------------Все вылеты--------------------------------\r\n" + listToString(flightList));
        System.out.println("--------------------Вылеты до текущего момента времени--------------------\r\n" + listToString(currentTimeFlightsFilter.filtrate(flightList)));
        System.out.println("------------Без сегментов с датой прилёта раньше даты вылета--------------\r\n" + listToString(invalidFlightsFilter.filtrate(flightList)));
        System.out.println("----------Общее время, проведённое на земле превышает два часа------------\r\n" + listToString(idleFlightsFilter.filtrate(flightList)));
    }
}
