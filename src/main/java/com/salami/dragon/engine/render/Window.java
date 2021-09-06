package com.salami.dragon.engine.render;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.event.Event;
import com.salami.dragon.engine.event.EventType;
import com.salami.dragon.engine.input.Input;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private GraphicsContext context;
    private final Application app;
    private Camera camera;
    private WindowOptions opts;

    private final String title;

    private int width;
    private int height;
    private int renderMode = 0; // 0 - fill, 1 - line, 2 - point
    private int prevXPos, prevYPos, prevWidth, prevHeight;

    private long windowHandle;
    private long window;

    private boolean resized;
    private boolean vSync;

    private float dayCycleTimer = 0.0f;

    public Window(Application app, int width, int height, String title, Camera camera, WindowOptions opts) {
        this.camera = camera;
        this.width = width;
        this.height = height;
        this.title = title;
        this.app = app; // Needed for events.
        this.opts = opts;

        registerEvents(
                EventType.WINDOW_OPEN, EventType.WINDOW_CLOSE, EventType.WINDOW_RESIZE,
                EventType.WINDOW_MOVE, EventType.WINDOW_FOCUS, EventType.WINDOW_LOST_FOCUS,
                EventType.WINDOW_MAXIMIZE, EventType.WINDOW_UN_MAXIMIZE
        );
    }

    public void init() throws Exception {
        Input.init(app);

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
        {
            System.err.println("Error initializing GLFW");
            System.exit(1);
        }

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        Application.getEventGovernor().fireEvent(EventType.WINDOW_OPEN);

        centreWindow();

        if (window == NULL)
        {
            System.err.println("Error creating a window");
            System.exit(1);
        }


        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        if (opts.compatibleProfile) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        boolean maximized = false;
        // If no size has been specified set it to maximized state
        if (width == 0 || height == 0) {
            // Set up a fixed width and height so window initialization does not fail
            width = 100;
            height = 100;

            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

            maximized = true;
        }

        context = new GraphicsContext(this);
        context.init();

        GL.createCapabilities();

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        glEnable(GL_DEPTH_TEST);

        glPointSize(5.0f);
        glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );

        // Support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        setupGLFWCallbacks();

        context.prepare();
    }

    private void setupGLFWCallbacks() {
        // Callbacks for window events
        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback(){
            @Override public void invoke(long window, int _width, int _height){
                if(prevWidth != _width || prevHeight != _height) {
                    glViewport(0, 0, _width, _height);

                    Application.getEventGovernor().fireEvent(EventType.WINDOW_RESIZE);

                    context.swapBuffers(camera, Application.getWorld());
                }

                prevWidth = _width;
                prevHeight = _height;
            }
        });

        glfwSetWindowPosCallback(window, new GLFWWindowPosCallback() {
            @Override public void invoke(long window, int xpos, int ypos) {
                if(xpos != prevXPos || ypos != prevYPos) {
                    Application.getEventGovernor().fireEvent(EventType.WINDOW_MOVE);
                }

                prevXPos = xpos;
                prevYPos = ypos;
            }
        });

        glfwSetWindowFocusCallback(window, new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long window, boolean focused) {
                if(focused) {
                    Application.getEventGovernor().fireEvent(EventType.WINDOW_FOCUS);
                } else {
                    Application.getEventGovernor().fireEvent(EventType.WINDOW_LOST_FOCUS);
                }
            }
        });

        glfwSetWindowMaximizeCallback(window, new GLFWWindowMaximizeCallback() {
            @Override
            public void invoke(long window, boolean maximized) {
                if(maximized) {
                    Application.getEventGovernor().fireEvent(EventType.WINDOW_MAXIMIZE);
                } else {
                    Application.getEventGovernor().fireEvent(EventType.WINDOW_UN_MAXIMIZE);
                }
            }
        });

        // Input callbacks
        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(action == GLFW_PRESS) {
                    Input.onKeyPress(key);
                } else if(action == GLFW_RELEASE) {
                    Input.onKeyRelease(key);
                }
            }
        });

        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if(action == GLFW_PRESS) {
                    Input.onMouseButtonPress(button);
                } else if(action == GLFW_RELEASE) {
                    Input.onMouseButtonRelease(button);
                }
            }
        });

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                Input.onMouseMove(xPos, yPos);
            }
        });

        glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                Input.onMouseScroll(yoffset);
            }
        });
    }

    private void centreWindow() {
        // Get window position and size
        int[] window_x = new int[1], window_y = new int[1];
        glfwGetWindowPos(window, window_x, window_y);

        int[] window_width = new int[1], window_height = new int[1];
        glfwGetWindowSize(window, window_width, window_height);

        // Halve the window size and use it to adjust the window position to the center of the window
        window_width[0] *= 0.5;
        window_height[0] *= 0.5;

        window_x[0] += window_width[0];
        window_y[0] += window_height[0];

        // Get the list of monitors
        long monitor = glfwGetPrimaryMonitor();

        if(monitor == NULL) {
            // Got no monitors back
            return;
        }

        // Figure out which monitor the window is in
        long owner = NULL;
        int owner_x = 0 , owner_y = 0, owner_width = 0, owner_height = 0;

        // Get the monitor position
        int[] monitor_x = new int[1], monitor_y = new int[1];
        glfwGetMonitorPos(monitor, monitor_x, monitor_y);

        // Get the monitor size from its video mode
        int monitor_width, monitor_height;
        GLFWVidMode monitor_vidmode = glfwGetVideoMode(monitor);

        assert monitor_vidmode != null;
        monitor_width = monitor_vidmode.width();
        monitor_height = monitor_vidmode.height();

        // Set the owner to this monitor if the center of the window is within its bounding box
        if((window_x[0] > monitor_x[0] && window_x[0] < (monitor_x[0] + monitor_width)) && (window_y[0] > monitor_y[0] && window_y[0] < (monitor_y[0] + monitor_height))) {
            owner = monitor;

            owner_x = monitor_x[0];
            owner_y = monitor_y[0];

            owner_width = monitor_width;
            owner_height = monitor_height;
        }

        if(owner != NULL) {
            // Set the window position to the center of the owner monitor
            glfwSetWindowPos(window, (int)Math.round(owner_x + (owner_width * 0.5) - window_width[0]), (int)Math.round(owner_y + (owner_height * 0.5) - window_height[0]));
        }
    }

    public void terminate() {
        context.cleanUp();

        glfwDestroyWindow(window);
        Application.getEventGovernor().fireEvent(EventType.WINDOW_CLOSE);

        glfwTerminate();
    }

    public void tick(float delta) {
        // Poll the events and swap the buffers
        glfwPollEvents();

        daylightCycle(delta);

        context.swapBuffers(camera, Application.getWorld());
    }

    public boolean RUNNING() {
        if(window != NULL && !glfwWindowShouldClose(window))
            return true;

        terminate();

        return false;
    }

    public void invalidateWindow() {
        window = NULL;
    }

    public double getTime() {
        return glfwGetTime();
    }

    public GraphicsContext getContext() {
        return context;
    }

    public long getGLFWWindow() {
        return window;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public RenderMode getRenderMode() {
        return switch (renderMode) {
            case 1 -> RenderMode.LINE;
            case 2 -> RenderMode.POINT;
            default -> RenderMode.FILL;
        };
    }

    public void setRenderMode(RenderMode mode) {
        switch (mode) {
            case FILL -> {
                renderMode = 0;
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }
            case LINE -> {
                renderMode = 1;
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            }
            case POINT -> {
                renderMode = 2;
                glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
            }
        }
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public WindowOptions getWindowOptions() {
        return opts;
    }


    public static class WindowOptions {

        public boolean cullFace;

        public boolean showTriangles;

        public boolean showFps;

        public boolean compatibleProfile;
    }

    private void registerEvents(EventType... events) {
        // Events
        Map<EventType, Event> eventMap = new HashMap<>();

        for(EventType eventType : events) {
            eventMap.put(eventType, new Event(eventType));
        }

        Application.getEventGovernor().registerEvents(eventMap);
    }

    private void daylightCycle(float delta) {

        if(Application.getWorld().getDoDaylightCycle())
            dayCycleTimer += delta;

        if(dayCycleTimer > Application.DAY_CYCLE_TIME) {
            dayCycleTimer = 0;
        }

        float percentDayCycleComplete = dayCycleTimer / Application.DAY_CYCLE_TIME;

        // Update directional light direction, intensity and colour

        Application.getWorld().setSunAngle(360 * percentDayCycleComplete);

        if (Application.getWorld().getSunAngle() > 90) {
            Application.getWorld().getWorldLight().getDirectionalLight().setIntensity(0);
            if (Application.getWorld().getSunAngle() >= 360) {
                Application.getWorld().setSunAngle(-90);
            }
        } else if (Application.getWorld().getSunAngle() <= -80 || Application.getWorld().getSunAngle() >= 80) {
            float factor = 1 - (float)(Math.abs(Application.getWorld().getSunAngle()) - 80)/ 10.0f;
            Application.getWorld().getWorldLight().getDirectionalLight().setIntensity(factor);
            Application.getWorld().getWorldLight().getDirectionalLight().getColor().y = Math.max(factor, 0.9f);
            Application.getWorld().getWorldLight().getDirectionalLight().getColor().z = Math.max(factor, 0.5f);
        } else {
            Application.getWorld().getWorldLight().getDirectionalLight().setIntensity(1);
            Application.getWorld().getWorldLight().getDirectionalLight().getColor().x = 1;
            Application.getWorld().getWorldLight().getDirectionalLight().getColor().y = 1;
            Application.getWorld().getWorldLight().getDirectionalLight().getColor().z = 1;
        }

        double angRad = Math.toRadians(Application.getWorld().getSunAngle());

        Application.getWorld().getWorldLight().getDirectionalLight().getDirection().x = (float) Math.sin(angRad);
        Application.getWorld().getWorldLight().getDirectionalLight().getDirection().y = (float) Math.cos(angRad);
    }

    public Camera getCamera() {
        return camera;
    }
}
