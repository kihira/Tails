package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

import uk.kihira.gltf.spec.Animation.AnimationPath;

/**
 * Used only when path is rotation (quaternion)
 */
class SlerpInterpolation extends Interpolation {

    private static final float E = 1e-6f;

    public SlerpInterpolation(AnimationPath path) {
        super(path);
    }

	@Override
	public float[] evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, float deltaTime) {
        double dot = dot(prevFrameData, nextFrameData);
        if (dot < 0) {
            dot = -dot;
            nextFrameData.put(0, -nextFrameData.get(0));
            nextFrameData.put(1, -nextFrameData.get(1));
            nextFrameData.put(2, -nextFrameData.get(2));
            nextFrameData.put(3, -nextFrameData.get(3));
        }

        double s1, s2;
        if (1f - dot > E) {
            double omega = Math.acos(dot);
            double sinOmega = Math.sin(omega);
            s1 = Math.sin((1f - deltaTime) * omega) / sinOmega;
            s2 = Math.sin(deltaTime * omega) / sinOmega;
        }
        else {
            s1 = 1f - deltaTime;
            s2 = deltaTime;
        }

        float[] out = new float[]{
            (float)(s1*prevFrameData.get(0)+s2*nextFrameData.get(0)),
            (float)(s1*prevFrameData.get(1)+s2*nextFrameData.get(1)),
            (float)(s1*prevFrameData.get(2)+s2*nextFrameData.get(2)),
            (float)(s1*prevFrameData.get(3)+s2*nextFrameData.get(3))
        };
        return out;
	}

    private float dot(FloatBuffer a, FloatBuffer b) {
        return a.get(0) * b.get(0) + a.get(1) * b.get(1) + a.get(2) * b.get(2) + a.get(3) * b.get(3);
    }
}