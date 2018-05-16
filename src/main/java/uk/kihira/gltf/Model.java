package uk.kihira.gltf;

import java.util.ArrayList;

import uk.kihira.gltf.animation.Animation;

public class Model {
    public final ArrayList<Node> allNodes;
    private ArrayList<Node> rootNodes;
    private ArrayList<Animation> animations;

    public Model(ArrayList<Node> allNodes, ArrayList<Node> rootNodes, ArrayList<Animation> animations) {
        this.allNodes = allNodes;
        this.rootNodes = allNodes;
        this.animations = animations;
    }

    public void render() {
        for (Node node : rootNodes) {
            node.render();
        }
    }
}