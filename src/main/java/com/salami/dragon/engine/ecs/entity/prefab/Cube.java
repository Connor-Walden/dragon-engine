package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.Material;
import com.salami.dragon.engine.render.Mesh;
import com.salami.dragon.engine.render.OBJLoader;
import com.salami.dragon.engine.render.Texture;

public class Cube {
    Entity cubeEntity;

    public Cube() throws Exception {
        Texture texture = new Texture("rockwall.png");
        texture.loadToGL();

        Material material = new Material();
        material.setTexture(texture);

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        mesh.setMaterial(material);

        cubeEntity = new Entity(mesh);
    }

    public Cube setScale(float scale) {
        cubeEntity.setScale(scale);

        return this;
    }

    public Cube setPosition(int x, int y, int z) {
        cubeEntity.setPosition(x, y, z);

        return this;
    }

    public Entity getCubeEntity() {
        return cubeEntity;
    }
}
