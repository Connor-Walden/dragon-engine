package com.salami.dragon.engine;

import com.salami.dragon.engine.audio.Audio;
import com.salami.dragon.engine.audio.AudioBuffer;
import com.salami.dragon.engine.audio.AudioSource_;
import com.salami.dragon.engine.ecs.component.Components;
import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.ecs.component.prefab.AudioListener;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.event.Event;
import com.salami.dragon.engine.log.Logger;
import com.salami.dragon.engine.render.RenderMode;
import com.salami.dragon.engine.render.Window;

import java.util.*;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.AL11.AL_EXPONENT_DISTANCE;

public class Application {
    public static final int DAY_CYCLE_TIME = 600;// In seconds

    private final IApplication app;
    private final EventGovernor eventGovernor;
    private final Window window;

    private static Application instance;

    private final List<Entity> entities;

    World world;
    Audio audio;

    private Application(IApplication app) throws Exception {
        this.app = app;
        this.eventGovernor = new EventGovernor(null);

        this.entities = new ArrayList<>();
        instance = this;

        window = new Window(
            this,
            app.WIDTH(),
            app.HEIGHT(),
            app.TITLE(),
            app.CAMERA(),
            app.WINDOW_OPTIONS()
        );

        setupEvents();

        start();
    }

    private void setupEvents() {
        Map<EventType, Event> eventMap = new HashMap<>();
        eventMap.put(EventType.APPLICATION_START, new Event(EventType.APPLICATION_START));
        eventMap.put(EventType.APPLICATION_STOP, new Event(EventType.APPLICATION_STOP));
        eventMap.put(EventType.APPLICATION_INIT, new Event(EventType.APPLICATION_INIT));
        eventMap.put(EventType.APPLICATION_TICK, new Event(EventType.APPLICATION_TICK));
        eventMap.put(EventType.COMPONENTS_INIT, new Event(EventType.COMPONENTS_INIT));
        eventMap.put(EventType.COMPONENTS_TICK, new Event(EventType.COMPONENTS_TICK));
        this.eventGovernor.registerEvents(eventMap);
    }

    public static void registerApp(IApplication app) throws Exception {
        System.setProperty("java.awt.headless", "true");

        instance = new Application(app);
    }

    public static void registerListeners(IListener listener, EventType... eventTypes) {
        for(EventType eventType : eventTypes) {
            instance.eventGovernor.registerListener(eventType, listener);
        }
    }

    public static void registerAudio(Entity listener) throws Exception {
        instance.audio = new Audio();
        instance.audio.init();
        instance.audio.setAttenuationModel(AL_EXPONENT_DISTANCE);

        AudioListener audioListenerComponent = (AudioListener) listener.getComponent(Components.AUDIO_LISTENER);
        instance.audio.setListener(audioListenerComponent.getSource());
    }

    public static Audio getAudio() {
        return instance.audio;
    }

    public static AudioSource_ createAudioSource(String filePath, boolean loop, boolean relative) throws Exception {
        AudioBuffer buff = new AudioBuffer(filePath);
        instance.audio.addAudioBuffer(buff);
        AudioSource_ source = new AudioSource_(loop, relative);
        instance.audio.addAudioSource("AudioSource(" + filePath + ")", source);
        source.setBuffer(buff.getBufferId());

        return source;
    }

    public static void registerEntity(Entity entity) {
        instance.entities.add(entity);
        getWorld().setEntities(instance.getEntities());
    }

    public static void registerEntities(Entity... entities) {
        for(Entity entity : entities) {
            registerEntity(entity);
        }
    }

    public void start() throws Exception {
        this.eventGovernor.fireEvent(EventType.APPLICATION_START);

        Logger.log_highlight("Hello from Dragon game engine!");

        // Follow application flow by initialising and going into tick()
        init();
        tick();
    }

    public static void stop() {
        getEventGovernor().fireEvent(EventType.APPLICATION_STOP);

        getAudio().cleanup();

        // Destroy the window
        getWindow().invalidateWindow();
    }

    public void init() throws Exception {
        window.init();
        app.init();

        for(Entity entity : entities) {
            List<IComponent> componentList = entity.getComponents();

            for(IComponent component : componentList) {
                component.init();
            }

        }

        eventGovernor.fireEvent(EventType.COMPONENTS_INIT);
        eventGovernor.fireEvent(EventType.APPLICATION_INIT);
    }

    public void tick() throws Exception {
        float now, last = 0;

        // Loop continuously and render and update
        while (window.RUNNING())
        {
            // Get the time
            now = (float) window.getTime();
            float delta = now - last;
            last = now;

            app.tick(delta);
            window.tick(delta);

            for(Entity entity : entities) {
                List<IComponent> componentList = entity.getComponents();

                for(IComponent component : componentList) {
                    component.tick(delta);
                }
            }

            getWorld().getSkyBox().setPosition(app.CAMERA().getPosition().x, app.CAMERA().getPosition().y, app.CAMERA().getPosition().z);

            eventGovernor.fireEvent(EventType.COMPONENTS_TICK);
            this.eventGovernor.fireEvent(EventType.APPLICATION_TICK);
        }

        stop();
    }

    public Entity[] getEntities() {
        Entity[] entitiesArray = new Entity[entities.size()];

        for(int i = 0; i < entities.size(); i++) {
            entitiesArray[i] = entities.get(i);
        }

        return entitiesArray;
    }

    public static void setCursorCaptured(boolean captured) {
        if(captured) {
            glfwSetInputMode(getWindow().getGLFWWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            setCursor(0, 0);
        } else {
            glfwSetInputMode(getWindow().getGLFWWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            centreCursor();
        }
    }

    public static void centreCursor() {
        glfwSetCursorPos(getWindow().getGLFWWindow(), getWindow().getWidth() / 2f, getWindow().getHeight() / 2f);
    }

    public static void setCursor(float xPos, float yPos) {
        glfwSetCursorPos(getWindow().getGLFWWindow(), xPos, yPos);
    }

    public static void setRenderMode(RenderMode renderMode) {
        getWindow().setRenderMode(renderMode);
    }

    public static void setWorld(World world) {
        instance.world = world;
    }

    public static World getWorld() {
        return instance.world;
    }

    public static void maximizeWindow() {
        glfwMaximizeWindow(getWindow().getGLFWWindow());
    }

    public static EventGovernor getEventGovernor() {
        return instance.eventGovernor;
    }

    public static Window getWindow() {
        return instance.window;
    }
}
