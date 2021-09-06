package com.salami.dragon.engine.audio;

import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.log.Logger;
import com.salami.dragon.engine.render.GraphicsContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Audio {
    private long device;

    private long context;

    private AudioListener_ listener;

    private final List<AudioBuffer> audioBufferList;

    private final Map<String, AudioSource_> audioSourceMap;

    public Audio() {
        audioBufferList = new ArrayList<>();
        audioSourceMap = new HashMap<>();
    }

    public void init() throws Exception {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public void addAudioSource(String name, AudioSource_ audioSource) {
        this.audioSourceMap.put(name, audioSource);
    }

    public AudioSource_ getAudioSource(String name) {
        return this.audioSourceMap.get(name);
    }

    public void playAudioSource(String name) {
        AudioSource_ audioSource = this.audioSourceMap.get(name);
        if (audioSource != null && !audioSource.isPlaying()) {
            audioSource.play();
        }
    }

    public void removeAudioSource(String name) {
        this.audioSourceMap.remove(name);
    }

    public void addAudioBuffer(AudioBuffer audioBuffer) {
        this.audioBufferList.add(audioBuffer);
    }

    public AudioListener_ getListener() {
        return this.listener;
    }

    public void setListener(AudioListener_ listener) {
        this.listener = listener;
    }

    public void setAttenuationModel(int model) {
        alDistanceModel(model);
    }

    public void cleanup() {
        for (AudioSource_ audioSource : audioSourceMap.values()) {
            audioSource.cleanup();
        }
        audioSourceMap.clear();

        for (AudioBuffer audioBuffer : audioBufferList) {
            audioBuffer.cleanup();
        }
        audioBufferList.clear();

        if (context != NULL) {
            try {
            alcDestroyContext(context);
            } catch(Exception e) {
                Logger.log_error("Could not destroy audio context :(");
                e.printStackTrace();
            }
        }

        if (device != NULL) {
            try {
                alcCloseDevice(device);
            } catch(Exception e) {
                Logger.log_error("Could not close device :(");
                e.printStackTrace();
            }
        }
    }
}
