package com.salami.dragon.engine.ecs.component.prefab;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.audio.AudioSource_;
import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.ecs.entity.Entity;

public class AudioSource implements IComponent {
    Entity parent;
    AudioSource_ source;
    String filePath;
    boolean loop, relative;

    public AudioSource(Entity parent, String filePath, boolean loop, boolean relative) {
        this.parent = parent;
        this.filePath = filePath;
        this.loop = loop;
        this.relative = relative;
    }

    @Override
    public void init() throws Exception {
        source = Application.createAudioSource(filePath, loop, relative);
    }

    @Override
    public void tick(float delta) {
        source.setPosition(parent.getPosition());
    }

    public void setGain(float gain) {
        source.setGain(gain);
    }

    public AudioSource_ getSource() {
        return source;
    }

    @Override
    public void setParent(Entity entity) {
        parent = entity;
    }

    @Override
    public Entity getParent() {
        return parent;
    }
}
