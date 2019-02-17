package uk.kihira.gltf.animation;

import net.minecraft.client.renderer.Quaternion;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Animation {
    private final ArrayList<Channel> channels;

    private FloatBuffer interpolatedValues; // todo per channel instead?
    private float currentAnimTime;

    public Animation(ArrayList<Channel> channels) {
        this.channels = channels;
        interpolatedValues = BufferUtils.createFloatBuffer(4);
    }

    public void run() {
        for (Channel channel : channels) {
            int prevKeyFrameIndex = 0, nextKeyFrameIndex = 1;
            float deltaTime;

            // Find where we are on the track (prev and next keyframe positions)
            for (int i = 0; i < channel.inputData.limit() - 1; i++) {
                if (currentAnimTime > channel.inputData.get(i) && currentAnimTime < channel.inputData.get(i + 1)) {
                    prevKeyFrameIndex = i;
                    nextKeyFrameIndex = i + 1;
                    break;
                }
            }
            deltaTime = (channel.inputData.get(nextKeyFrameIndex) - channel.inputData.get(prevKeyFrameIndex)) / (currentAnimTime - channel.inputData.get(prevKeyFrameIndex));

            // note: BufferViews referenced by Accessor's won't have byteStride as per spec
            FloatBuffer prevFrameData = (FloatBuffer) channel.outputData.slice().position(prevKeyFrameIndex * channel.outputType.size).limit(channel.outputType.size);
            FloatBuffer nextFrameData = (FloatBuffer) channel.outputData.slice().position(nextKeyFrameIndex * channel.outputType.size).limit(channel.outputType.size);

            // Do the interpolation
            switch (channel.sampler.interpolation) {
                case CUBICSPLINE:
                    Interpolators.CUBIC.evaluate(prevFrameData, nextFrameData, interpolatedValues, deltaTime);
                    break;
                case STEP:
                    Interpolators.STEP.evaluate(prevFrameData, nextFrameData, interpolatedValues, deltaTime);
                    break;
                case LINEAR:
                    if (channel.path == AnimationPath.ROTATION) {
                        Interpolators.SLERP.evaluate(prevFrameData, nextFrameData, interpolatedValues, deltaTime);
                    } else {
                        Interpolators.LINEAR.evaluate(prevFrameData, nextFrameData, interpolatedValues, deltaTime);
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown interpolation method");
            }

            // Apply the interpolated values to the node
            switch (channel.path) {
                case TRANSLATION:
                    channel.node.translation.set(interpolatedValues.get(0), interpolatedValues.get(1), interpolatedValues.get(2));
                    break;
                case ROTATION:
                    channel.node.rotation = new Quaternion(interpolatedValues.get(0), interpolatedValues.get(1), interpolatedValues.get(2), interpolatedValues.get(3));
                    break;
                case SCALE:
                    channel.node.scale.set(interpolatedValues.get(0), interpolatedValues.get(1), interpolatedValues.get(2));
                    break;
                default:
                    break;
            }
        }
    }
}