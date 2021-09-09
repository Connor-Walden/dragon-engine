package com.salami.dragon.engine.event;

import java.util.HashMap;
import java.util.Map;

public class EventGovernor {
    Map<EventType, Event> eventMap;

    public EventGovernor(Map<EventType, Event> initialMap) {
        if (initialMap != null) {
            eventMap = initialMap;
        } else {
            eventMap = new HashMap<EventType, Event>();
        }
    }

    public void fireEvent(EventType name, String eventTime) {
        Event eventToFire = eventMap.get(name);

        // Will only fire the right event as there is in-built validation for this.
        eventToFire.fireEngineEvent(eventTime);
        eventToFire.fireAudioEvent(eventTime);
        eventToFire.fireComponentEvent(eventTime);
    }

    public void fireEvent(EventType name, String eventTime, int key) {
        Event eventToFire = eventMap.get(name);

        eventToFire.fireKeyEvent(eventTime, key);
    }

    public void fireEvent(EventType name, String eventTime, double xPos, double yPos) {
        Event eventToFire = eventMap.get(name);

        eventToFire.fireMouseMoveEvent(eventTime, xPos, yPos);
    }

    public void fireEvent(EventType name, String eventTime, double amount) {
        Event eventToFire = eventMap.get(name);

        eventToFire.fireMouseScrollEvent(eventTime, amount);
    }

    public void registerEvent(EventType name, Event event) {
        eventMap.put(name, event);
    }

    public void registerEvents(Map<EventType, Event> eventMap) {
        for(EventType eventType : eventMap.keySet()) {
            registerEvent(eventType, eventMap.get(eventType));
        }
    }

    public void registerListener(EventType eventType, IListener listener) {
        eventMap.get(eventType).registerListener(listener);
    }

    public void registerListeners(IListener listener, EventType... events) {
        for(EventType eventType : events) {
            registerListener(eventType, listener);
        }
    }
}
