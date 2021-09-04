package com.salami.dragon.engine.render;

import com.salami.dragon.engine.Utils;
import com.salami.dragon.engine.World;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.ecs.entity.prefab.SkyBox;
import com.salami.dragon.engine.light.DirectionalLight;
import com.salami.dragon.engine.light.PointLight;
import com.salami.dragon.engine.light.WorldLight;
import com.salami.dragon.engine.light.SpotLight;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GraphicsContext {

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.0f;

    private static final float SPECULAR_POWER = 10f;

    private static final int MAX_POINT_LIGHTS = 5;

    private static final int MAX_SPOT_LIGHTS = 5;

    Window window;

    private ShaderProgram worldShaderProgram;
    private ShaderProgram hudShaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    static Transformation transformation;

    public GraphicsContext(Window window) {
        this.window = window;
    }

    public void init() {
        glfwMakeContextCurrent(window.getGLFWWindow());
    }

    public void prepare() throws Exception {
        transformation = new Transformation();

        setupSkyboxShader();
        setupWorldShader();
        setupHUDShader();
    }

    private void setupWorldShader() throws Exception {
        worldShaderProgram = new ShaderProgram();
        worldShaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        worldShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        worldShaderProgram.link();

        worldShaderProgram.createUniform("projectionMatrix");
        worldShaderProgram.createUniform("modelViewMatrix");
        worldShaderProgram.createUniform("texture_sampler");

        worldShaderProgram.createMaterialUniform("material");

        worldShaderProgram.createUniform("specularPower");
        worldShaderProgram.createUniform("ambientLight");

        worldShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        worldShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        worldShaderProgram.createDirectionalLightUniform("directionalLight");
    }

    private void setupHUDShader() throws Exception {
        hudShaderProgram = new ShaderProgram();

        hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.glsl"));
        hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.glsl"));
        hudShaderProgram.link();

        // Create uniforms for Ortographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("colour");
        hudShaderProgram.createUniform("hasTexture");
    }

    private void setupSkyboxShader() throws Exception {
        skyBoxShaderProgram = new ShaderProgram();

        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sb_vertex.glsl"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sb_fragment.glsl"));
        skyBoxShaderProgram.link();

        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }

    public void clear() {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT );
    }

    public void render(Window window, Camera camera, World world) {
        clear();

        // Update projection and view atrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderWorld(window, camera, world);

        if(world.getSkyBox() != null) renderSkyBox(window, camera, world);
    }

    public void renderWorld(Window window, Camera camera, World world) {
        worldShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        worldShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix();

        WorldLight sceneLight = world.getWorldLight();
        renderLights(viewMatrix, sceneLight);

        worldShaderProgram.setUniform("texture_sampler", 0);
        // Render each mesh with the associated game Items
        Map<Mesh, List<Entity>> mapMeshes = world.getGameMeshes();

        for (Mesh mesh : mapMeshes.keySet()) {
            worldShaderProgram.setUniform("material", mesh.getMaterial());
            mesh.renderList(mapMeshes.get(mesh), (Entity entity) -> {
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(entity, viewMatrix);
                worldShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            });
        }


        worldShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, WorldLight worldLight) {
        worldShaderProgram.setUniform("ambientLight", worldLight.getAmbientLight());
        worldShaderProgram.setUniform("specularPower", SPECULAR_POWER);

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

    private void renderSkyBox(Window window, Camera camera, World world) {
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


    public void cleanUp() {
        if (skyBoxShaderProgram != null) {
            skyBoxShaderProgram.cleanup();
        }
        if (worldShaderProgram != null) {
            worldShaderProgram.cleanup();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
    }

    public void swapBuffers(Camera camera, World world) {
        render(window, camera, world);

        if(window.getGLFWWindow() != NULL)
            glfwSwapBuffers(window.getGLFWWindow());
    }

    public static Transformation getTransformation() {
        return transformation;
    }
}
