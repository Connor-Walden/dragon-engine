package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.World;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.render.ShadowMap;

public interface IContext {
    public void setup() throws Exception;
    public void render(World world, Transformation transformation, ShadowMap shadowMap);
    public void cleanUp();
}
