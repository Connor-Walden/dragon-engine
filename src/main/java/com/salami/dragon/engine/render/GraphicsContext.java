package com.salami.dragon.engine.render;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.Utils;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.light.DirectionalLight;
import com.salami.dragon.engine.light.PointLight;
import com.salami.dragon.engine.light.SceneLight;
import com.salami.dragon.engine.light.SpotLight;
import com.salami.dragon.engine.log.Logger;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GraphicsContext {

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private static final float SPECULAR_POWER = 10f;

    private static final int MAX_POINT_LIGHTS = 5;

    private static final int MAX_SPOT_LIGHTS = 5;

    Window window;

    private ShaderProgram sceneShaderProgram;
    private ShaderProgram hudShaderProgram;
    Transformation transformation;

    public GraphicsContext(Window window) {
        this.window = window;
    }

    public void init() {
        glfwMakeContextCurrent(window.getGLFWWindow());
    }

    public void prepare() throws Exception {
        transformation = new Transformation();

        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        sceneShaderProgram.link();

        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");
        sceneShaderProgram.createUniform("texture_sampler");

        sceneShaderProgram.createMaterialUniform("material");

        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");

        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");

        hudShaderProgram = new ShaderProgram();

        hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.glsl"));
        hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.glsl"));
        hudShaderProgram.link();

        // Create uniforms for Ortographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("colour");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Entity[] entities, SceneLight sceneLight, IHud hud) {
        clear();

        renderScene(window, camera, entities, sceneLight);

        if(hud != null) renderHud(window, hud);
    }

    public void renderScene(Window window, Camera camera, Entity[] entities, SceneLight sceneLight) {

        sceneShaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        renderLights(viewMatrix, sceneLight);

        sceneShaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for (Entity entity : entities) {
            Mesh mesh = entity.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(entity, viewMatrix);
            sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            sceneShaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

        sceneShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {
        sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", SPECULAR_POWER);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            if (pointLightList[i] != null) {
                PointLight currPointLight = new PointLight(pointLightList[i]);
                Vector3f lightPos = currPointLight.getPosition();
                Vector4f aux = new Vector4f(lightPos, 1);
                aux.mul(viewMatrix);
                lightPos.x = aux.x;
                lightPos.y = aux.y;
                lightPos.z = aux.z;
                sceneShaderProgram.setUniform("pointLights", currPointLight, i);
            }
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = sceneLight.getSpotLightList();

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

            sceneShaderProgram.setUniform("spotLights", currSpotLight.getPointLight(), i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        if (sceneLight.getDirectionalLight() != null) {
            DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
            Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
            dir.mul(viewMatrix);
            currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
            sceneShaderProgram.setUniform("directionalLight", currDirLight);
        }
    }

    private void renderHud(Window window, IHud hud) {
        hudShaderProgram.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (Entity entity : hud.getEntities()) {
            Mesh mesh = entity.getMesh();
            // Set ortohtaphic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrtoProjModelMatrix(entity, ortho);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("colour", entity.getMesh().getMaterial().getAmbientColour());

            // Render the mesh for this HUD item
            mesh.render();
        }

        hudShaderProgram.unbind();
    }

    public void cleanUp() {
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanup();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
    }

    public void swapBuffers(Camera camera, Entity[] entities, SceneLight sceneLight, IHud hud) {
        render(window, camera, entities, sceneLight, hud);

        if(window.getGLFWWindow() != NULL)
            glfwSwapBuffers(window.getGLFWWindow());
    }
}
