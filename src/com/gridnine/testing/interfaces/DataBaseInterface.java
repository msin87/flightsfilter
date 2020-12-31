package com.gridnine.testing.interfaces;

import java.util.List;

public interface DataBaseInterface<T> {
    List<T> getAll();
}
