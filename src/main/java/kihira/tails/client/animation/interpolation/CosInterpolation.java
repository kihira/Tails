package kihira.tails.client.animation.interpolation;

public class CosInterpolation implements IInterpolation {

    private final float start;
    private final float end;

    public CosInterpolation(float start, float end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public float getValue(float time) {
        float timeCos = (float) ((1f - Math.cos(time * Math.PI)) / 2f);
        return start * (1f - timeCos) + end * timeCos;
    }
}
