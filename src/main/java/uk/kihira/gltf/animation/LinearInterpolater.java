package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

class LinearInterpolater extends Interpolater {

    public LinearInterpolater(AnimationPath path) {
        super(path);
    }

	@Override
	public FloatBuffer evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, FloatBuffer result, float deltaTime) {
        for (int i = 0; i < prevFrameData.limit(); i++) {
            result.put(i, prevFrameData.get(i) + deltaTime * (nextFrameData.get(i) - prevFrameData.get(i)));
        }
        return result;
	}

}