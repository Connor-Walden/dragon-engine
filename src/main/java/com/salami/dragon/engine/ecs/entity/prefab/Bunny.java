package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.Material;
import com.salami.dragon.engine.render.Mesh;
import com.salami.dragon.engine.render.OBJLoader;
import com.salami.dragon.engine.render.Texture;

public class Bunny {
    Entity bunnyEntity;

    public Bunny() throws Exception {
        Material material = new Material();

        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        mesh.setMaterial(material);

        bunnyEntity = new Entity(mesh);
    }

    public Bunny setScale(float scale) {
        bunnyEntity.setScale(scale);

        return this;
    }

    public Bunny setPosition(int x, int y, int z) {
        bunnyEntity.setPosition(x, y, z);

        return this;
    }

    public Entity getBunnyEntity() {
        return bunnyEntity;
    }
}
