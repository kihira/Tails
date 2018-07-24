package uk.kihira.gltf;

import uk.kihira.gltf.animation.Animation;

import java.util.ArrayList;

public class Model {
    public final ArrayList<Node> allNodes;
    private ArrayList<Node> rootNodes;
    private ArrayList<Animation> animations;

    public Model(ArrayList<Node> allNodes, ArrayList<Node> rootNodes, ArrayList<Animation> animations) {
        this.allNodes = allNodes;
        this.rootNodes = rootNodes;
        this.animations = animations;
    }

    public void render() {
        for (Node node : rootNodes) {
            node.render();
        }
    }
}