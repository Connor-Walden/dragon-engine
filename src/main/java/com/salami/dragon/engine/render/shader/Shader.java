package com.salami.dragon.engine.render.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public String vertexPath, fragmentPath;

    private final int vertexId, fragmentId, programId;

    public Shader(int programId, String vertexPath, String fragmentPath) throws Exception {
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;
        this.programId = programId;

        StringBuilder vertexCode = new StringBuilder();
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(vertexPath));
            String buffer = "";
            while((buffer = bufferedReader.readLine()) != null)
                vertexCode.append(buffer).append("\n");
            bufferedReader.close();
        }catch (IOException e1){
            e1.printStackTrace();
        }

        StringBuilder fragmentCode = new StringBuilder();
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fragmentPath));
            String buffer = "";
            while((buffer = bufferedReader.readLine()) != null)
                fragmentCode.append(buffer).append("\n");
            bufferedReader.close();
        }catch (IOException e1){
            e1.printStackTrace();
        }

        vertexId = createShader(vertexCode.toString(), GL_VERTEX_SHADER);
        fragmentId = createShader(fragmentCode.toString(), GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
        glAttachShader(programId, shaderId);
        return shaderId;
    }

    public int getVertexShaderId() {
        return vertexId;
    }

    public int getFragmentShaderId() {
        return fragmentId;
    }
}
