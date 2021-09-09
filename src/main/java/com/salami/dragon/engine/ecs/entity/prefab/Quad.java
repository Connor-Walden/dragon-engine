package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.Material;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.texture.Texture;

import java.io.IOException;

public class Quad extends Entity {
    float[] vertices = new float[] {
        -1f, -1f, 0.0f,
        -1f, 1f, 0.0f,
        1f, 1f, 0.0f,
        1f, -1f, 0.0f
    };

    int[] indices = new int[] {
        0, 2, 1,
        0, 3, 2
    };

    float[] normals = new float[] { 1.0f };

    float[] textCoords = new float[] {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    public Quad() throws IOException {
        setMesh(new Mesh(vertices,textCoords,normals,indices));
        getMesh().setMaterial(new Material(new Texture("textures/rockwall.png")));
    }
}
