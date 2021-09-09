package com.salami.dragon.engine.input;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.event.Event;
import com.salami.dragon.engine.event.EventTime;
import com.salami.dragon.engine.event.EventType;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static List<Integer> keysHeld;
    private static List<Integer> mouseButtonsHeld;

    private static double mouseX, mouseY;

    public static final int MOUSE_LEFT = GLFW_MOUSE_BUTTON_LEFT;
    public static final int MOUSE_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;

    public static final int KEY_A = GLFW_KEY_A;
    public static final int KEY_B = GLFW_KEY_B;
    public static final int KEY_C = GLFW_KEY_C;
    public static final int KEY_D = GLFW_KEY_D;
    public static final int KEY_E = GLFW_KEY_E;
    public static final int KEY_F = GLFW_KEY_F;
    public static final int KEY_G = GLFW_KEY_G;
    public static final int KEY_H = GLFW_KEY_H;
    public static final int KEY_I = GLFW_KEY_I;
    public static final int KEY_J = GLFW_KEY_J;
    public static final int KEY_K = GLFW_KEY_K;
    public static final int KEY_L = GLFW_KEY_L;
    public static final int KEY_M = GLFW_KEY_M;
    public static final int KEY_N = GLFW_KEY_N;
    public static final int KEY_O = GLFW_KEY_O;
    public static final int KEY_P = GLFW_KEY_P;
    public static final int KEY_Q = GLFW_KEY_Q;
    public static final int KEY_R = GLFW_KEY_R;
    public static final int KEY_S = GLFW_KEY_S;
    public static final int KEY_T = GLFW_KEY_T;
    public static final int KEY_U = GLFW_KEY_U;
    public static final int KEY_V = GLFW_KEY_V;
    public static final int KEY_W = GLFW_KEY_W;
    public static final int KEY_X = GLFW_KEY_X;
    public static final int KEY_Y = GLFW_KEY_Y;
    public static final int KEY_Z = GLFW_KEY_Z;

    public static final int KEY_SPACE = GLFW_KEY_SPACE;
    public static final int KEY_LSHIFT = GLFW_KEY_LEFT_SHIFT;
    public static final int KEY_ESCAPE = GLFW_KEY_ESCAPE;

    public static final int KEY_F1 = GLFW_KEY_F1;
    public static final int KEY_F2 = GLFW_KEY_F2;
    public static final int KEY_F3 = GLFW_KEY_F3;
    public static final int KEY_F4 = GLFW_KEY_F4;
    public static final int KEY_F5 = GLFW_KEY_F5;
    public static final int KEY_F6 = GLFW_KEY_F6;
    public static final int KEY_F7 = GLFW_KEY_F7;
    public static final int KEY_F8 = GLFW_KEY_F8;
    public static final int KEY_F9 = GLFW_KEY_F9;
    public static final int KEY_F10 = GLFW_KEY_F10;
    public static final int KEY_F11 = GLFW_KEY_F11;
    public static final int KEY_F12 = GLFW_KEY_F12;

    public static void init() {
        keysHeld = new ArrayList<>();
        mouseButtonsHeld = new ArrayList<>();

        Map<EventType, Event> eventMap = new HashMap<>();

        eventMap.put(EventType.KEY_PRESS, new Event(EventType.KEY_PRESS, EventTime.BEFORE_EVENT));
        eventMap.put(EventType.KEY_RELEASE, new Event(EventType.KEY_RELEASE, EventTime.BEFORE_EVENT));
        eventMap.put(EventType.MOUSE_BUTTON_PRESS, new Event(EventType.MOUSE_BUTTON_PRESS, EventTime.BEFORE_EVENT));
        eventMap.put(EventType.MOUSE_BUTTON_RELEASE, new Event(EventType.MOUSE_BUTTON_RELEASE, EventTime.BEFORE_EVENT));
        eventMap.put(EventType.MOUSE_MOVE, new Event(EventType.MOUSE_MOVE, EventTime.BEFORE_EVENT));
        eventMap.put(EventType.MOUSE_SCROLL, new Event(EventType.MOUSE_SCROLL, EventTime.BEFORE_EVENT));

        // Register before events
        Application.getEventGovernor().registerEvents(eventMap);
        eventMap = new HashMap<>();

        eventMap.put(EventType.KEY_PRESS, new Event(EventType.KEY_PRESS, EventTime.AFTER_EVENT));
        eventMap.put(EventType.KEY_RELEASE, new Event(EventType.KEY_RELEASE, EventTime.AFTER_EVENT));
        eventMap.put(EventType.MOUSE_BUTTON_PRESS, new Event(EventType.MOUSE_BUTTON_PRESS, EventTime.AFTER_EVENT));
        eventMap.put(EventType.MOUSE_BUTTON_RELEASE, new Event(EventType.MOUSE_BUTTON_RELEASE, EventTime.AFTER_EVENT));
        eventMap.put(EventType.MOUSE_MOVE, new Event(EventType.MOUSE_MOVE, EventTime.AFTER_EVENT));
        eventMap.put(EventType.MOUSE_SCROLL, new Event(EventType.MOUSE_SCROLL, EventTime.AFTER_EVENT));

        // Register after events
        Application.getEventGovernor().registerEvents(eventMap);
    }

    public static void onKeyPress(int key) {
        Application.getEventGovernor().fireEvent(EventType.KEY_PRESS, EventTime.BEFORE_EVENT, key);

        keysHeld.add(key);

        Application.getEventGovernor().fireEvent(EventType.KEY_PRESS, EventTime.AFTER_EVENT, key);
    }

    public static void onKeyRelease(int key) {
        Application.getEventGovernor().fireEvent(EventType.KEY_RELEASE, EventTime.BEFORE_EVENT, key);

        keysHeld.remove((Integer) key);

        Application.getEventGovernor().fireEvent(EventType.KEY_RELEASE, EventTime.AFTER_EVENT, key);
    }

    public static void onMouseMove(double xPos, double yPos) {
        Application.getEventGovernor().fireEvent(EventType.MOUSE_MOVE, EventTime.BEFORE_EVENT, xPos, yPos);

        mouseX = xPos;
        mouseY = yPos;

        Application.getEventGovernor().fireEvent(EventType.MOUSE_MOVE, EventTime.AFTER_EVENT, xPos, yPos);
    }

    public static void onMouseButtonPress(int button) {
        Application.getEventGovernor().fireEvent(EventType.MOUSE_BUTTON_PRESS, EventTime.BEFORE_EVENT, button);

        mouseButtonsHeld.add(button);

        Application.getEventGovernor().fireEvent(EventType.MOUSE_BUTTON_PRESS, EventTime.AFTER_EVENT, button);
    }

    public static void onMouseButtonRelease(int button) {
        Application.getEventGovernor().fireEvent(EventType.MOUSE_BUTTON_RELEASE, EventTime.BEFORE_EVENT, button);

        if(mouseButtonsHeld.contains(button))
            mouseButtonsHeld.remove((Integer) button);

        Application.getEventGovernor().fireEvent(EventType.MOUSE_BUTTON_RELEASE, EventTime.AFTER_EVENT, button);
    }

    public static void onMouseScroll(double amount) {
        Application.getEventGovernor().fireEvent(EventType.MOUSE_SCROLL, EventTime.BEFORE_EVENT, amount);
        Application.getEventGovernor().fireEvent(EventType.MOUSE_SCROLL, EventTime.AFTER_EVENT, amount);
    }

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static boolean isKeyDown(int key) {
        return keysHeld.contains(key);
    }

    public static boolean isMouseButtonDown(int button) {
        return mouseButtonsHeld.contains(button);
    }
}
