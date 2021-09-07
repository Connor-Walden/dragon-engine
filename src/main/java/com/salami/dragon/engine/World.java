package com.salami.dragon.engine;

import com.salami.dragon.engine.ecs.entity.Entity;
import com.salami.dragon.engine.ecs.entity.prefab.SkyBox;
import com.salami.dragon.engine.light.DirectionalLight;
import com.salami.dragon.engine.light.WorldLight;
import com.salami.dragon.engine.render.Fog;
import com.salami.dragon.engine.render.mesh.Mesh;
import com.salami.dragon.engine.render.particle.IParticleEmitter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {
    private final Map<Mesh, List<Entity>> meshMap;
    private IParticleEmitter[] particleEmitters;

    private SkyBox skyBox;
    private WorldLight worldLight;

    float sunAngle;
    boolean doDaylightCycle = true;

    Fog fog;

    public World() throws Exception {
        meshMap = new HashMap<>();

        setupWorldLight();

        // Default skybox
        skyBox = new SkyBox("/models/skybox.obj", "textures/skybox.png");
        skyBox.setScale(500.0f);
    }

    private void setupWorldLight() {
        worldLight = new WorldLight();
        worldLight.setAmbientLight(new Vector3f(0.15f, 0.15f, 0.15f));
        worldLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1.0f));
        worldLight.getDirectionalLight().setShadowPosMult(5);
        worldLight.getDirectionalLight().setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        worldLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));
        sunAngle = 0.0f;
    }

    public Map<Mesh, List<Entity>> getGameMeshes() {
        return meshMap;
    }

    public void setEntities(Entity[] entities) {
        int numEntities = entities != null ? entities.length : 0;
        for (int i=0; i<numEntities; i++) {
            Entity entity = entities[i];

            Mesh mesh = entity.getMesh();

            List<Entity> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());

            list.add(entity);
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public WorldLight getWorldLight() {
        return worldLight;
    }

    public void setWorldLight(WorldLight worldLight) {
        this.worldLight = worldLight;
    }

    public float getSunAngle() {
        return sunAngle;
    }

    public void setSunAngle(float newAngle) {
        sunAngle = newAngle;
    }

    public boolean getDoDaylightCycle() {
        return doDaylightCycle;
    }

    public void setDoDaylightCycle(boolean doDaylightCycle) {
        this.doDaylightCycle = doDaylightCycle;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] newArr) {
        particleEmitters = newArr;
    }
}
