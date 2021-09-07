package com.salami.dragon.engine.render.particle;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.prefab.Particle;

import java.util.List;

public interface IParticleEmitter {
    void cleanup();
    Particle getBaseParticle();
    List<Entity> getParticles();
}
