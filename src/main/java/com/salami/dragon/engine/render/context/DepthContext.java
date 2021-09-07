package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.World;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.light.DirectionalLight;
import com.salami.dragon.engine.render.ShadowMap;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class DepthContext implements IContext {
    private ShaderProgram depthShaderProgram;

    public DepthContext() {}

    @Override
    public void setup() throws Exception {
        depthShaderProgram = GraphicsContext.setupShaderProgram("/shaders/depth_vertex.glsl", "/shaders/depth_fragment.glsl");
        GraphicsContext.createUniforms(depthShaderProgram, "orthoProjectionMatrix", "modelLightViewMatrix");
    }

    @Override
    public void render(World world, Transformation transformation, ShadowMap shadowMap) {
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = world.getWorldLight().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Map<Mesh, List<Entity>> mapMeshes = world.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            if(mesh != null) {
                mesh.renderList(mapMeshes.get(mesh), (Entity entity) -> {
                            Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(entity, lightViewMatrix);
                            depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                        }
                );
            }
        }

        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void cleanUp() {
        if (depthShaderProgram != null) {
            depthShaderProgram.cleanup();
        }
    }
}
