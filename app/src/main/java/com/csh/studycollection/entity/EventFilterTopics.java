package com.csh.studycollection.entity;

import java.io.Serializable;

public class EventFilterTopics implements Serializable {
    public Subject sb;

    public EventFilterTopics(Subject sb) {
        this.sb = sb;
    }
    public EventFilterTopics() {
    }
}
