package uk.kihira.gltf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * A keyframe animation.
 */
public class Animation {
    /**
     * An array of channels, each of which targets an animation's sampler at a node's property.
     * Different channels of the same animation can't have equal targets.
     */
    @Nonnull
    public ArrayList<Channel> channels;

    /**
     * An array of samplers that combines input and output accessors with an interpolation algorithm to define a keyframe graph (but not its target).
     */
    @Nonnull
    public ArrayList<AnimationSampler> samplers;

    /**
     * Targets an animation's sampler at a node's property.v
     */
    public static class Channel {
        /**
         * The index of a sampler in this animation used to compute the value for the target, e.g., a node's translation, rotation, or scale (TRS).
         */
        public int sampler = 0;

        /**
         * The index of the node and TRS property to target.
         */
        @Nonnull
        public ChannelTarget target;
    }

    /**
     * The index of the node and TRS property that an animation channel targets.
     */
    public static class ChannelTarget {
        /**
         * The index of the node to target.
         */
        @Nullable
        public Integer node;

        /**
         * The name of the node's TRS property to modify, or the "weights" of the Morph Targets it instantiates.
         * For the "translation" property, the values that are provided by the sampler are the translation along the x, y, and z axes.
         * For the "rotation" property, the values are a quaternion in the order (x, y, z, w), where w is the scalar.
         * For the "scale" property, the values are the scaling factors along the x, y, and z axes.
         */
        @Nonnull
        public String path = "";
    }
}
