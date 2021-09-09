package com.salami.dragon.engine.ecs.entity;

import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.render.mesh.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class Entity {

    private Map<String, IComponent> componentList;

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

        componentList = new HashMap<>();
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

    public void move(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
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

    public Entity setScale(float scale) {
        this.scale = scale;

        return this;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final Entity setRotation(Quaternionf q) {
        this.rotation.set(q);

        return this;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Entity setMesh(Mesh mesh) {
        this.mesh = mesh;

        return this;
    }

    public Entity addComponent(String componentName, IComponent component) {
        componentList.put(componentName, component);

        return this;
    }

    public Entity cleanUp() {
        clearComponents();

        return this;
    }

    public Entity removeComponent(String componentName) {
        componentList.remove(componentName);

        return this;
    }

    public IComponent getComponent(String componentName) {
        return componentList.get(componentName);
    }

    public List<IComponent> getComponents() {
        return componentList.values().stream().toList();
    }

    public Entity clearComponents() {
        componentList = new HashMap<>();

        return this;
    }
}