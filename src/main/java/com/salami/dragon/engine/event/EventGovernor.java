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

    public void fireEvent(EventType name) {
        Event eventToFire = eventMap.get(name);

        eventToFire.fireEvent();
    }

    public void fireEvent(EventType name, int key) {
        Event eventToFire = eventMap.get(name);

        eventToFire.fireKeyEvent(key);
    }

    public void fireEvent(EventType name, double xPos, double yPos) {
        Event eventToFire = eventMap.get(name);

        eventToFire.fireMouseMoveEvent(xPos, yPos);
    }

    public void fireEvent(EventType name, double amount) {
        Event eventToFire = eventMap.get(name);

        eventToFire.fireMouseScrollEvent(amount);
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

    public void registerListeners(Map<EventType, IListener> listenerMap) {
        for(EventType eventType : listenerMap.keySet()) {
            registerListener(eventType, listenerMap.get(eventType));
        }
    }
}
