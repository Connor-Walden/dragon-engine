package com.salami.dragon.engine.camera;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.event.Event;
import com.salami.dragon.engine.event.EventType;
import com.salami.dragon.engine.event.IListener;
import com.salami.dragon.engine.log.Logger;
import com.salami.dragon.engine.window.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Camera implements IListener {
    public static final float FOV = (float) Math.toRadians(90.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.f;

    private Matrix4f projectionMatrix;

    private Window window;

    public Camera(Window window) {
        this.window = window;

        List<EventType> eventsToListenTo = new ArrayList<EventType>();
        eventsToListenTo.add(EventType.WINDOW_RESIZE);

        Application.registerListener(eventsToListenTo, this);

        recalculateProjectionMatrix();
    }

    public void recalculateProjectionMatrix() {
        projectionMatrix = new Transformation().getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public void onEngineEvent(Event event) {
        recalculateProjectionMatrix();
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
