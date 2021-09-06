package com.salami.dragon.engine.event;

import java.util.List;
import java.util.ArrayList;

import static com.salami.dragon.engine.event.EventType.*;

public class Event {
    List<IListener> listeners;

    EventType eventType;

    public Event(EventType eventType) {
        this.eventType = eventType;

        listeners = new ArrayList<IListener>();
    }

    public void fireEngineEvent() {
        if(eventType == EventType.APPLICATION_INIT || eventType == EventType.APPLICATION_START || eventType == EventType.APPLICATION_STOP || eventType == EventType.APPLICATION_TICK ||
            eventType == EventType.WINDOW_OPEN || eventType == EventType.WINDOW_CLOSE || eventType == EventType.WINDOW_FOCUS || eventType == EventType.WINDOW_LOST_FOCUS || eventType == EventType.WINDOW_RESIZE || eventType == EventType.WINDOW_MAXIMIZE || eventType == EventType.WINDOW_UN_MAXIMIZE || eventType == EventType.WINDOW_MOVE) {
            for(IListener listener : listeners) {
                listener.onEngineEvent(this);
            }
        }
    }

    public void fireComponentEvent() {
        if(eventType == COMPONENTS_INIT || eventType == COMPONENTS_TICK) {
            for(IListener listener : listeners) {
                listener.onComponentEvent(this);
            }
        }
    }

    public void fireKeyEvent(int key) {
        if(eventType == EventType.KEY_PRESS || eventType == EventType.KEY_RELEASE || eventType == EventType.MOUSE_BUTTON_PRESS || eventType == EventType.MOUSE_BUTTON_RELEASE) {
            for(IListener listener : listeners) {
                listener.onKeyOrMouseButtonEvent(this, key);
            }
        }
    }

    public void fireMouseMoveEvent(double xPos, double yPos) {
        if(eventType == EventType.MOUSE_MOVE) {
            for(IListener listener : listeners) {
                listener.onMouseMoveEvent(this, xPos, yPos);
            }
        }
    }

    public void fireMouseScrollEvent(double amount) {
        if(eventType == EventType.MOUSE_SCROLL) {
            for(IListener listener : listeners) {
                listener.onMouseScrollEvent(this, amount);
            }
        }
    }

    public void fireAudioEvent() {
        if(eventType == AUDIO_INIT || eventType == AUDIO_SOURCE_INIT || eventType == AUDIO_SOURCE_PLAY || eventType == AUDIO_SOURCE_STOP || eventType == AUDIO_SOURCE_PAUSE || eventType == AUDIO_SOURCE_CLEANUP ||
        eventType == AUDIO_SOURCE_NEW_AUDIO || eventType == AUDIO_SOURCE_NEW_POSITION || eventType == AUDIO_SOURCE_NEW_VELOCITY || eventType == AUDIO_SOURCE_NEW_GAIN ||
        eventType == AUDIO_LISTENER_INIT || eventType == AUDIO_LISTENER_NEW_POSITION || eventType == AUDIO_LISTENER_NEW_VELOCITY || eventType == AUDIO_LISTENER_NEW_ORIENTATION) {
            for(IListener listener : listeners) {
                listener.onAudioEvent(this);
            }
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean registerListener(IListener listener) {
        try {
            listeners.add(listener);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean unregisterListener(IListener listener) {
        return listeners.remove(listener);
    }
}
