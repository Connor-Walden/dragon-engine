package com.salami.dragon.engine;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.event.Event;
import com.salami.dragon.engine.light.PointLight;
import com.salami.dragon.engine.light.SceneLight;
import com.salami.dragon.engine.render.IHud;
import com.salami.dragon.engine.render.RenderMode;
import com.salami.dragon.engine.render.Window;

import java.util.*;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Application {
    public static final int DAY_CYCLE_TIME = 600;// In seconds

    private final IApplication app;
    private final EventGovernor eventGovernor;
    private final Window window;

    private static Application instance;

    private final List<Entity> entities;
    SceneLight sceneLight;
    float sunAngle;

    boolean doDaylightCycle = true;

    IHud hud;

    private Application(IApplication app) throws Exception {
        this.app = app;
        this.eventGovernor = new EventGovernor(null);

        this.sceneLight = new SceneLight();

        this.entities = new ArrayList<>();
        instance = this;

        window = new Window(
            this,
            app.WIDTH(),
            app.HEIGHT(),
            app.TITLE(),
            app.CAMERA()
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

        this.eventGovernor.registerEvents(eventMap);
    }

    public static void registerApp(IApplication app) throws Exception {
        instance = new Application(app);
    }

    public static void registerListener(IListener listener, EventType... eventTypes) {
        for(EventType eventType : eventTypes) {
            instance.eventGovernor.registerListener(eventType, listener);
        }
    }

    public static void registerEntity(Entity entity) {
        instance.entities.add(entity);
    }

    public void start() throws Exception {
        this.eventGovernor.fireEvent(EventType.APPLICATION_START);

        // Follow application flow by initialising and going into tick()
        init();
        tick();
    }

    public static void stop() {
        // Destroy the window
        instance.getWindow().invalidateWindow();

        instance.getEventGovernor().fireEvent(EventType.APPLICATION_STOP);
    }

    public void init() throws Exception {
        window.init();
        app.init();

        this.eventGovernor.fireEvent(EventType.APPLICATION_INIT);
    }

    public void tick() {
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
            glfwSetInputMode(instance.getWindow().getGLFWWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            setCursor(0, 0);
        } else {
            glfwSetInputMode(instance.getWindow().getGLFWWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            centreCursor();
        }
    }

    public static void setDoDaylightCycle(boolean doCycle) {
        instance.doDaylightCycle = doCycle;
    }

    public static boolean getDoDaylightCycle() {
        return instance.doDaylightCycle;
    }

    public static void centreCursor() {
        glfwSetCursorPos(instance.getWindow().getGLFWWindow(), instance.getWindow().getWidth() / 2f, instance.getWindow().getHeight() / 2f);
    }

    public static void setCursor(float xPos, float yPos) {
        glfwSetCursorPos(instance.getWindow().getGLFWWindow(), xPos, yPos);
    }

    public static void setRenderMode(RenderMode renderMode) {
        instance.getWindow().setRenderMode(renderMode);
    }

    public static SceneLight getSceneLight() {
        return instance.sceneLight;
    }

    public static void setSunAngle(float angle) {
        instance.sunAngle = angle;
    }

    public static float getSunAngle() {
        return instance.sunAngle;
    }

    public static void addPointLight(PointLight pointLight) {
        PointLight[] pointLightList = instance.sceneLight.getPointLightList();

        if(pointLightList == null) pointLightList = new PointLight[1];

        PointLight[] newArr = new PointLight[pointLightList.length + 1];

        for(int i = 0; i < newArr.length; i++) {
            if(i != pointLightList.length) {
                newArr[i] = pointLightList[i];
            } else {
                newArr[i] = pointLight;
            }
        }

        instance.sceneLight.setPointLightList(newArr);
    }

    public static void removePointLight(PointLight pointLight) {
        PointLight[] pointLightList = instance.sceneLight.getPointLightList();

        List<PointLight> newList = new ArrayList<>();

        for(PointLight pl : pointLightList) {
            if(pl != pointLight) {
                newList.add(pl);
            }
        }

        newList.toArray(instance.sceneLight.getPointLightList());
    }

    public static PointLight[] getPointLights() {
        return instance.sceneLight.getPointLightList();
    }

    public static void setHUD(IHud hud) {
        instance.hud = hud;
    }

    public static IHud getHUD() {
        return instance.hud;
    }

    public static void maximizeWindow() {
        glfwMaximizeWindow(instance.getWindow().getGLFWWindow());
    }

    public EventGovernor getEventGovernor() {
        return eventGovernor;
    }

    public Window getWindow() {
        return window;
    }
}
