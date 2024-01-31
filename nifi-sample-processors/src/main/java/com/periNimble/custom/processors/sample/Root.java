package com.periNimble.custom.processors.sample;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Root {
    public String message_identifier;
    public String message_description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date message_timestamp;
    public String message_source;
    public String message_read_count;
    public Measures measures;

    public String getMessage_identifier() {
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

    @Override
    public String toString() {
        return "Root{" +
                "message_identifier='" + message_identifier + '\'' +
                ", message_description='" + message_description + '\'' +
                ", message_timestamp=" + message_timestamp +
                ", message_source='" + message_source + '\'' +
                ", message_read_count='" + message_read_count + '\'' +
                ", measures=" + measures +
                '}';
    }

}

