package com.salami.dragon.engine.event;

public enum EventType {
    APPLICATION_START, APPLICATION_STOP, APPLICATION_INIT, APPLICATION_TICK,
    WINDOW_OPEN, WINDOW_CLOSE, WINDOW_RESIZE, WINDOW_MOVE, WINDOW_LOST_FOCUS, WINDOW_FOCUS, WINDOW_MAXIMIZED, WINDOW_UN_MAXIMIZED,
    KEY_PRESS, KEY_RELEASE,
    MOUSE_BUTTON_PRESS, MOUSE_BUTTON_RELEASE, MOUSE_MOVE, MOUSE_SCROLL
}