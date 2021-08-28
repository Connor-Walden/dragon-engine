package com.salami.dragon.engine.render;

import static org.lwjgl.opengl.GL15.*;

public class VBO {
    int id;

    public VBO() {
        this.id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
    }

    public int getVBOID() {
        return id;
    }
}
