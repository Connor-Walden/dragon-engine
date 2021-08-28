package com.salami.dragon.engine.render;

import com.salami.dragon.engine.render.shader.ShaderProgram;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GraphicsContext {

    long window;

    ShaderProgram shaderProgram;
    Mesh mesh;

    public GraphicsContext(long window) {
        this.window = window;
    }

    public void init() {
        glfwMakeContextCurrent(window);
    }

    public void prepare(Mesh mesh) throws Exception {
        shaderProgram = new ShaderProgram();

        shaderProgram.link(
                "C:\\Users\\conno\\OneDrive\\Desktop\\dragon-engine\\src\\main\\java\\com\\salami\\dragon\\engine\\render\\shader\\vertex.glsl",
                "C:\\Users\\conno\\OneDrive\\Desktop\\dragon-engine\\src\\main\\java\\com\\salami\\dragon\\engine\\render\\shader\\fragment.glsl"
        );

        this.mesh = mesh;
    }

    public void render() {
        shaderProgram.bind();
        glBindVertexArray(mesh.getVAO().getVAOID());
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }

        mesh.cleanUp();
    }

    public void swapBuffers(long window) {
        render();
        glfwSwapBuffers(window);
    }
}
