package com.salami.dragon.sandbox;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.render.ui.IUiLayer;
import imgui.ImGui;

public class UI implements IUiLayer {
    @Override
    public void imgui() {
        ImGui.begin("IMGUI WINDOW");

        if(ImGui.button("Quit")) {
            Application.stop();
        }

        ImGui.end();
    }
}
