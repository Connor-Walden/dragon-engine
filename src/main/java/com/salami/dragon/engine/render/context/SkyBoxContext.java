package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.World;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.ecs.entity.prefab.SkyBox;
import com.salami.dragon.engine.render.ShadowMap;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import org.joml.Matrix4f;

public class SkyBoxContext implements IContext {
    private ShaderProgram skyBoxShaderProgram;

    public SkyBoxContext() {}

    @Override
    public void setup() throws Exception {
        skyBoxShaderProgram = GraphicsContext.setupShaderProgram("/shaders/sb_vertex.glsl", "/shaders/sb_fragment.glsl");
        GraphicsContext.createUniforms(skyBoxShaderProgram, "projectionMatrix", "modelViewMatrix", "texture_sampler", "ambientLight");
    }

    @Override
    public void render(World world, Transformation transformation, ShadowMap shadowMap) {
        skyBoxShaderProgram.bind();

        skyBoxShaderProgram.setUniform("texture_sampler", 0);

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        SkyBox skyBox = world.getSkyBox();
        Matrix4f viewMatrix = transformation.getViewMatrix();
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        skyBoxShaderProgram.setUniform("ambientLight", world.getWorldLight().getSkyBoxLight());

        world.getSkyBox().getMesh().render();

        skyBoxShaderProgram.unbind();
    }

    @Override
    public void cleanUp() {
        if (skyBoxShaderProgram != null) {
            skyBoxShaderProgram.cleanup();
        }
    }
}
