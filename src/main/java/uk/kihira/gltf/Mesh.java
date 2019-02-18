package uk.kihira.gltf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.common.IDisposable;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class Mesh implements IDisposable {
    private ArrayList<Geometry> geometries;

    public Mesh(ArrayList<Geometry> geometries) {
        this.geometries = geometries;
    }

    public void render() {
        for (Geometry geometry : geometries) {
            geometry.render();
        }
    }

    @Override
    public void dispose() {
        geometries.forEach(Geometry::dispose);
    }
}