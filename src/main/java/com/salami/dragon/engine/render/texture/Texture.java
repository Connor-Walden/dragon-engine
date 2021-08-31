package com.salami.dragon.engine.render.texture;

import com.salami.dragon.engine.log.Logger;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Texture {
    PNGDecoder decoder;
    int textureId;
    int width, height;
    ByteBuffer buffer;

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
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public int getTextureId() {
        if(textureId == NULL)
            Logger.log_error("Texture not yet loaded. Please run 'loadToGL' before using this texture.");

        return textureId;
    }

    public void cleanup() {
        glDeleteTextures(textureId);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
