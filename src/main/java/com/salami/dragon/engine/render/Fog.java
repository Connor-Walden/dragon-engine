package com.salami.dragon.engine.render;

import org.joml.Vector3f;

public class Fog {
    private boolean active;
    private Vector3f colour;
    private float density;
    private float distance;

    public static Fog NOFOG = new Fog();

    public Fog() {
        active = false;
        this.colour = new Vector3f(0, 0, 0);
        this.density = 0;
        this.distance = 1;
    }

    public Fog(boolean active, Vector3f colour, float density, float distance) {
        this.colour = colour;
        this.density = density;
        this.active = active;
        this.distance = distance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}