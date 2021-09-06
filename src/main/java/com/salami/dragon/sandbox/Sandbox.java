package com.salami.dragon.sandbox;

import com.salami.dragon.engine.*;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.ecs.component.Components;
import com.salami.dragon.engine.ecs.component.prefab.*;
import com.salami.dragon.engine.ecs.entity.prefab.*;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.input.Input;
import com.salami.dragon.engine.render.Fog;
import com.salami.dragon.engine.render.RenderMode;
import com.salami.dragon.engine.render.Window;
import org.joml.Vector3f;

public class Sandbox implements IApplication, IListener {
    static Player player;

    @Override public int WIDTH() { return 1280; }
    @Override public int HEIGHT() { return 720; }
    @Override public String TITLE() { return "Sandbox (built with Dragon game engine)"; }
    @Override public Camera CAMERA() { return player.getCamera(); }
    @Override public Window.WindowOptions WINDOW_OPTIONS() { return new Window.WindowOptions(); }

    Cube cubeObject;
    Bunny bunnyObject;

    public static void main(String[] args) throws Exception {
        player = new Player(new Camera());

        // TODO: Add validation to determine if the camera has been created or not.
        Application.registerApp(new Sandbox());
    }

    @Override
    public void init() throws Exception {
        // Application creation phase
        cubeObject = new Cube().setScale(0.5f).setPosition(0, -2, -5);
        bunnyObject = new Bunny().setScale(0.5f).setPosition(0, 0, -5);

        cubeObject.addComponent(
                Components.AUDIO_SOURCE,
                new AudioSource(cubeObject.getCubeEntity(), "/audio/beep.ogg", true, false)
        );

        bunnyObject.addComponent(
                Components.AUDIO_SOURCE,
                new AudioSource(bunnyObject.getBunnyEntity(), "/audio/fire.ogg", true, false)
        );

        // Application configuration phase
        Application.setCursorCaptured(true);

        Application.setWorld(new World());
        Application.getWorld().setDoDaylightCycle(false);

        // Application registration phase
        Application.registerListeners(this, EventType.KEY_PRESS, EventType.MOUSE_MOVE, EventType.APPLICATION_STOP, EventType.COMPONENTS_INIT);
        Application.registerEntities(cubeObject.getCubeEntity(), bunnyObject.getBunnyEntity(), player);
        Application.registerAudio(player);
        Application.registerFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.15f, 15.0f));
    }

    // delta - time in seconds since last frame.
    @Override
    public void tick(float delta) {
        //cubeObject.getCubeEntity().move(0.01f, 0, 0);
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

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F4) {
            AudioSource source = (AudioSource) cubeObject.getCubeEntity().getComponent(Components.AUDIO_SOURCE);

            source.getSource().pause();
        }

        if(event.getEventType() == EventType.KEY_PRESS && key == Input.KEY_F5) {
            AudioSource source = (AudioSource) cubeObject.getCubeEntity().getComponent(Components.AUDIO_SOURCE);

            source.getSource().play();
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
            AudioSource cubeSource = (AudioSource) cubeObject.getCubeEntity().getComponent(Components.AUDIO_SOURCE);
            AudioSource bunnySource = (AudioSource) bunnyObject.getBunnyEntity().getComponent(Components.AUDIO_SOURCE);
            cubeSource.getSource().play();
            bunnySource.getSource().play();
        }
    }
}
