package com.salami.dragon.sandbox;

import com.salami.dragon.engine.*;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.component.Components;
import com.salami.dragon.engine.ecs.component.prefab.*;
import com.salami.dragon.engine.ecs.entity.prefab.*;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.input.Input;
import com.salami.dragon.engine.render.RenderMode;
import com.salami.dragon.engine.render.Window;

public class Sandbox implements IApplication, IListener {
    static Player player;

    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }
    @Override public Camera CAMERA() { return player.getCamera(); }
    @Override public Window.WindowOptions WINDOW_OPTIONS() { return new Window.WindowOptions(); }

    Cube cubeObject;
    Bunny bunnyObject;

    AudioSource cubeAudioSource;
    AudioSource bunnyAudioSource;

    public static void main(String[] args) throws Exception {
        player = new Player(new Camera());

        // TODO: Add validation to determine if the camera has been created or not.
        Application.registerApp(new Sandbox());
    }

    @Override
    public void init() throws Exception {
        // Application creation phase
        cubeObject = new Cube().setScale(0.5f).setPosition(0, 0, -5);
        bunnyObject = new Bunny().setScale(0.5f).setPosition(2, 0, -5);

        cubeAudioSource = new AudioSource(cubeObject.getCubeEntity(), "/audio/beep.ogg", true, true);
        bunnyAudioSource = new AudioSource(bunnyObject.getBunnyEntity(), "/audio/fire.ogg", true, false);

        cubeObject.addComponent(Components.AUDIO_SOURCE, cubeAudioSource);
        bunnyObject.addComponent(Components.AUDIO_SOURCE, bunnyAudioSource);

        player.addComponent(Components.AUDIO_LISTENER, new AudioListener(player));

        // Application configuration phase
        Application.setCursorCaptured(true);

        Application.setWorld(new World());
        Application.getWorld().setDoDaylightCycle(true);

        // Application registration phase
        Application.registerListeners(this, EventType.KEY_PRESS, EventType.MOUSE_MOVE, EventType.APPLICATION_STOP, EventType.COMPONENTS_INIT);
        Application.registerEntities(cubeObject.getCubeEntity(), bunnyObject.getBunnyEntity(), player);
        Application.registerAudio(player);
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {
    }

    @Override
    public void onEngineEvent(Event event) {
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
    }

    @Override
    public void onMouseScrollEvent(Event event, double amount) {}

    @Override
    public void onAudioEvent(Event event) {

    }

    @Override
    public void onComponentEvent(Event event) {
        if(event.getEventType() == EventType.COMPONENTS_INIT) {
            cubeAudioSource.getSource().play();
            bunnyAudioSource.getSource().play();
        }
    }
}
