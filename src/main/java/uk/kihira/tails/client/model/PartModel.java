package uk.kihira.tails.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.function.Function;

// ties us to only rendering on players but should be fine
public abstract class PartModel extends HumanoidModel<AvatarRenderState>
{
    private static final ModelPart EMPTY_ROOT = new ModelPart(Collections.emptyList(), Collections.emptyMap());

    public PartModel()
    {
        super(EMPTY_ROOT, RenderTypes::entityTranslucent);
    }

    public PartModel(ModelPart root)
    {
        super(root, RenderTypes::entityTranslucent);
    }

    public PartModel(ModelPart root, Function<Identifier, RenderType> renderType)
    {
        super(root, renderType);
    }

    @Override
    public void setupAnim(AvatarRenderState renderState)
    {
        super.setupAnim(renderState);
    }

    protected static float getAnimationTime(double cycleTime, long offset)
    {
        //Returns between 0-360 in radians depending on far in the "cycle" we are.
        //TODO could probably use ageInTicks as an alternative in the future
        // This currently needs to have cycleTime as a double, most likely due to precision as otherwise the number effectively doesn't update
        return ((float)(((offset + System.currentTimeMillis()) % cycleTime) / cycleTime) * 2f * Mth.PI);
    }

    protected float[] getMotionAngles(Player player, float partialTicks)
    {
        var deltaMovement = player.getDeltaMovement();
       // double xMotion = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * partialTicks - (player.xOld + (player.position().x - player.xOld) * partialTicks);
       // double yMotion = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * partialTicks - (player.yOld + (player.position().y - player.yOld) * partialTicks); //Positive when falling, negative when climbing
        //double zMotion = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * partialTicks - (player.zOld + (player.position().z - player.zOld) * partialTicks);
        double xMotion = deltaMovement.x;
        double yMotion = -deltaMovement.y;
        double zMotion = deltaMovement.z;
        var bodyYaw = Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        //Pretty sure renderYawOffset is actually the way the body is "pointing"
        //In degrees, not bound 0-360, be warned!
        var bodyYawSin = Mth.sin(bodyYaw * Mth.PI / 180F);
        var bodyYawCos = -Mth.cos(bodyYaw * Mth.PI / 180F);
        float xOffset = Mth.clamp((float) yMotion * 10F, -6F, 32F);
        float f1 = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
        float f2 = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;

        if (f1 < 0F) {
            f1 = 0F;
        }

        return new float[] {
                (float) Math.toRadians(f1 / 2.5F + (xOffset + getTailBob(player, partialTicks))),
                (float) Math.toRadians(-f2 / 20F),
                (float) Math.toRadians(f2 / 2F)
        };
    }

    protected float getTailBob(Player player, float partialTicks)
    {
        // 1.21 removed Player's public bob/walkDist fields. Use the walk animation state instead.
        // We intentionally keep this as a soft approximation since the exact camera bob value is
        // now computed in the renderer rather than exposed on the entity.

        // Walk phase in radians-ish; position() is usually increasing with movement.
        float walkPos;
        float walkSpeed;
        try
        {
            // WalkAnimationState exists on LivingEntity/Player in modern versions.
            walkPos = player.walkAnimation.position(partialTicks);
            walkSpeed = player.walkAnimation.speed(partialTicks);
        }
        catch (Throwable ignored)
        {
            // Fallback for mappings / unexpected API differences.
            walkPos = (float) player.tickCount + partialTicks;
            walkSpeed = (float) player.getDeltaMovement().horizontalDistance();
        }

        // Convert to something close to the old cameraYaw effect.
        float cameraYaw = Mth.clamp(walkSpeed, 0.0F, 1.0F);

        return Mth.sin(walkPos * 6F) * 12F * cameraYaw;
    }
}
