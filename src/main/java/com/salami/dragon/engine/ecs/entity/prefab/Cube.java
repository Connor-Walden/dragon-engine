package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.Material;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.OBJLoader;
import com.salami.dragon.engine.render.texture.Texture;

public class Cube {
    Entity cubeEntity;

    public Cube() throws Exception {
        Texture texture = new Texture("textures/rockwall.png");

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

    public void addComponent(String name, IComponent component) {
        cubeEntity.addComponent(name, component);
    }
}
