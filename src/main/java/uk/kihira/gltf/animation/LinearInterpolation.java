package uk.kihira.gltf.animation;

import uk.kihira.gltf.spec.Animation.Path;

class LinearInterpolation extends Interpolation {

    public LinearInterpolation(Path path) {
        super(path);
    }

	@Override
	public float[] evaluate(float[] prevFrameData, float[] nextFrameData, float deltaTime) {
        float[] out = new float[prevFrameData.length];
        for (int i = 0; i < prevFrameData.length; i++) {
            out[i] = prevFrameData[i] + deltaTime * (nextFrameData[i] - prevFrameData[i]);
        }
        return out;
	}

}