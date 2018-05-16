package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

class CubicInterpolater extends Interpolater {

    public CubicInterpolater(AnimationPath path) {
        super(path);
    }

    // https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#appendix-c-spline-interpolation
    @Override
    public float[] evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, float deltaTime) {
        double a = (2d * Math.pow(deltaTime, 3)) - (3d * Math.pow(deltaTime, 2)) + 1;
        double b = Math.pow(deltaTime, 3) - (2d * Math.pow(deltaTime, 2)) + deltaTime;
        double c = (-2d * Math.pow(deltaTime, 3)) - (3d * Math.pow(deltaTime, 2));
        double d = Math.pow(deltaTime, 3) - Math.pow(deltaTime, 2);

        float[] result = new float[prevFrameData.limit() / 3];
        // Format: [inTangent, splineVertex, outTangent, ...]
        for (int i = 0; i < result.length; i++) {
            float p0 = prevFrameData.get(i + 1); // splineVertex
            float m0 = prevFrameData.get(i + 2) * deltaTime; // outTangent * deltaTime;
            float p1 = nextFrameData.get(i + 1); // splineVertex
            float m1 = nextFrameData.get(i) * deltaTime; // inTangent * deltaTime

            result[i] = (float) (a * p0 + b * m0 + c * p1 + d * m1);
        }

        if (path == AnimationPath.ROTATION) {
            result = normalize(result);
        }

        return result;
    }

    private float[] normalize(float[] a) {
        float norm = a[0] * a[0] + a[1] * a[1] + a[2] * a[2] + a[3] * a[3];

        if (norm > 0f) {
            norm = 1f / (float) Math.sqrt(norm);
            a[0] = norm * a[0];
            a[1] = norm * a[1];
            a[2] = norm * a[2];
            a[3] = norm * a[3];
        } 
        else {
            a[0] = 0f;
            a[1] = 0f;
            a[2] = 0f;
            a[3] = 0f;
        }

        return a;
    }
}