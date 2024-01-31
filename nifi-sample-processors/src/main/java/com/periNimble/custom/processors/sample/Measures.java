package com.periNimble.custom.processors.sample;


import java.util.ArrayList;

public class Measures {
    public String read_source;
    public String device;
    public String measure_name;
    public ArrayList<Read> reads;

    @Override
    public String toString() {
        return "Measures{" +
                "read_source='" + read_source + '\'' +
                ", device='" + device + '\'' +
                ", measure_name='" + measure_name + '\'' +
                ", reads=" + reads +
                '}';
    }

    public String getRead_source() {
        return read_source;
    }

    public void setRead_source(String read_source) {
        this.read_source = read_source;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getMeasure_name() {
        return measure_name;
    }

    public void setMeasure_name(String measure_name) {
        this.measure_name = measure_name;
    }

    public ArrayList<Read> getReads() {
        return reads;
    }

    public void setReads(ArrayList<Read> reads) {
        this.reads = reads;
    }
}

