package com.salami.dragon.engine.render.shader;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int programId;

    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        uniforms = new HashMap<>();

        programId = glCreateProgram();

        if (programId == 0)
            throw new Exception("Could not create Shader");
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);

        if (uniformLocation < 0)
            throw new Exception("Could not find uniform:" + uniformName);

        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }


    public void link(String vertexPath, String fragmentPath) throws Exception {
        Shader shader = new Shader(programId, vertexPath, fragmentPath);

        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
        if (shader.getVertexShaderId() != 0) {
            glDetachShader(programId, shader.getVertexShaderId());
        }
        if (shader.getFragmentShaderId() != 0) {
            glDetachShader(programId, shader.getFragmentShaderId());
        }
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public int getProgramId() {
        return programId;
    }
}
