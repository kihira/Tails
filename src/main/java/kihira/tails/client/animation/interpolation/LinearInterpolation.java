package kihira.tails.client.animation.interpolation;

public class LinearInterpolation implements IInterpolation {

    private final float start;
    private final float end;

    public LinearInterpolation(float start, float end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public float getValue(float time) {
        return start * (1f - time) + end * time;
    }
}
