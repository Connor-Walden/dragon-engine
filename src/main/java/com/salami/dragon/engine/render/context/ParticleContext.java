package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.World;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.render.ShadowMap;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.particle.IParticleEmitter;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glDepthMask;

public class ParticleContext implements IContext {
    private ShaderProgram particlesShaderProgram;

    public ParticleContext() {}

    @Override
    public void setup() throws Exception {
        particlesShaderProgram = GraphicsContext.setupShaderProgram("/shaders/particle_vertex.glsl", "/shaders/particle_fragment.glsl");
        GraphicsContext.createUniforms(particlesShaderProgram, "projectionMatrix", "modelViewMatrix", "texture_sampler");
    }

    @Override
    public void render(World world, Transformation transformation, ShadowMap shadowMap) {
        particlesShaderProgram.bind();

        particlesShaderProgram.setUniform("texture_sampler", 0);

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        particlesShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix();
        IParticleEmitter[] emitters = world.getParticleEmitters();
        int numEmitters = emitters != null ? emitters.length : 0;

        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        for (int i = 0; i < numEmitters; i++) {
            IParticleEmitter emitter = emitters[i];
            Mesh mesh = emitter.getBaseParticle().getMesh();
            mesh.renderList((emitter.getParticles()), (Entity entity) -> {
                        Matrix4f modelMatrix = transformation.buildModelMatrix(entity);
                        viewMatrix.transpose3x3(modelMatrix);

                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                        modelViewMatrix.scale(entity.getScale());

                        particlesShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                    }
            );
        }


        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);
        particlesShaderProgram.unbind();
    }

    @Override
    public void cleanUp() {
        if (particlesShaderProgram != null) {
            particlesShaderProgram.cleanup();
        }
    }
}
