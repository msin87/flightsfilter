package com.gridnine.testing.db;

import com.gridline.testing.domain.Flight;

import java.util.List;

public class FlightsDao {
    private static FlightsDao instance;
    DataBaseInterface<Flight> db;
    private FlightsDao(DataBaseInterface<Flight> db){
        this.db = db;
    }
    private FlightsDao(){}
    public static synchronized FlightsDao getInstance(DataBaseInterface<Flight> db) {
        if (instance==null){
            instance = new FlightsDao(db);
        }
        return instance;
    }
    public List<Flight> getAll(){
        return db.getAll();
    }
}
