package uk.kihira.gltf;

import net.minecraft.util.ResourceLocation;
import uk.kihira.gltf.animation.Animation;

import java.util.ArrayList;

public class Model {
    public final ArrayList<Node> allNodes;
    private final ArrayList<Node> rootNodes;
    private final ArrayList<Animation> animations;
    private final ArrayList<ResourceLocation> textures;

    public Model(ArrayList<Node> allNodes, ArrayList<Node> rootNodes, ArrayList<Animation> animations, ArrayList<ResourceLocation> textures) {
        this.allNodes = allNodes;
        this.rootNodes = rootNodes;
        this.animations = animations;
        this.textures = textures;
    }

    public void render() {
        for (Node node : rootNodes) {
            node.render();
        }
    }
}