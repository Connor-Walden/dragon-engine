package com.salami.dragon.engine.ecs.entity;

import com.salami.dragon.engine.render.mesh.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity {
    private Mesh mesh;

    private final Vector3f position;

    private float scale;

    private final Quaternionf rotation;

    private int textPos;

    public Entity() {
        position = new Vector3f();
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
    }

    public Entity(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public int getTextPos() {
        return textPos;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final void setRotation(Quaternionf q) {
        this.rotation.set(q);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}