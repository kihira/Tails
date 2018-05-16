package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Animation {
    private final ArrayList<Channel> channels;

    private float currentAnimTime;

    public Animation(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public void run() {
        for (Channel channel : channels) {
            int prevKeyFrameIndex = 0, nextKeyFrameIndex = 1;
            float deltaTime;
            float[] interpolatedValues;

            // Find where we are on the track (prev and next keyframe positions)
            for (int i = 0; i < channel.inputData.limit() - 1; i++) {
                if (currentAnimTime > channel.inputData.get(i)
                        && currentAnimTime < channel.inputData.get(i + 1)) {
                    prevKeyFrameIndex = i;
                    nextKeyFrameIndex = i + 1;
                    break;
                }
            }
            deltaTime = (channel.inputData.get(nextKeyFrameIndex)-channel.inputData.get(prevKeyFrameIndex))/(currentAnimTime-channel.inputData.get(prevKeyFrameIndex));

            // note: BufferViews referenced by Accessor's won't have byteStride as per spec
            FloatBuffer prevFrameData = (FloatBuffer) channel.outputData.slice().position(prevKeyFrameIndex * channel.outputType.size).limit(channel.outputType.size);
            FloatBuffer nextFrameData = (FloatBuffer) channel.outputData.slice().position(nextKeyFrameIndex * channel.outputType.size).limit(channel.outputType.size);

            // Do the interpolation
            switch (channel.sampler.interpolation) {
            case CUBICSPLINE:
                interpolatedValues = Interpolators.CUBIC.evaluate(prevFrameData, nextFrameData, deltaTime);
                break;
            case STEP:
                interpolatedValues = Interpolators.STEP.evaluate(prevFrameData, nextFrameData, deltaTime);
                break;
            case LINEAR:
                if (channel.path == AnimationPath.ROTATION) {
                    interpolatedValues = Interpolators.SLERP.evaluate(prevFrameData, nextFrameData, deltaTime);
                } else {
                    interpolatedValues = Interpolators.LINEAR.evaluate(prevFrameData, nextFrameData, deltaTime);
                }
                break;
            default:
                throw new RuntimeException("Unknown interpolation method");
            }

            // Apply the interpolated values to the node
            switch (channel.path) {
            case TRANSLATION:
                channel.node.translation.set(interpolatedValues[0], interpolatedValues[1], interpolatedValues[2]);
                break;
            case ROTATION:
                channel.node.rotation.set(interpolatedValues[0], interpolatedValues[1], interpolatedValues[2], interpolatedValues[3]);
                break;
            case SCALE:
                channel.node.scale.set(interpolatedValues[0], interpolatedValues[1], interpolatedValues[2]);
                break;
            default:
                break;
            }
        }
    }
}