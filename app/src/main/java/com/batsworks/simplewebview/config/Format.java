package com.batsworks.simplewebview.config;

public class Format {

    private static final int BYTE = 1024 * 1024;

    public static double bytesToMB(float value) {
        if (value < 0.0) return value;
        return value / BYTE;
    }

    public static double bytesToMB(int value) {
        if (value < 0.0) return value;
        return (double) value / BYTE;
    }

    public static double bytesToMB(double value) {
        if (value < 0.0) return value;
        return (double) value / BYTE;
    }

}
