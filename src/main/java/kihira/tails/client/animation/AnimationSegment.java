package kihira.tails.client.animation;

import kihira.tails.client.animation.interpolation.IInterpolation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class AnimationSegment implements Comparable {
    private final AnimationVariable variable;
    public IInterpolation curve;
    public int startFrame;
    public int length;

    public AnimationSegment(AnimationVariable variable, IInterpolation curve, int startFrame, int length) {
        this.variable = variable;
        this.curve = curve;
        this.startFrame = startFrame;
        this.length = length;
    }

    public void animate(ModelRenderer model, float currFrame) {
        float time = MathHelper.clamp_float((currFrame - startFrame) / (float) length, 0f, 1f);
        float value = time;
        if (curve != null) {
            value = curve.getValue(time);
        }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnimationSegment segment = (AnimationSegment) o;

        if (startFrame != segment.startFrame) return false;
        if (length != segment.length) return false;
        if (variable != segment.variable) return false;
        return curve.equals(segment.curve);

    }

    @Override
    public int hashCode() {
        int result = variable.hashCode();
        result = 31 * result + curve.hashCode();
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
}
