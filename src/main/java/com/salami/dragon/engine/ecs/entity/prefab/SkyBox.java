package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.Material;
import com.salami.dragon.engine.render.Mesh;
import com.salami.dragon.engine.render.OBJLoader;
import com.salami.dragon.engine.render.texture.Texture;

public class SkyBox extends Entity {
    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxtexture = new Texture(textureFile);

        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));

        setMesh(skyBoxMesh);

        setPosition(0, 0, 0);
    }
}
