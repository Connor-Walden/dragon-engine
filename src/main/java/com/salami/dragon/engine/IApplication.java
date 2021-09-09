package com.salami.dragon.engine;

import com.salami.dragon.engine.camera.Camera;
import com.salami.dragon.engine.render.Window;
import com.salami.dragon.engine.render.ui.IUiLayer;

public interface IApplication {
    // Dragon API methods to control window stuff.
    public int WIDTH();
    public int HEIGHT();
    public String TITLE();
    public Camera CAMERA();
    public Window.WindowOptions WINDOW_OPTIONS();
    public IUiLayer IMGUI_LAYER();

    public void init() throws Exception;
    public void tick(float delta);
}
