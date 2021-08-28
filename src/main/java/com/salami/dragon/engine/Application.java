package com.salami.dragon.engine;

import com.salami.dragon.engine.event.*;
import com.salami.dragon.engine.log.Logger;
import com.salami.dragon.engine.window.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {
    private final IApplication app;
    private final EventGovernor eventGovernor;
    private final Window window;

    private static Application instance;

    private Application(IApplication app) {
        this.app = app;
        this.eventGovernor = new EventGovernor(null);
        instance = this;

        window = new Window(
            this,
            app.WIDTH(),
            app.HEIGHT(),
            app.TITLE()
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

    public static void registerApp(IApplication app) {
        instance = new Application(app);
    }

    public static void registerListener(List<EventType> eventTypeList, IListener listener) {
        for(EventType eventType : eventTypeList) {
            instance.eventGovernor.registerListener(eventType, listener);
        }
    }

    public void start() {
        this.eventGovernor.fireEvent(EventType.APPLICATION_START);

        // Follow application flow by initialising and going into tick()
        init();
        tick();
    }

    public void stop() {
        // Destroy the window
        window.terminate();

        this.eventGovernor.fireEvent(EventType.APPLICATION_STOP);
    }

    public void init() {
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

    public EventGovernor getEventGovernor() {
        return eventGovernor;
    }
}