package uk.kihira.gltf;

import java.util.ArrayList;

public class Mesh {
    private ArrayList<Geometry> geometries;

    public Mesh(ArrayList<Geometry> geometries) {
        this.geometries = geometries;
    }

    public void render() {
        for (Geometry geometry : geometries) {
            geometry.render();
        }
    }
}