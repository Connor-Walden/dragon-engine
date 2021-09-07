package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.Utils;
import com.salami.dragon.engine.World;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.render.ShadowMap;
import com.salami.dragon.engine.render.Window;
import com.salami.dragon.engine.render.shader.ShaderProgram;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GraphicsContext {
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000.0f;
    public static final float SPECULAR_POWER = 10f;

    public static final int MAX_POINT_LIGHTS = 5;
    public static final int MAX_SPOT_LIGHTS = 5;

    private IContext worldContext, hudContext, skyBoxContext, depthContext, particleContext;

    private final Window window;

    private ShadowMap shadowMap;

    private static Transformation transformation;

    public GraphicsContext(Window window) {
        this.window = window;
    }

    public void init() {
        glfwMakeContextCurrent(window.getGLFWWindow());
    }

    public void prepare() throws Exception {
        // Setup dependant info for context rendering
        transformation = new Transformation();
        shadowMap = new ShadowMap();

        // Create all contexts
        worldContext = new WorldContext();
        hudContext = new HUDContext();
        skyBoxContext = new SkyBoxContext();
        depthContext = new DepthContext();
        particleContext = new ParticleContext();

        // Setup all contexts
        depthContext.setup();
        skyBoxContext.setup();
        worldContext.setup();
        hudContext.setup();
        particleContext.setup();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, World world) throws Exception {
        clear();

        // Render depth map before view ports has been set up
        depthContext.render(world, transformation, shadowMap);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        worldContext.render(world, transformation, shadowMap);
        skyBoxContext.render(world, transformation, shadowMap);
        particleContext.render(world, transformation, shadowMap);
        // hudContext.render(world, transformation, shadowMap);
    }

    public void cleanUp() {
        if (shadowMap != null) {
            shadowMap.cleanup();
        }

        depthContext.cleanUp();
        skyBoxContext.cleanUp();
        worldContext.cleanUp();
        hudContext.cleanUp();
        particleContext.cleanUp();
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
    public static void createUniforms(ShaderProgram sp, String... uniforms) throws Exception {
        for(String uniform : uniforms) {
            sp.createUniform(uniform);
        }
    }

    // sets up the shader to be used by this graphics context
    public static ShaderProgram setupShaderProgram(String vertexLocation, String fragmentLocation) throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource(vertexLocation));
        shaderProgram.createFragmentShader(Utils.loadResource(fragmentLocation));
        shaderProgram.link();

        return shaderProgram;
    }
}
