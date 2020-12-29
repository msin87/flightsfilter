package com.gridline.testing;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Main {
    public static void main(String[] args) {
        long currentEpoch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

    }
}
