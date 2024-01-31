package com.periNimble.custom.processors.sample;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;

public class Read {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date read_time;
    public String measure_value;
    public String quality;
    public ArrayList<String> quality_detail;

    public Date getRead_time() {
        return read_time;
    }

    public void setRead_time(Date read_time) {
        this.read_time = read_time;
    }

    public String getMeasure_value() {
        return measure_value;
    }

    public void setMeasure_value(String measure_value) {
        this.measure_value = measure_value;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public ArrayList<String> getQuality_detail() {
        return quality_detail;
    }

    @Override
    public String toString() {
        return "Read{" +
                "read_time=" + read_time +
                ", measure_value='" + measure_value + '\'' +
                ", quality='" + quality + '\'' +
                ", quality_detail=" + quality_detail +
                '}';
    }

    public void setQuality_detail(ArrayList<String> quality_detail) {
        this.quality_detail = quality_detail;
    }

}
