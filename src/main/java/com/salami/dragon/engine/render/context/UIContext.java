package com.salami.dragon.engine.render.context;

import com.salami.dragon.engine.Application;
import com.salami.dragon.engine.World;
import com.salami.dragon.engine.ecs.entity.Transformation;
import com.salami.dragon.engine.render.ShadowMap;
import com.salami.dragon.engine.render.ui.IUiLayer;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class UIContext implements IContext {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private IUiLayer uiLayer;

    @Override
    public void setup() throws Exception {
        uiLayer = Application.getWindow().getUiLayer();

        // Setup imgui
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imGuiGlfw.init(Application.getWindow().getGLFWWindow(), true);
        imGuiGl3.init(null);
    }

    @Override
    public void render(World world, Transformation transformation, ShadowMap shadowMap) {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        uiLayer.imgui();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindow = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindow);
        }
    }

    @Override
    public void cleanUp() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
}
