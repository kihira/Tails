package kihira.tails.client.animation;

import kihira.tails.client.animation.interpolation.IInterpolation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class AnimationSegment implements Comparable {
    public final Variable variable;
    protected IInterpolation interpolation;
    public final int startFrame;
    public final int length;

    protected int playTime;

    public AnimationSegment(Variable variable, int startFrame, int length, IInterpolation interpolation) {
        this.variable = variable;
        this.interpolation = interpolation;
        this.startFrame = startFrame;
        this.length = length;
    }

    public void animate(ModelRenderer model, float partialTicks) {
        float time = MathHelper.clamp_float((playTime + partialTicks) / (float) length, 0f, 1f);
        float value = interpolation.getValue(time);

        // TODO modify off the base variable
        switch (variable) {
            case POSX:
                model.rotationPointX = value;
                break;
            case POSY:
                model.rotationPointY = value;
                break;
            case POSZ:
                model.rotationPointZ = value;
                break;
            case ROTX:
                model.rotateAngleX = value;
                break;
            case ROTY:
                model.rotateAngleY = value;
                break;
            case ROTZ:
                model.rotateAngleZ = value;
                break;
        }
    }

    /**
     * Called once per tick whilst this segment is active
     * @return Whether segment is complete
     */
    public boolean update() {
        playTime++;
        if (playTime > length) {
            playTime = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnimationSegment segment = (AnimationSegment) o;

        if (startFrame != segment.startFrame) return false;
        if (length != segment.length) return false;
        if (variable != segment.variable) return false;
        return interpolation.equals(segment.interpolation);

    }

    @Override
    public int hashCode() {
        int result = variable.hashCode();
        result = 31 * result + interpolation.hashCode();
        result = 31 * result + startFrame;
        result = 31 * result + length;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (equals(o)) {
            return 0;
        }
        else if (o instanceof AnimationSegment) {
            AnimationSegment other = (AnimationSegment) o;
            if (startFrame > other.startFrame) {
                return 1;
            }
            else if (other.startFrame == startFrame) {
                return 0;
            }
            else {
                return -1;
            }
        }
        return 0;
    }

    public static class Blank extends AnimationSegment {

        public Blank(int startFrame, int length) {
            super(null, startFrame, length, null);
        }

        @Override
        public void animate(ModelRenderer model, float partialTicks) {}
    }
}
