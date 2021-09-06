package com.salami.dragon.engine.ecs.component;

import com.salami.dragon.engine.ecs.entity.Entity;

public interface IComponent {

    public void init() throws Exception;
    public void tick(float delta);
    public void setParent(Entity entity);
    public Entity getParent();
}
