package com.salami.dragon.engine.render;

import static org.lwjgl.opengl.GL15.*;

public class EAO {
    int id;

    public EAO() {
        this.id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id);
    }

    public int getEAOID() {
        return id;
    }
}
