package com.salami.dragon.sandbox;

import com.salami.dragon.engine.*;
import com.salami.dragon.engine.event.*;

import java.util.ArrayList;
import java.util.List;

public class Sandbox implements IApplication, IListener {
    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }

    public static void main(String[] args) {
        Application.registerApp(
            new Sandbox()
        );
    }

    @Override
    public void init() {
        List<EventType> eventsToListenTo = new ArrayList<>();
        eventsToListenTo.add(EventType.KEY_PRESS);
        eventsToListenTo.add(EventType.KEY_RELEASE);
        eventsToListenTo.add(EventType.MOUSE_MOVE);
        eventsToListenTo.add(EventType.MOUSE_BUTTON_PRESS);
        eventsToListenTo.add(EventType.MOUSE_BUTTON_RELEASE);
        eventsToListenTo.add(EventType.MOUSE_SCROLL);

        Application.registerListener(eventsToListenTo, this);
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {

    }

    @Override
    public void onEngineEvent(Event event) {

    }

    @Override
    public void onKeyOrMouseButtonEvent(Event event, int key) {

    }

    @Override
    public void onMouseMoveEvent(Event event, double xPos, double yPos) {

    }

    @Override
    public void onMouseScrollEvent(Event event, double amount) {

    }
}
