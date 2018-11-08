package com.littlepay.models;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Tap implements Comparable<Tap> {
    private final String id;
    private final ZonedDateTime time;
    private final TapType tapType;
    private final Stop stopId;
    private final String companyId;
    private final String busId;
    private final String pan;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public Tap(String id, ZonedDateTime time, TapType tapType, Stop stopId, String companyId, String busId, String pan) {
        this.id = id;
        this.time = time;
        this.tapType = tapType;
        this.stopId = stopId;
        this.companyId = companyId;
        this.busId = busId;
        this.pan = pan;
    }

    public String getId() {
        return id;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public TapType getTapType() {
        return tapType;
    }

    public Stop getStopId() {
        return stopId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getBusId() {
        return busId;
    }

    public String getPan() {
        return pan;
    }

    public String formatTimeAsString() {
        return this.getTime().format(formatter);
    }

    @Override
    public int compareTo(Tap tap) {
        return this.time.isAfter(tap.getTime()) ? 1 : this.time.isBefore(getTime()) ? -1 : 0;
    }
}
