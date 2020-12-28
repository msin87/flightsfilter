package com.gridline.testing.db;

import java.util.List;

public interface DataBaseInterface<T> {
    List<T> getAll();
}
