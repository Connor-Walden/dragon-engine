package com.salami.dragon.sandbox;

import com.salami.dragon.engine.IApplication;
import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.camera.FirstPersonCamera;
import com.salami.dragon.engine.ecs.entity.prefab.Cube;
import com.salami.dragon.engine.event.IListener;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.input.Input;

public class Sandbox implements IApplication, IListener {
    static Camera camera;

    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }
    @Override public Camera CAMERA() { return camera; }

    Cube cubeObject;
    FirstPersonCamera firstPersonCamera;

    public static void main(String[] args) throws Exception {
        camera = new Camera();

        // TODO: Add validation to determine if the camera has been created or not.
        Application.registerApp(new Sandbox());
    }

    @Override
    public void init() throws Exception {
        cubeObject = new Cube().setScale(0.5f).setPosition(0, 0, -5);
        firstPersonCamera = new FirstPersonCamera(camera);

        // Application configuration phase
        Application.setCursorCaptured(true);

        // Application registration phase
        Application.registerListener(this, EventType.KEY_PRESS, EventType.MOUSE_MOVE);
        Application.registerEntity(cubeObject.getCubeEntity());
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {
        firstPersonCamera.movePlayer();
        firstPersonCamera.rotateCamera();
    }

    @Override
    public void onEngineEvent(Event event) {}

    @Override
    public void onKeyOrMouseButtonEvent(Event event, int key) {
        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_ESCAPE) {
            Application.stop();
        }
    }

    @Override
    public void onMouseMoveEvent(Event event, double xPos, double yPos) {
        firstPersonCamera.updateMousePos(xPos, yPos);
    }

    @Override
    public void onMouseScrollEvent(Event event, double amount) {}
}
