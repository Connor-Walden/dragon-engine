package com.salami.dragon.sandbox;

import com.salami.dragon.engine.IApplication;
import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.event.IListener;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.input.Input;
import com.salami.dragon.engine.log.Logger;
import com.salami.dragon.engine.render.Mesh;
import com.salami.dragon.engine.render.Texture;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Sandbox implements IApplication, IListener {
    static Camera camera;

    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }
    @Override public Camera CAMERA() { return camera; }

    Entity[] entities;

    private Vector2d previousPos;
    private Vector2d currentPos;
    private Vector2f displVec;

    public static void main(String[] args) throws Exception {
        camera = new Camera();

        Application.registerApp(new Sandbox());
    }

    @Override
    public void init() throws Exception {
        Application.setCursorCaptured(true);

        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();

        List<EventType> eventsToListenTo = new ArrayList<>();
        eventsToListenTo.add(EventType.KEY_PRESS);
        eventsToListenTo.add(EventType.MOUSE_MOVE);
        eventsToListenTo.add(EventType.MOUSE_BUTTON_PRESS);

        Application.registerListener(eventsToListenTo, this);

        float[] positions = new float[]{
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f
        };

        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f
        };

        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7
        };

        Texture texture = new Texture("rockwall.png");
        texture.loadToGL();
        Mesh mesh = new Mesh(positions, textCoords, indices, texture);

        entities = new Entity[4];
        entities[0] = new Entity(mesh);
        entities[0].setScale(0.5f);
        entities[0].setPosition(0, 0, -3);
        entities[1] = new Entity(mesh);
        entities[1].setScale(0.5f);
        entities[1].setPosition(0.5f, 2.5f, -5);
        entities[2] = new Entity(mesh);
        entities[2].setScale(0.5f);
        entities[2].setPosition(0, 0, -10.5f);
        entities[3] = new Entity(mesh);
        entities[3].setScale(0.5f);
        entities[3].setPosition(2f, 0, -5.5f);

        for(Entity entity : entities) {
            Application.registerEntity(entity);
        }
    }

    private void spinEntities() {
        for(Entity entity : entities) {
            float rotation = entity.getRotation().x + 0.5f;

            if (rotation > 360) {
                rotation = 0;
            }

            entity.setRotation(rotation, rotation, rotation);
        }
    }

    private void movePlayer() {
        float speed = 0.025f;

        if(Input.isKeyDown(Input.KEY_W)) {
            camera.movePosition(0, 0, -speed);
        }

        if(Input.isKeyDown(Input.KEY_A)) {
            camera.movePosition(-speed, 0, 0);
        }

        if(Input.isKeyDown(Input.KEY_S)) {
            camera.movePosition(0, 0, speed);
        }

        if(Input.isKeyDown(Input.KEY_D)) {
            camera.movePosition(speed, 0, 0);
        }

        if(Input.isKeyDown(Input.KEY_SPACE)) {
            camera.movePosition(0, speed, 0);
        }

        if(Input.isKeyDown(Input.KEY_LSHIFT)) {
            camera.movePosition(0, -speed, 0);
        }
    }

    private void rotateCamera() {
        float sensitivity = 0.1f;

        displVec.y = ((float) currentPos.x - (float) previousPos.x);
        displVec.x = ((float) currentPos.y - (float) previousPos.y);

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

        Vector2f rotVec = displVec;
        camera.moveRotation(rotVec.x * sensitivity, rotVec.y * sensitivity, 0);
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {
        spinEntities();
        movePlayer();
        rotateCamera();
    }

    @Override
    public void onEngineEvent(Event event) {
        Logger.log_error(event.getEventType().toString());
    }

    @Override
    public void onKeyOrMouseButtonEvent(Event event, int key) {
        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_ESCAPE) {
            Application.stop();
        }
    }

    @Override
    public void onMouseMoveEvent(Event event, double xPos, double yPos) {
        currentPos.x = xPos;
        currentPos.y = yPos;
    }

    @Override
    public void onMouseScrollEvent(Event event, double amount) {

    }
}
