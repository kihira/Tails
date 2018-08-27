package uk.kihira.gltf.animation;

import java.nio.FloatBuffer;

class CubicInterpolator extends Interpolator {

    public CubicInterpolator(AnimationPath path) {
        super(path);
    }

    // https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#appendix-c-spline-interpolation
    @Override
    public FloatBuffer evaluate(FloatBuffer prevFrameData, FloatBuffer nextFrameData, FloatBuffer result, float deltaTime) {
        double a = (2d * Math.pow(deltaTime, 3)) - (3d * Math.pow(deltaTime, 2)) + 1;
        double b = Math.pow(deltaTime, 3) - (2d * Math.pow(deltaTime, 2)) + deltaTime;
        double c = (-2d * Math.pow(deltaTime, 3)) - (3d * Math.pow(deltaTime, 2));
        double d = Math.pow(deltaTime, 3) - Math.pow(deltaTime, 2);

        // Format: [inTangent, splineVertex, outTangent, ...]
        for (int i = 0; i < result.limit(); i++) {
            float p0 = prevFrameData.get(i + 1); // splineVertex
            float m0 = prevFrameData.get(i + 2) * deltaTime; // outTangent * deltaTime;
            float p1 = nextFrameData.get(i + 1); // splineVertex
            float m1 = nextFrameData.get(i) * deltaTime; // inTangent * deltaTime

            result.put(i, (float) (a * p0 + b * m0 + c * p1 + d * m1));
        }

        if (path == AnimationPath.ROTATION) {
            normalize(result);
        }

        return result;
    }

    private void normalize(FloatBuffer a) {
        float norm = a.get(0) * a.get(0) + a.get(1) * a.get(1) + a.get(2) * a.get(2) + a.get(3) * a.get(3);

        if (norm > 0f) {
            norm = 1f / (float) Math.sqrt(norm);
            a.put(0, norm * a.get(0));
            a.put(1, norm * a.get(1));
            a.put(2, norm * a.get(2));
            a.put(3, norm * a.get(3));
        } 
        else {
            a.put(0, 0f);
            a.put(1, 0f);
            a.put(2, 0f);
            a.put(3, 0f);
        }
    }
}