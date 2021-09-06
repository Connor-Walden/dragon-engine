package com.salami.dragon.engine.ecs.component.prefab;

import com.salami.dragon.engine.audio.AudioListener_;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.GraphicsContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class AudioListener implements IComponent {
    AudioListener_ listener;
    Entity parent;
    Camera camera;

    public AudioListener(Entity parent, Camera camera) {
        this.parent = parent;
        this.camera = camera;
    }

    @Override
    public void init() {
        listener = new AudioListener_(
                new Vector3f(0, 0, 0)
        );
    }

    @Override
    public void tick(float delta) {
        setPosition(parent.getPosition());
        setRotation();
    }

    @Override
    public void setParent(Entity entity) {
        this.parent = entity;
    }

    @Override
    public Entity getParent() {
        return parent;
    }

    public void setPosition(Vector3f position) {
        listener.setPosition(position);
    }

    public void setRotation() {
        // Update camera matrix with camera data
        Matrix4f cameraMatrix = GraphicsContext.getTransformation().updateViewMatrix(camera);

        listener.setPosition(camera.getPosition());
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        listener.setOrientation(at, up);
    }

    public AudioListener_ getSource() {
        return listener;
    }
}
