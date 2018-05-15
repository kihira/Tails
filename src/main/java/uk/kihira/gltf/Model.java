package uk.kihira.gltf;

import java.util.ArrayList;

import uk.kihira.gltf.animation.Animation;

public class Model {
    public final ArrayList<NodeImpl> allNodes;
    private ArrayList<NodeImpl> rootNodes;
    private ArrayList<Animation> animations;

    public Model(ArrayList<NodeImpl> allNodes, ArrayList<Integer> rootNodes) {
        this.allNodes = allNodes;
        this.rootNodes = allNodes; // todo get root nodes
    }

    public void render() {
        for (NodeImpl node : rootNodes) {
            node.render();
        }
    }
}