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
import com.salami.dragon.engine.render.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GraphicsContext {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private static final float SPECULAR_POWER = 10f;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private ShaderProgram worldShaderProgram;
    private ShaderProgram hudShaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram depthShaderProgram;

    private Window window;

    private ShadowMap shadowMap;

    private static Transformation transformation;

    public GraphicsContext(Window window) {
        this.window = window;
    }

    public void init() {
        glfwMakeContextCurrent(window.getGLFWWindow());
    }

    public void prepare() throws Exception {
        transformation = new Transformation();
        shadowMap = new ShadowMap();

        setupDepthShader();
        setupSkyboxShader();
        setupWorldShader();
        setupHUDShader();
    }

    private void setupSkyboxShader() throws Exception {
        skyBoxShaderProgram = setupShaderProgram("/shaders/sb_vertex.glsl", "/shaders/sb_fragment.glsl");
        createUniforms(skyBoxShaderProgram, "projectionMatrix", "modelViewMatrix", "texture_sampler", "ambientLight");
    }

    private void setupWorldShader() throws Exception {
        worldShaderProgram = setupShaderProgram("/shaders/vertex.glsl", "/shaders/fragment.glsl");
        createUniforms(worldShaderProgram,"projectionMatrix", "modelViewMatrix", "texture_sampler", "specularPower", "ambientLight", "normalMap", "shadowMap", "orthoProjectionMatrix", "modelLightViewMatrix");

        worldShaderProgram.createMaterialUniform("material");
        worldShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        worldShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        worldShaderProgram.createDirectionalLightUniform("directionalLight");
        worldShaderProgram.createFogUniform("fog");
    }

    private void setupHUDShader() throws Exception {
        hudShaderProgram = setupShaderProgram("/shaders/hud_vertex.glsl", "/shaders/hud_fragment.glsl");
        createUniforms(hudShaderProgram, "projModelMatrix", "colour", "hasTexture");
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = setupShaderProgram("/shaders/depth_vertex.glsl", "/shaders/depth_fragment.glsl");
        createUniforms(depthShaderProgram, "orthoProjectionMatrix", "modelLightViewMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, World world) throws Exception {
        clear();

        // Render depth map before view ports has been set up
        renderDepthMap(window, camera, world);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection and view atrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderWorld(world);
        renderSkyBox(world);
    }

    private void renderDepthMap(Window window, Camera camera, World world) {
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

    public void renderWorld(World world) {
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

    private void renderSkyBox(World world) {
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
        if (shadowMap != null) {
            shadowMap.cleanup();
        }
        if (depthShaderProgram != null) {
            depthShaderProgram.cleanup();
        }
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

    public void swapBuffers(Camera camera, World world) throws Exception {
        render(window, camera, world);

        if(window.getGLFWWindow() != NULL)
            glfwSwapBuffers(window.getGLFWWindow());
    }

    public static Transformation getTransformation() {
        return transformation;
    }

    // Create all of the uniforms for a shader program in one call!
    private void createUniforms(ShaderProgram sp, String... uniforms) throws Exception {
        for(String uniform : uniforms) {
            sp.createUniform(uniform);
        }
    }

    // sets up the shader to be used by this graphics context
    private ShaderProgram setupShaderProgram(String vertexLocation, String fragmentLocation) throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource(vertexLocation));
        shaderProgram.createFragmentShader(Utils.loadResource(fragmentLocation));
        shaderProgram.link();

        return shaderProgram;
    }
}
