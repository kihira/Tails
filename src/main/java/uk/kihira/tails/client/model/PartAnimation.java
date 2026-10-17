package uk.kihira.tails.client.model;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import uk.kihira.tails.Tails;

public class PartAnimation
{
    public static MotionAngles getMotionAngles(AbstractClientPlayer player, float partialTick)
    {
        var avatar = player.avatarState();

        var xMotion = avatar.getInterpolatedCloakX(partialTick) - Mth.lerp(partialTick, player.xo, player.getX());
        var yMotion = avatar.getInterpolatedCloakY(partialTick) - Mth.lerp(partialTick, player.yo, player.getY()); //Positive when falling, negative when climbing
        var zMotion = avatar.getInterpolatedCloakZ(partialTick) - Mth.lerp(partialTick, player.zo, player.getZ());

        var bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        //Pretty sure renderYawOffset is actually the way the body is "pointing"
        //In degrees, not bound 0-360, be warned!
        var bodyYawSin = Mth.sin(bodyYaw * Mth.PI / 180f);
        var bodyYawCos = -Mth.cos(bodyYaw * Mth.PI / 180f);
        float xOffset = Mth.clamp((float) yMotion * 10f, -6f, 32f);
        float f1 = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100f;
        float f2 = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100f;

        if (f1 < 0f) {
            f1 = 0f;
        }

        return new MotionAngles(
                (float) Math.toRadians(f1 / 2.5f + (xOffset + getTailBob(player, partialTick))),
                (float) Math.toRadians(-f2 / 20f),
                (float) Math.toRadians(f2 / 2f)
        );
    }

    public static float getTailBob(AbstractClientPlayer player, float partialTick)
    {
        var avatar = player.avatarState();
        var cameraYaw = avatar.getInterpolatedBob(partialTick);
        var walkDistance = avatar.getInterpolatedWalkDistance(partialTick);

        return Mth.sin(walkDistance * 6f) * 12f * cameraYaw;
    }

    public record MotionAngles(float xAngle, float yAngle, float zAngle) {}

    public static final ContextKey<MotionAngles> MOTION_KEY = new ContextKey<>(Identifier.fromNamespaceAndPath(Tails.MOD_ID, "motion_angles"));
}
