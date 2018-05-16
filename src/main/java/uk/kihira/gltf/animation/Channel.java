package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

import uk.kihira.gltf.NodeImpl;
import uk.kihira.gltf.spec.AnimationSampler;
import uk.kihira.gltf.spec.Accessor.Type;
import uk.kihira.gltf.spec.Animation.AnimationPath;

public class Channel {
    public final AnimationSampler sampler;
    public final Type outputType;
    public final FloatBuffer inputData;
    public final FloatBuffer outputData;
    public final NodeImpl node;
    public final AnimationPath path;
    public final float length;

    public Channel(AnimationSampler sampler, Type outputType, FloatBuffer inputData, FloatBuffer outputData, NodeImpl node, AnimationPath path) {
        this.sampler = sampler;
        this.outputType = outputType;
        this.inputData = inputData;
        this.outputData = outputData;
        this.node = node;
        this.path = path;
        this.length = inputData.get(inputData.limit());
    }
}