package kihira.tails.client.animation;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class AnimationSegment {

    public double[] rotChange;

    public BezierCurve curve;
    public int startFrame;
    public int length;

    public AnimationSegment() {

    }

    public void animate(ModelRenderer model, float currFrame) {
        float time = MathHelper.clamp_float((currFrame - startFrame) / (float) length,  0f, 1f);
        float value = time;
        if (curve != null) {
            value = curve.update(time).y;
        }

        for (int i = 0; i < 3; i++) {
            model.rotateAngleX += Math.toRadians(rotChange[i] * value);
            model.rotateAngleY += Math.toRadians(rotChange[i] * value);
            model.rotateAngleZ += Math.toRadians(rotChange[i] * value);
        }
    }
}
