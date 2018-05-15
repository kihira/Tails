package uk.kihira.gltf.animation;

import uk.kihira.gltf.spec.Animation.Path;

import java.nio.FloatBuffer;

class LinearInterpolation extends Interpolation {

    public LinearInterpolation(Path path) {
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