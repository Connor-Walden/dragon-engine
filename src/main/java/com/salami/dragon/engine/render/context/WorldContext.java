package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.World;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.light.DirectionalLight;
import com.salami.dragon.engine.light.PointLight;
import com.salami.dragon.engine.light.SpotLight;
import com.salami.dragon.engine.light.WorldLight;
import com.salami.dragon.engine.render.ShadowMap;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class WorldContext implements IContext {
    private ShaderProgram worldShaderProgram;

    public WorldContext() {}

    @Override
    public void setup() throws Exception {
        worldShaderProgram = GraphicsContext.setupShaderProgram("/shaders/vertex.glsl", "/shaders/fragment.glsl");
        GraphicsContext.createUniforms(worldShaderProgram,"projectionMatrix", "modelViewMatrix", "texture_sampler", "specularPower", "ambientLight", "normalMap", "shadowMap", "orthoProjectionMatrix", "modelLightViewMatrix");

        worldShaderProgram.createMaterialUniform("material");
        worldShaderProgram.createPointLightListUniform("pointLights", GraphicsContext.MAX_POINT_LIGHTS);
        worldShaderProgram.createSpotLightListUniform("spotLights", GraphicsContext.MAX_SPOT_LIGHTS);
        worldShaderProgram.createDirectionalLightUniform("directionalLight");
        worldShaderProgram.createFogUniform("fog");
    }

    @Override
    public void render(World world, Transformation transformation, ShadowMap shadowMap) {
        worldShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        worldShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        worldShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        Matrix4f viewMatrix = transformation.getViewMatrix();

        WorldLight worldLight = world.getWorldLight();
        renderLights(viewMatrix, worldLight);

        worldShaderProgram.setUniform("fog", world.getFog());
        worldShaderProgram.setUniform("texture_sampler", 0);
        worldShaderProgram.setUniform("normalMap", 1);
        worldShaderProgram.setUniform("shadowMap", 2);

        // Render each mesh with the associated game Items
        Map<Mesh, List<Entity>> mapMeshes = world.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            if(mesh != null) {
                worldShaderProgram.setUniform("material", mesh.getMaterial());
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
                mesh.renderList(mapMeshes.get(mesh), (Entity entity) -> {
                            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(entity, viewMatrix);
                            worldShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                            Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(entity, lightViewMatrix);
                            worldShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                        }
                );
            }
        }

        worldShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, WorldLight worldLight) {
        worldShaderProgram.setUniform("ambientLight", worldLight.getAmbientLight());
        worldShaderProgram.setUniform("specularPower", GraphicsContext.SPECULAR_POWER);

        // Process Point Lights
        PointLight[] pointLightList = worldLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            worldShaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = worldLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            worldShaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(worldLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        worldShaderProgram.setUniform("directionalLight", currDirLight);
    }

    @Override
    public void cleanUp() {
        if (worldShaderProgram != null) {
            worldShaderProgram.cleanup();
        }
    }
}
