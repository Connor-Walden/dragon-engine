package com.salami.dragon.engine.render;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VAO {
    int id;

    public VAO() {
        this.id = glGenVertexArrays();
        glBindVertexArray(this.id);
    }

    public int getVAOID() {
        return id;
    }
}
