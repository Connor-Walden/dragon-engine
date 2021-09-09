package com.salami.dragon.engine.ecs.entity.prefab;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.render.Material;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.OBJLoader;
import com.salami.dragon.engine.render.texture.Texture;

public class Cube extends Entity {

    public static final String DEFAULT_CUBE_TEXTURE = "textures/rockwall.png";

    public Cube(Mesh mesh) throws Exception {
        super(mesh);
    }

    public static Cube createCube(String texLoc) throws Exception {
        Texture texture = new Texture(texLoc);
        Material material = new Material();
        material.setTexture(texture);

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        mesh.setMaterial(material);

        return new Cube(mesh);
    }

    public static Cube createCube() throws Exception {
        Texture texture = new Texture(DEFAULT_CUBE_TEXTURE);
        Material material = new Material();
        material.setTexture(texture);

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        mesh.setMaterial(material);

        return new Cube(mesh);
    }
}
