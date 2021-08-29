package com.salami.dragon.sandbox;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.prefab.TextItem;
import com.salami.dragon.engine.render.IHud;
import com.salami.dragon.engine.render.Window;
import org.joml.Vector4f;

public class HUD implements IHud {
    private static final int FONT_COLS = 16;

    private static final int FONT_ROWS = 16;

    private static final String FONT_TEXTURE = "textures/font_texture.png";

    private final Entity[] entities;

    private final TextItem statusTextItem;

    public HUD(String statusText) throws Exception {
        this.statusTextItem = new TextItem(statusText, FONT_TEXTURE, FONT_COLS, FONT_ROWS);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        entities = new Entity[]{statusTextItem};
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    @Override
    public Entity[] getEntities() {
        return entities;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
    }
}