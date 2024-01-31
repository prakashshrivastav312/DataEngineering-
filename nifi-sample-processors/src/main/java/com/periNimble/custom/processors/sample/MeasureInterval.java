package com.periNimble.custom.processors.sample;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@ToString
public class MeasureInterval {
    public String message_identifier;
    public String message_description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date message_timestamp;
    public String message_source;
    public String message_read_count;
    public String read_source;
    public String device;
    public String measure_name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date read_time;
    public String measure_value;
    public String quality;
    public ArrayList<String> quality_detail;

    /*public String getMessage_identifier() {
        return message_identifier;
    }

    public void setMessage_identifier(String message_identifier) {
        this.message_identifier = message_identifier;
    }

    public String getMessage_description() {
        return message_description;
    }

    public void setMessage_description(String message_description) {
        this.message_description = message_description;
    }

    public Date getMessage_timestamp() {
        return message_timestamp;
    }

    public void setMessage_timestamp(Date message_timestamp) {
        this.message_timestamp = message_timestamp;
    }

    public String getMessage_source() {
        return message_source;
    }

    public void setMessage_source(String message_source) {
        this.message_source = message_source;
    }

    public String getMessage_read_count() {
        return message_read_count;
    }

    public void setMessage_read_count(String message_read_count) {
        this.message_read_count = message_read_count;
    }

    public Measures getMeasures() {
        return measures;
    }

    public void setMeasures(Measures measures) {
        this.measures = measures;
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
*/
    public ArrayList<String> getQuality_detail() {
        return quality_detail;
    }

    public void setQuality_detail(ArrayList<String> quality_detail) {
        this.quality_detail = quality_detail;
    }

   /* @Override
    public String toString() {
        return "MeasureInterval{" +
                "message_identifier='" + message_identifier + '\'' +
                ", message_description='" + message_description + '\'' +
                ", message_timestamp=" + message_timestamp +
                ", message_source='" + message_source + '\'' +
                ", message_read_count='" + message_read_count + '\'' +
                ", measures=" + measures +
                ", read_source='" + read_source + '\'' +
                ", device='" + device + '\'' +
                ", measure_name='" + measure_name + '\'' +
                ", read_time=" + read_time +
                ", measure_value='" + measure_value + '\'' +
                ", quality='" + quality + '\'' +
                ", quality_detail=" + quality_detail +
                '}';
    }*/
}
