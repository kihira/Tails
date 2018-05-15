package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

import uk.kihira.gltf.spec.Animation.Path;

class StepInterpolation extends Interpolation {

    public StepInterpolation(Path path) {
        super(path);
    }

	@Override
	public float[] evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, float deltaTime) {
        return prevFrameData; // TODO
	}
}