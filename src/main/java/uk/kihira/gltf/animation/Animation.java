package uk.kihira.gltf.animation;

import uk.kihira.gltf.Model;
import uk.kihira.gltf.NodeImpl;
import uk.kihira.gltf.spec.Accessor.Type;
import uk.kihira.gltf.spec.Animation.Channel;
import uk.kihira.gltf.spec.Animation.Path;
import uk.kihira.gltf.spec.AnimationSampler;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Animation {
    private Model model;
    private ArrayList<ChannelImpl> channels;

    private float currentAnimTime;

    public Animation(Model model, ArrayList<Channel> channels, ArrayList<AnimationSampler> samplers) {
        this.model = model;

        for (Channel channel : channels) {
            ChannelImpl channelImpl = new ChannelImpl();
            channelImpl.sampler = samplers.get(channel.sampler);
            // channelImpl.outputType; todo channelImpl.sampler.output
            // channelImpl.inputData
            // channelImpl.outputData =
            channelImpl.node = model.allNodes.get(channel.target.node);
            channelImpl.path = channel.target.path;
        }
    }

    public void run() {
        for (ChannelImpl channel : channels) {
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
                if (channel.path == Path.ROTATION) {
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

    private class ChannelImpl {
        AnimationSampler sampler;
        Type outputType;
        FloatBuffer inputData;
        FloatBuffer outputData;
        NodeImpl node;
        Path path;
    }
}