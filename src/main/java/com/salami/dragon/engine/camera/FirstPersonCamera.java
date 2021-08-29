package com.salami.dragon.engine.camera;

import com.salami.dragon.engine.input.Input;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class FirstPersonCamera {

    public static final float MOVE_SPEED = 0.025f;
    public static final float SENSITIVITY = 0.1f;

    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private final Vector2f displVec;

    Camera camera;

    public FirstPersonCamera(Camera camera) {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();

        this.camera = camera;
    }

    public void updateMousePos(double mouseX, double mouseY) {
        currentPos.x = mouseX;
        currentPos.y = mouseY;
    }

    public void movePlayer() {
        if(Input.isKeyDown(Input.KEY_W)) {
            camera.movePosition(0, 0, -MOVE_SPEED);
        }

        if(Input.isKeyDown(Input.KEY_A)) {
            camera.movePosition(-MOVE_SPEED, 0, 0);
        }

        if(Input.isKeyDown(Input.KEY_S)) {
            camera.movePosition(0, 0, MOVE_SPEED);
        }

        if(Input.isKeyDown(Input.KEY_D)) {
            camera.movePosition(MOVE_SPEED, 0, 0);
        }

        if(Input.isKeyDown(Input.KEY_SPACE)) {
            camera.movePosition(0, MOVE_SPEED, 0);
        }

        if(Input.isKeyDown(Input.KEY_LSHIFT)) {
            camera.movePosition(0, -MOVE_SPEED, 0);
        }
    }

    public void rotateCamera() {
        displVec.y = ((float) currentPos.x - (float) previousPos.x);
        displVec.x = ((float) currentPos.y - (float) previousPos.y);

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

        Vector2f rotVec = displVec;
        camera.moveRotation(rotVec.x * SENSITIVITY, rotVec.y * SENSITIVITY, 0);
    }
}
