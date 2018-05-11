package uk.kihira.gltf.animation;

import uk.kihira.gltf.spec.Animation.Path;

/**
 * Used only when path is rotation (quaternion)
 */
class SlerpInterpolation extends Interpolation {

    private static final float E = 1e-6f;

    public SlerpInterpolation(Path path) {
        super(path);
    }

	@Override
	public float[] evaluate(float[] prevFrameData, float[] nextFrameData, float deltaTime) {
        double dot = dot(prevFrameData, nextFrameData);
        if (dot < 0) {
            dot = -dot;
            nextFrameData[0] = -nextFrameData[0];
            nextFrameData[1] = -nextFrameData[1];
            nextFrameData[2] = -nextFrameData[2];
            nextFrameData[3] = -nextFrameData[3];
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
            (float)(s1*prevFrameData[0]+s2*nextFrameData[0]),
            (float)(s1*prevFrameData[1]+s2*nextFrameData[1]),
            (float)(s1*prevFrameData[2]+s2*nextFrameData[2]),
            (float)(s1*prevFrameData[3]+s2*nextFrameData[3])
        };
        return out;
	}

    private float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3];
    }
}