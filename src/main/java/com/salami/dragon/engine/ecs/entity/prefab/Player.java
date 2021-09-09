package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.component.Components;
import com.salami.dragon.engine.ecs.component.prefab.AudioListener;
import com.salami.dragon.engine.ecs.component.prefab.FirstPersonCamera;
import com.salami.dragon.engine.ecs.entity.Entity;

public class Player extends Entity {
    Camera camera;

    public Player(Camera camera) {
        this.camera = camera;

        addComponent(Components.FIRST_PERSON_CAMERA, new FirstPersonCamera(this.camera, false, this));
        addComponent(Components.AUDIO_LISTENER, new AudioListener(this, camera));
    }

    public Camera getCamera() {
        return camera;
    }

    public void suspendControls() {
        FirstPersonCamera fpc = (FirstPersonCamera) getComponent(Components.FIRST_PERSON_CAMERA);
        fpc.setDoControls(false);
    }

    public void reinstateControls() {
        FirstPersonCamera fpc = (FirstPersonCamera) getComponent(Components.FIRST_PERSON_CAMERA);
        fpc.setDoControls(true);
    }
}
