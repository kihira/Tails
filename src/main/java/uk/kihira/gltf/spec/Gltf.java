package uk.kihira.gltf.spec;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Gltf {

    /**
     * An array of accessors.  An accessor is a typed view into a bufferView.
     */
    @Nullable
    public ArrayList<Accessor> accessors;

    /**
     * An array of keyframe animations.
     */
    @Nullable
    public ArrayList<Animation> animations;

    /**
     * An array of bufferViews.
     * A bufferView is a view into a buffer generally representing a subset of the buffer.
     */
    @Nullable
    public ArrayList<BufferView> bufferViews;

    /**
     * An array of images.
     * An image defines data used to create a texture.
     */
    @Nullable
    public ArrayList<Image> images;

    /**
     * An array of nodes.
     */
    @Nullable
    public ArrayList<Node> nodes;


    @Nullable
    public Integer scene;

    @Nullable
    public ArrayList<Scene> scenes;

    /**
     * An array of textures.
     */
    @Nullable
    public ArrayList<Texture> textures;
}