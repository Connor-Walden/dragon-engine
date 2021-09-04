package com.salami.dragon.sandbox;

import com.salami.dragon.engine.IApplication;
import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.World;
import com.salami.dragon.engine.audio.Audio;
import com.salami.dragon.engine.audio.AudioBuffer;
import com.salami.dragon.engine.audio.AudioListener;
import com.salami.dragon.engine.audio.AudioSource;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.camera.FirstPersonCamera;
import com.salami.dragon.engine.ecs.entity.prefab.Bunny;
import com.salami.dragon.engine.ecs.entity.prefab.Cube;
import com.salami.dragon.engine.event.IListener;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.input.Input;
import com.salami.dragon.engine.light.PointLight;
import com.salami.dragon.engine.render.RenderMode;
import com.salami.dragon.engine.render.Window;
import org.joml.Vector3f;

import static org.lwjgl.openal.AL11.AL_EXPONENT_DISTANCE;

public class Sandbox implements IApplication, IListener {
    static Camera camera;

    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }
    @Override public Camera CAMERA() { return camera; }
    @Override public Window.WindowOptions WINDOW_OPTIONS() { return new Window.WindowOptions(); }

    Cube cubeObject;
    Bunny bunnyObject;
    Audio audio;

    FirstPersonCamera firstPersonCamera;
    PointLight playerLight = new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), 1.0f);

    private enum Sounds {
        MUSIC, BEEP, FIRE
    };

    public static void main(String[] args) throws Exception {
        camera = new Camera();

        // TODO: Add validation to determine if the camera has been created or not.
        Application.registerApp(new Sandbox());
    }

    @Override
    public void init() throws Exception {
        firstPersonCamera = new FirstPersonCamera(camera);

        cubeObject = new Cube().setScale(0.5f).setPosition(0, 0, -5);
        bunnyObject = new Bunny().setScale(0.5f).setPosition(2, 0, -5);

        audio = new Audio();
        audio.init();
        audio.setAttenuationModel(AL_EXPONENT_DISTANCE);
        setupAudio();

        // Application configuration phase
        Application.setCursorCaptured(true);

        Application.setWorld(new World());
        Application.getWorld().setDoDaylightCycle(true);

        // Application registration phase
        Application.registerListeners(this, EventType.KEY_PRESS, EventType.MOUSE_MOVE, EventType.APPLICATION_STOP);

        Application.registerEntity(cubeObject.getCubeEntity());
        Application.registerEntity(bunnyObject.getBunnyEntity());

    }

    private void setupAudio() throws Exception {
        AudioBuffer buffBack = new AudioBuffer("/audio/background.ogg");
        audio.addAudioBuffer(buffBack);
        AudioSource sourceBack = new AudioSource(true, true);
        sourceBack.setBuffer(buffBack.getBufferId());
        audio.addAudioSource(Sounds.MUSIC.toString(), sourceBack);

        AudioBuffer buffBeep = new AudioBuffer("/audio/beep.ogg");
        audio.addAudioBuffer(buffBeep);
        AudioSource sourceBeep = new AudioSource(false, true);
        sourceBeep.setBuffer(buffBeep.getBufferId());
        audio.addAudioSource(Sounds.BEEP.toString(), sourceBeep);

        AudioBuffer buffFire = new AudioBuffer("/audio/fire.ogg");
        audio.addAudioBuffer(buffFire);
        AudioSource sourceFire = new AudioSource(true, false);
        Vector3f pos = cubeObject.getCubeEntity().getPosition();
        sourceFire.setPosition(pos);
        sourceFire.setBuffer(buffFire.getBufferId());
        audio.addAudioSource(Sounds.FIRE.toString(), sourceFire);
        sourceFire.play();

        audio.setListener(new AudioListener(new Vector3f()));

        sourceBack.play();
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {
        firstPersonCamera.movePlayer();
        firstPersonCamera.rotateCamera();

        playerLight.setPosition(camera.getPosition());
    }

    @Override
    public void onEngineEvent(Event event) {
        if(event.getEventType() == EventType.APPLICATION_STOP) {
            audio.cleanup();
        }
    }

    @Override
    public void onKeyOrMouseButtonEvent(Event event, int key) {
        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_ESCAPE) {
            Application.stop();
        }

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F1) {
            Application.setRenderMode(RenderMode.FILL);
        }

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F2) {
            Application.setRenderMode(RenderMode.LINE);
        }

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F3) {
            Application.setRenderMode(RenderMode.POINT);
        }
    }

    @Override
    public void onMouseMoveEvent(Event event, double xPos, double yPos) {
        firstPersonCamera.updateMousePos(xPos, yPos);
    }

    @Override
    public void onMouseScrollEvent(Event event, double amount) {}

    @Override
    public void onAudioEvent(Event event) {

    }
}
