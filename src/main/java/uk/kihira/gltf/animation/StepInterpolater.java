package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

class StepInterpolater extends Interpolater {

    public StepInterpolater(AnimationPath path) {
        super(path);
    }

	@Override
	public float[] evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, float deltaTime) {
        return prevFrameData; // TODO
	}
}