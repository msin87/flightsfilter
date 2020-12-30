package com.gridnine.testing.interfaces;

public interface Filter<T> {
    T filtrate(T sourceData);
}
