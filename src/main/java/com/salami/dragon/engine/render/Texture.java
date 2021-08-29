package com.salami.dragon.engine.render;

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

    public Texture(String fileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

        if(is == null)
            throw new IOException("Could not load texture '" + fileName + "' to stream.");

        decoder = new PNGDecoder(is);
    }

    public void loadToGL() throws IOException {
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

        buf.flip();

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

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
}
