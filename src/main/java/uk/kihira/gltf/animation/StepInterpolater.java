package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

class StepInterpolater extends Interpolater {

    public StepInterpolater(AnimationPath path) {
        super(path);
    }

	@Override
	public FloatBuffer evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, FloatBuffer result, float deltaTime) {
        result = prevFrameData;
        return result;
	}
}