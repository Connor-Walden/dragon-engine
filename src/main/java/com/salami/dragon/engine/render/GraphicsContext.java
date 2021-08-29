package com.salami.dragon.engine.render;

import com.salami.dragon.engine.Utils;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import com.salami.dragon.engine.window.Window;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GraphicsContext {

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    Window window;

    ShaderProgram shaderProgram;
    Transformation transformation;

    public GraphicsContext(Window window) {
        this.window = window;
    }

    public void init() {
        glfwMakeContextCurrent(window.getGLFWWindow());
    }

    public void prepare() throws Exception {
        // Create shader
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shaderProgram.link();

        transformation = new Transformation();

        // Create uniforms for modelView and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Entity[] entities) {
        clear();

        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for (Entity entity : entities) {
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(entity, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mes for this game item
            entity.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

    public void swapBuffers(Camera camera, Entity[] entities) {
        render(window, camera, entities);

        if(window.getGLFWWindow() != NULL)
            glfwSwapBuffers(window.getGLFWWindow());
    }
}
