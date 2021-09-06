package com.salami.dragon.engine.camera;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.render.GraphicsContext;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {
    private final Vector3f position;

    private final Vector3f rotation;

    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f();
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    /**
     * Compute the world direction vector based on the given X and Y coordinates
     * in normalized-device space.
     *
     * @param x
     *            the X coordinate within [-1..1]
     * @param y
     *            the Y coordinate within [-1..1]
     */
    public Vector3f getEyeRay(float x, float y) {
        Vector4f tmp = new Vector4f(x, y, 0.0f, 1.0f);
        GraphicsContext.getTransformation().getProjectionMatrix().transform(tmp);
        tmp.mul(1.0f / tmp.w);
        Vector3f tmp2 = new Vector3f(tmp.x, tmp.y, tmp.z);
        tmp2.sub(position);

        return tmp2;
    }
}
