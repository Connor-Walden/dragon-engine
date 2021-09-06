package com.salami.dragon.engine.ecs.component.prefab;

import com.salami.dragon.engine.audio.AudioListener_;
import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.ecs.entity.Entity;
import org.joml.Vector3f;

public class AudioListener implements IComponent {
    AudioListener_ listener;
    Entity parent;

    public AudioListener(Entity parent) {
        this.parent = parent;
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

    public AudioListener_ getSource() {
        return listener;
    }
}
