package kihira.tails.client.animation;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class AnimationSegment {

    public double[] rotChange;

    public BezierCurve curve;
    public int startFrame;
    public int length;

    public AnimationSegment(double[] rotChange, BezierCurve curve, int startFrame, int length) {
        this.rotChange = rotChange;
        this.curve = curve;
        this.startFrame = startFrame;
        this.length = length;
    }

    public void animate(ModelRenderer model, float currFrame) {
        float time = MathHelper.clamp_float((currFrame - startFrame) / (float) length,  0f, 1f);
        float value = time;
        if (curve != null) {
            value = curve.update(time).y;
        }

        for (int i = 0; i < 3; i++) {
            model.rotateAngleY = value;
        }
    }
}
