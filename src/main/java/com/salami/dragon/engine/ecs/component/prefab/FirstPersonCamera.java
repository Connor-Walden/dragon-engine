package com.salami.dragon.engine.ecs.component.prefab;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.event.Event;
import com.salami.dragon.engine.event.EventType;
import com.salami.dragon.engine.event.IListener;
import com.salami.dragon.engine.input.Input;
import com.salami.dragon.engine.light.PointLight;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FirstPersonCamera implements IComponent, IListener {
    public static final float MOVE_SPEED = 0.025f;
    public static final float SENSITIVITY = 0.1f;

    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private final Vector2f displVec;

    Camera camera;

    boolean doControls = true;

    Entity parent;
    boolean glow = false;

    PointLight playerLight = new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), 1.0f);

    public FirstPersonCamera(Camera camera, boolean glow, Entity parent) {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();

        this.camera = camera;
        this.glow = glow;
        this.parent = parent;
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

    public Camera getCamera() {
        return camera;
    }

    public void setDoControls(boolean doControls) {
        this.doControls = doControls;
    }

    @Override
    public void init() {
        Application.getEventGovernor().registerListeners(this, EventType.MOUSE_MOVE);

        if(glow) {
            PointLight[] pointLightList = Application.getWorld().getWorldLight().getPointLightList();

            if(pointLightList != null) {
                PointLight[] newPointLightList = new PointLight[pointLightList.length + 1];

                for(int i = 0; i < pointLightList.length; i++)
                    newPointLightList[i] = pointLightList[i];

                newPointLightList[pointLightList.length] = playerLight;

                Application.getWorld().getWorldLight().setPointLightList(newPointLightList);
            } else {
                Application.getWorld().getWorldLight().setPointLightList(new PointLight[] { playerLight });
            }
        }
    }

    @Override
    public void tick(float delta) {
        if (doControls) {
            movePlayer();
            rotateCamera();

            parent.setPosition(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
            playerLight.setPosition(parent.getPosition());
        }
    }

    @Override
    public void setParent(Entity entity) {
        parent = entity;
    }

    @Override
    public Entity getParent() {
        return parent;
    }

    @Override
    public void onEngineEvent(Event event) {}

    @Override
    public void onKeyOrMouseButtonEvent(Event event, int key) {}

    @Override
    public void onMouseMoveEvent(Event event, double xPos, double yPos) {
        updateMousePos(xPos, yPos);
    }

    @Override
    public void onMouseScrollEvent(Event event, double amount) {}

    @Override
    public void onAudioEvent(Event event) {}

    @Override
    public void onComponentEvent(Event event) {

    }
}
