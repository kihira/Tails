package uk.kihira.gltf.animation;

import uk.kihira.gltf.spec.Animation.AnimationPath;

import java.nio.FloatBuffer;

public abstract class Interpolation {
    public final AnimationPath path;

    public Interpolation(AnimationPath path) {
        this.path = path;
    }

    /**
     * TODO: Look into passing ByteBuffers instead as not mutating? Would also need to pay attention to accessor.componentType in that case
     * 
     * @param prevFrameData The previous key frame data (from the output accessor)
     * @param nextFrameData The next key frame data (from the output accessor)
     * @param deltaTime The normalised time between the previous key frame and the next one
     * @return The resulting interpolated value
     */
    public abstract float[] evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, float deltaTime);
}