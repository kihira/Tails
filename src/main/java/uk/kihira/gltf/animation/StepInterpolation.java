package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

import uk.kihira.gltf.spec.Animation.AnimationPath;

class StepInterpolation extends Interpolation {

    public StepInterpolation(AnimationPath path) {
        super(path);
    }

	@Override
	public float[] evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, float deltaTime) {
        return prevFrameData; // TODO
	}
}