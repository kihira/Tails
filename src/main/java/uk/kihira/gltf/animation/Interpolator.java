package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

public abstract class Interpolator {
    public final AnimationPath path;

    public Interpolator(AnimationPath path) {
        this.path = path;
    }

    /**
     * TODO: Look into passing ByteBuffers instead as not mutating? Would also need to pay attention to accessor.componentType in that case
     * 
     * @param prevFrameData The previous key frame data (from the output accessor)
     * @param nextFrameData The next key frame data (from the output accessor)
     * @param result
     * @param deltaTime The normalised time between the previous key frame and the next one
     * @return The resulting interpolated value
     */
    public abstract FloatBuffer evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, FloatBuffer result, float deltaTime);
}