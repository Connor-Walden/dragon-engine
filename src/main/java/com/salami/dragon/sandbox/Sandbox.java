package com.salami.dragon.sandbox;

import com.salami.dragon.engine.IApplication;
import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.event.IListener;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.log.Logger;
import com.salami.dragon.engine.render.Mesh;

import java.util.ArrayList;
import java.util.List;

public class Sandbox implements IApplication, IListener {
    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }

    public static void main(String[] args) throws Exception {
        Application.registerApp(
            new Sandbox()
        );
    }

    @Override
    public void init() {
        List<EventType> eventsToListenTo = new ArrayList<>();
        eventsToListenTo.add(EventType.KEY_PRESS);
        eventsToListenTo.add(EventType.MOUSE_BUTTON_PRESS);

        Application.registerListener(eventsToListenTo, this);

        float[] vertices = new float[]{
                -0.5f, 0.5f, -1.0f,
                -0.5f, -0.5f, -1.0f,
                0.5f, -0.5f, -1.0f,
                0.5f, 0.5f, -1.0f
        };

        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f
        };

        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
        };

        Application.registerEntity(
            new Entity(
                new Mesh(vertices, colours, indices)
            )
        );
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {

    }

    @Override
    public void onEngineEvent(Event event) {
        Logger.log_error(event.getEventType().toString());
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
