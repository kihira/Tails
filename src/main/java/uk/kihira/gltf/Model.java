package uk.kihira.gltf;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4fStack;
import uk.kihira.gltf.animation.Animation;
import uk.kihira.tails.common.IDisposable;

import java.util.ArrayList;
import java.util.HashMap;

public class Model implements IDisposable
{
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

    public void render(Matrix4fStack matrixStack)
    {
        for (Node node : rootNodes)
        {
            node.render(matrixStack);
        }
    }

    public HashMap<String, Animation> getAnimations()
    {
        return animations;
    }

    public void dispose()
    {
        allNodes.forEach(Node::dispose);

        // Textures
        textures.forEach(texture -> Minecraft.getInstance().getTextureManager().release(texture));
        textures.clear();
    }
}