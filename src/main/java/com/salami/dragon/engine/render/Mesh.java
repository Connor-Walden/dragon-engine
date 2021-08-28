package com.salami.dragon.engine.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class Mesh {
    private final VAO vao;
    private final VBO vbo;
    private final EAO eao;
    private final int vertexCount;
    FloatBuffer vertexBuffer;
    IntBuffer indexBuffer;

    public Mesh(float[] vertices, int[] indices) {
        vertexCount = indices.length;

        vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        vao = new VAO();
        vbo = new VBO();

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        eao = new EAO();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDeleteBuffers(vbo.getVBOID());
        glDeleteBuffers(eao.getEAOID());

        glBindVertexArray(0);
        glDeleteVertexArrays(vao.getVAOID());
    }

    public VAO getVAO() {
        return vao;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
