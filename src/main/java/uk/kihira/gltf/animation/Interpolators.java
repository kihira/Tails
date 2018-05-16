package uk.kihira.gltf.animation;

class Interpolators {
    public static final CubicInterpolater CUBIC = new CubicInterpolater(null);
    public static final StepInterpolater STEP = new StepInterpolater(null);
    public static final LinearInterpolater LINEAR = new LinearInterpolater(null);
    public static final SlerpInterpolater SLERP = new SlerpInterpolater(null);
}