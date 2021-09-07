package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.World;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.render.ShadowMap;
import com.salami.dragon.engine.render.shader.ShaderProgram;

public class HUDContext implements IContext {
    private ShaderProgram hudShaderProgram;

    public HUDContext() {}

    @Override
    public void setup() throws Exception {
        hudShaderProgram = GraphicsContext.setupShaderProgram("/shaders/hud_vertex.glsl", "/shaders/hud_fragment.glsl");
        GraphicsContext.createUniforms(hudShaderProgram, "projModelMatrix", "colour", "hasTexture");
    }

    @Override
    public void render(World world, Transformation transformation, ShadowMap shadowMap) {

    }

    @Override
    public void cleanUp() {
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
    }
}
