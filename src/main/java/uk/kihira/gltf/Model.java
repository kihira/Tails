package uk.kihira.gltf;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import uk.kihira.gltf.animation.Animation;
import uk.kihira.tails.common.IDisposable;

import java.util.ArrayList;
import java.util.HashMap;

public class Model implements IDisposable {
    private final ArrayList<Node> allNodes;
    private final ArrayList<Node> rootNodes;
    private final HashMap<String, Animation> animations;
    private final ArrayList<ResourceLocation> textures;

    public Model(ArrayList<Node> allNodes, ArrayList<Node> rootNodes, HashMap<String, Animation> animations, ArrayList<ResourceLocation> textures) {
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

    public void dispose() {
        allNodes.forEach(Node::dispose);

        // Textures
        textures.forEach(texture -> Minecraft.getMinecraft().getTextureManager().deleteTexture(texture));
        textures.clear();
    }
}