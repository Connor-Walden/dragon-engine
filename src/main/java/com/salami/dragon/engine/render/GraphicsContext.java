package com.salami.dragon.engine.render;

import com.salami.dragon.engine.Application;
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
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_WORK_GROUP_SIZE;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
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
    private ShaderProgram postProcessingShaderProgram;
    private ShaderProgram postProcessingQuadShaderProgram;

    private Window window;

    private static Transformation transformation;

    private Texture ppTex;

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
        setupPostProcessingShader();
    }

    private void setupPostProcessingShader() throws Exception {
        ppTex = new Texture(window.getWidth(), window.getHeight(), GL_TEXTURE_2D);
        int vao = quadFullScreenVao();
        createPrograms();
    }

    private void setupWorldShader() throws Exception {
        worldShaderProgram = setupShaderProgram("/shaders/vertex.glsl", "/shaders/fragment.glsl");
        createUniforms(worldShaderProgram,"projectionMatrix", "modelViewMatrix", "texture_sampler", "specularPower", "ambientLight");

        worldShaderProgram.createMaterialUniform("material");
        worldShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        worldShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        worldShaderProgram.createDirectionalLightUniform("directionalLight");
    }

    private void setupHUDShader() throws Exception {
        hudShaderProgram = setupShaderProgram("/shaders/hud_vertex.glsl", "/shaders/hud_fragment.glsl");
        createUniforms(hudShaderProgram, "projModelMatrix", "colour", "hasTexture");
    }

    private void setupSkyboxShader() throws Exception {
        skyBoxShaderProgram = setupShaderProgram("/shaders/sb_vertex.glsl", "/shaders/sb_fragment.glsl");
        createUniforms(skyBoxShaderProgram, "projectionMatrix", "modelViewMatrix", "texture_sampler", "ambientLight");
    }

    public void clear() {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT );
    }

    public void render(Window window, Camera camera, World world) throws Exception {
        clear();

        // Update projection and view atrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderWorld(world);
        renderSkyBox(world);
    }

    public void renderWorld(World world) {
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
            if(mesh != null) {
                worldShaderProgram.setUniform("material", mesh.getMaterial());
                mesh.renderList(mapMeshes.get(mesh), (Entity entity) -> {
                    Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(entity, viewMatrix);
                    worldShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                });
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

    private ShaderProgram setupShaderProgram(String computeLocation) throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createComputeShader(Utils.loadResource(computeLocation));
        shaderProgram.link();

        return shaderProgram;
    }

    /**
     * Creates a VAO with a full-screen quad VBO.
     */
    private int quadFullScreenVao() {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        ByteBuffer bb = BufferUtils.createByteBuffer(2 * 6);
        bb.put((byte) -1).put((byte) -1);
        bb.put((byte) 1).put((byte) -1);
        bb.put((byte) 1).put((byte) 1);
        bb.put((byte) 1).put((byte) 1);
        bb.put((byte) -1).put((byte) 1);
        bb.put((byte) -1).put((byte) -1);
        bb.flip();
        glBufferData(GL_ARRAY_BUFFER, bb, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_BYTE, false, 0, 0L);
        glBindVertexArray(0);
        return vao;
    }

    /**
     * Create the texture that will serve as our framebuffer.
     *
     * @return the texture id
     */
    private int createFramebufferTexture() {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        ByteBuffer black = null;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, black);
        glBindTexture(GL_TEXTURE_2D, 0);
        return tex;
    }

    /**
     * Create the tracing compute shader program.
     *
     * @throws IOException
     */
    private void createPrograms() throws Exception {
        postProcessingShaderProgram = setupShaderProgram("/shaders/pp_compute.glsl");

        createUniforms(postProcessingShaderProgram, "eye", "ray00", "ray10", "ray01", "ray11");

        int linked = glGetProgrami(postProcessingShaderProgram.getProgramId(), GL_LINK_STATUS);
        String programLog = glGetProgramInfoLog(postProcessingShaderProgram.getProgramId());
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linked == 0) {
            throw new AssertionError("Could not link compute program");
        }

        postProcessingQuadShaderProgram = setupShaderProgram("/shaders/pp_quad_vertex.glsl", "/shaders/pp_quad_fragment.glsl");
        createUniforms(postProcessingQuadShaderProgram, "tex");

        linked = glGetProgrami(postProcessingQuadShaderProgram.getProgramId(), GL_LINK_STATUS);
        programLog = glGetProgramInfoLog(postProcessingQuadShaderProgram.getProgramId());
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linked == 0) {
            throw new AssertionError("Could not link quad program");
        }
    }

    /**
     * Compute one frame by tracing the scene using our compute shader and
     * presenting that image on the screen.
     */
    private void trace() {
        postProcessingShaderProgram.bind();

        /* Set viewing frustum corner rays in shader */

        postProcessingShaderProgram.setUniform("eye", window.getCamera().getPosition());

        Vector3f eyeRay = window.getCamera().getEyeRay(-1, -1);
        postProcessingShaderProgram.setUniform("ray00", eyeRay);

        eyeRay = window.getCamera().getEyeRay(-1, 1);
        postProcessingShaderProgram.setUniform("ray01", eyeRay);

        eyeRay = window.getCamera().getEyeRay(1, -1);
        postProcessingShaderProgram.setUniform("ray10", eyeRay);

        eyeRay = window.getCamera().getEyeRay(1, 1);
        postProcessingShaderProgram.setUniform("ray11", eyeRay);

        /* Bind level 0 of framebuffer texture as writable image in the shader. */
        glBindImageTexture(0, ppTex.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);

        /* Compute appropriate invocation dimension. */
        int worksizeX = Utils.nextPowerOfTwo(window.getWidth());
        int worksizeY = Utils.nextPowerOfTwo(window.getHeight());

        /* Invoke the compute shader. */
        IntBuffer workGroupSize = BufferUtils.createIntBuffer(3);
        workGroupSize.put(glGetProgrami(postProcessingShaderProgram.getProgramId(), GL_COMPUTE_WORK_GROUP_SIZE));
        int workGroupSizeX = workGroupSize.get(0);
        int workGroupSizeY = workGroupSize.get(1);

        glDispatchCompute(worksizeX / workGroupSizeX, worksizeY / workGroupSizeY, 1);

        /* Reset image binding. */
        glBindImageTexture(0, 0, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        glUseProgram(0);

        /*
         * Draw the rendered image on the screen using textured full-screen
         * quad.
         */
        glUseProgram(postProcessingQuadShaderProgram.getProgramId());
        glBindTexture(GL_TEXTURE_2D, ppTex.getId());
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindVertexArray(0);
        postProcessingShaderProgram.unbind();
    }
}
