package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.component.prefab.AudioListener;
import com.salami.dragon.engine.ecs.component.prefab.FirstPersonCamera;
import com.salami.dragon.engine.ecs.entity.Entity;

public class Player extends Entity {
    Camera camera;

    public Player(Camera camera) {
        this.camera = camera;

        addComponent("FirstPersonCamera", new FirstPersonCamera(this.camera, false, this));
        addComponent("AudioListener", new AudioListener(this));
    }

    public Camera getCamera() {
        return camera;
    }
}
