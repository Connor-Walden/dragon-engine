package com.salami.dragon.engine.render;

import com.salami.dragon.engine.ecs.entity.Entity;

public interface IHud {

    Entity[] getEntities();

    default void cleanup() {
        Entity[] entities = getEntities();

        for (Entity entity : entities) {
            entity.getMesh().cleanUp();
        }
    }
}