package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

class LinearInterpolater extends Interpolater {

    public LinearInterpolater(AnimationPath path) {
        super(path);
    }

	@Override
	public float[] evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, float deltaTime) {
        float[] out = new float[prevFrameData.limit()];
        for (int i = 0; i < prevFrameData.limit(); i++) {
            out[i] = prevFrameData.get(i) + deltaTime * (nextFrameData.get(i) - prevFrameData.get(i));
        }
        return out;
	}

}