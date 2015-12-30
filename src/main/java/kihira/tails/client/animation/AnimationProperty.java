package kihira.tails.client.animation;

import net.minecraft.util.MathHelper;

public abstract class AnimationProperty {

    public abstract float update(float partialTicks);

    public static class CosProperty extends AnimationProperty {
        private final float offset;
        private final float multiplier;

        public CosProperty(float offset, float multiplier) {
            this.offset = offset;
            this.multiplier = multiplier;
        }

        @Override
        public float update(float partialTicks) {
            return MathHelper.cos(partialTicks + offset) * multiplier;
        }
    }

    public static class SinProperty extends AnimationProperty {
        private final float offset;
        private final float multiplier;

        public SinProperty(float offset, float multiplier) {
            this.offset = offset;
            this.multiplier = multiplier;
        }

        @Override
        public float update(float partialTicks) {
            return MathHelper.sin(partialTicks + offset) * multiplier;
        }
    }
}
