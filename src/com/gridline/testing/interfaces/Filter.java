package com.gridline.testing.interfaces;

public interface Filter<T> {
    T filtrate(T sourceData);
}
