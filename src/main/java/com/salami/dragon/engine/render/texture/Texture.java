package com.salami.dragon.engine.render.texture;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {
    PNGDecoder decoder;
    int textureId;
    int width, height;
    ByteBuffer buffer;

    private int numRows = 1;
    private int numCols = 1;

    /**
     * Creates an empty texture.
     *
     * @param width Width of the texture
     * @param height Height of the texture
     * @param pixelFormat Specifies the format of the pixel data (GL_RGBA, etc.)
     * @throws Exception
     */
    public Texture(int width, int height, int pixelFormat) throws Exception {
        this.textureId = glGenTextures();
        this.width = width;
        this.height = height;

        glBindTexture(GL_TEXTURE_2D, this.textureId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, pixelFormat, GL_FLOAT, (ByteBuffer) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public Texture(String fileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

        if(is == null)
            throw new IOException("Could not load texture '" + fileName + "' to stream.");

        decoder = new PNGDecoder(is);

        buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

        width = decoder.getWidth();
        height = decoder.getHeight();

        decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
        buffer.flip();
        loadToGL();
    }

    public Texture(ByteBuffer buffer, int width, int height) throws IOException {
        this.width = width;
        this.height = height;
        this.buffer = buffer;

        loadToGL();
    }

    public void loadToGL() throws IOException {
        // Create a new OpenGL texture
        textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public int getId() {
        return textureId;
    }

    public void cleanup() {
        glDeleteTextures(textureId);
    }
}
