package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.ecs.component.IComponent;
import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.Material;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.OBJLoader;

public class Bunny extends Entity {
    public Bunny(Mesh mesh) throws Exception {
        super(mesh);
    }

    public static Bunny createBunny() throws Exception {
        Material material = new Material();

        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        mesh.setMaterial(material);

        return new Bunny(mesh);
    }
}
