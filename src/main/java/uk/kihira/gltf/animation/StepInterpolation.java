package uk.kihira.gltf.animation;

import uk.kihira.gltf.spec.Animation.Path;

class StepInterpolation extends Interpolation {

    public StepInterpolation(Path path) {
        super(path);
    }

	@Override
	public float[] evaluate(float[] prevFrameData, float[] nextFrameData, float deltaTime) {
        return prevFrameData;
	}
}