package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

import uk.kihira.gltf.Node;
import uk.kihira.gltf.spec.Accessor.Type;

public class Channel {
    public final Sampler sampler;
    public final Type outputType;
    public final FloatBuffer inputData;
    public final FloatBuffer outputData;
    public final Node node;
    public final AnimationPath path;
    public final float length;

    public Channel(Sampler sampler, Type outputType, FloatBuffer inputData, FloatBuffer outputData, Node node, AnimationPath path) {
        this.sampler = sampler;
        this.outputType = outputType;
        this.inputData = inputData;
        this.outputData = outputData;
        this.node = node;
        this.path = path;
        this.length = inputData.get(inputData.limit());
    }
}