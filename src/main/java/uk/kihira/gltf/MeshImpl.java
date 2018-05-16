package uk.kihira.gltf;

import java.util.ArrayList;

public class MeshImpl {
    private ArrayList<Geometry> geometries;

    public MeshImpl(ArrayList<Geometry> geometries) {
        this.geometries = geometries;
    }

    public void render() {
        for (Geometry geometry : geometries) {
            geometry.render();
        }
    }
}