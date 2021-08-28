package com.salami.dragon.engine.render;

import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.render.shader.ShaderProgram;
import com.salami.dragon.engine.window.Window;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GraphicsContext {

    Window window;

    ShaderProgram shaderProgram;
    Camera camera;
    Transformation transformation;

    public GraphicsContext(Window window) {
        this.window = window;
    }

    public void init() {
        glfwMakeContextCurrent(window.getGLFWWindow());
    }

    public void prepare() throws Exception {
        shaderProgram = new ShaderProgram();

        shaderProgram.link(
                "C:\\Users\\conno\\OneDrive\\Desktop\\dragon-engine\\src\\main\\java\\com\\salami\\dragon\\engine\\render\\shader\\vertex.glsl",
                "C:\\Users\\conno\\OneDrive\\Desktop\\dragon-engine\\src\\main\\java\\com\\salami\\dragon\\engine\\render\\shader\\fragment.glsl"
        );

        camera = new Camera(window);
        transformation = new Transformation();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("worldMatrix");
    }

    public void render(Entity[] entities) {
        shaderProgram.bind();
        for(Entity entity : entities) {
            glBindVertexArray(entity.getMesh().getVAO().getVAOID());

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            Matrix4f projectionMatrix = transformation.getProjectionMatrix(Camera.FOV, window.getWidth(), window.getHeight(), Camera.Z_NEAR, Camera.Z_FAR);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);

            Matrix4f worldMatrix = transformation.getWorldMatrix(
                entity.getPosition(),
                entity.getRotation(),
                entity.getScale()
            );

            shaderProgram.setUniform("worldMatrix", worldMatrix);

            glDrawElements(GL_TRIANGLES, entity.getMesh().getVertexCount(), GL_UNSIGNED_INT, 0);

            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);

            glBindVertexArray(0);
        }

        shaderProgram.unbind();
    }

    public void cleanUp(Entity[] entities) {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }

        for (Entity entity : entities) {
            entity.getMesh().cleanUp();
        }
    }

    public void swapBuffers(long window, Entity[] entities) {
        render(entities);
        glfwSwapBuffers(window);
    }
}
