package com.salami.dragon.sandbox;

import com.salami.dragon.engine.IApplication;
import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.camera.FirstPersonCamera;
import com.salami.dragon.engine.ecs.entity.prefab.Bunny;
import com.salami.dragon.engine.ecs.entity.prefab.Cube;
import com.salami.dragon.engine.event.IListener;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.input.Input;
import com.salami.dragon.engine.light.DirectionalLight;
import com.salami.dragon.engine.light.PointLight;
import com.salami.dragon.engine.render.RenderMode;
import org.joml.Vector3f;

public class Sandbox implements IApplication, IListener {
    static Camera camera;

    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }
    @Override public Camera CAMERA() { return camera; }

    Cube cubeObject;
    Bunny bunnyObject;

    FirstPersonCamera firstPersonCamera;
    PointLight playerLight = new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), 1.0f);

    public static void main(String[] args) throws Exception {
        camera = new Camera();

        // TODO: Add validation to determine if the camera has been created or not.
        Application.registerApp(new Sandbox());
    }

    @Override
    public void init() throws Exception {
        firstPersonCamera = new FirstPersonCamera(camera);

        // Application configuration phase
        Application.setCursorCaptured(true);
        Application.getSceneLight().setAmbientLight(new Vector3f(0.15f, 0.15f, 0.15f));
        Application.getSceneLight().setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1.0f));
        Application.setDoDaylightCycle(false);

        // Application registration phase
        Application.registerListener(this, EventType.KEY_PRESS, EventType.MOUSE_MOVE);

        cubeObject = new Cube().setScale(0.5f).setPosition(0, 0, -5);
        Application.registerEntity(cubeObject.getCubeEntity());

        bunnyObject = new Bunny().setScale(0.5f).setPosition(2, 0, -5);
        Application.registerEntity(bunnyObject.getBunnyEntity());
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {
        firstPersonCamera.movePlayer();
        firstPersonCamera.rotateCamera();

        playerLight.setPosition(camera.getPosition());
    }

    @Override
    public void onEngineEvent(Event event) {}

    @Override
    public void onKeyOrMouseButtonEvent(Event event, int key) {
        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_ESCAPE) {
            Application.stop();
        }

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F1) {
            Application.setRenderMode(RenderMode.FILL);
        }

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F2) {
            Application.setRenderMode(RenderMode.LINE);
        }

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F3) {
            Application.setRenderMode(RenderMode.POINT);
        }
    }

    @Override
    public void onMouseMoveEvent(Event event, double xPos, double yPos) {
        firstPersonCamera.updateMousePos(xPos, yPos);
    }

    @Override
    public void onMouseScrollEvent(Event event, double amount) {}
}
