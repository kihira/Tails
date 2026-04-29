package com.akashiro.tails.common;

import com.akashiro.tails.Tails;
import com.akashiro.tails.common.data.PartInfo;
import com.akashiro.tails.common.data.PartsData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class TailAbilityHandler {

    private static final UUID FOX_SPEED_ID = UUID.fromString("0bce3dd5-01d8-4e93-9649-ea7bb6a8f1d0");
    private static final UUID DRAGON_ARMOR_ID = UUID.fromString("b4687f21-cf98-4d07-b2b8-71a2ff1b3a8f");
    private static final UUID DRAGON_KNOCKBACK_ID = UUID.fromString("c1d8f131-af4b-4df8-8136-54fc43cb9958");
    private static final UUID DEVIL_ATTACK_ID = UUID.fromString("ea20dcf5-7ef5-4d3c-944a-21b0f1687247");
    private static final UUID SHARK_SWIM_SPEED_ID = UUID.fromString("a624d7a2-6d93-430e-88bc-ea29ef9db11e");

    private static final double FOX_SPEED_BONUS = 0.18D;
    private static final double NINE_TAILS_SPEED_BONUS = 0.30D;
    private static final double DEVIL_ATTACK_BONUS = 3.0D;
    private static final double BUNNY_JUMP_BONUS = 0.12D;
    private static final double NINE_TAILS_JUMP_BONUS = 0.18D;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        TailProfile profile = TailProfile.fromPlayer(player);

        syncModifier(
                player,
                Attributes.MOVEMENT_SPEED,
                FOX_SPEED_ID,
                "tails.fox_speed",
                profile.movementSpeedBonus,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        syncModifier(
                player,
                Attributes.ARMOR,
                DRAGON_ARMOR_ID,
                "tails.dragon_armor",
                profile.armorBonus,
                AttributeModifier.Operation.ADDITION);
        syncModifier(
                player,
                Attributes.KNOCKBACK_RESISTANCE,
                DRAGON_KNOCKBACK_ID,
                "tails.dragon_knockback_resistance",
                profile.knockbackResistanceBonus,
                AttributeModifier.Operation.ADDITION);
        syncModifier(
                player,
                Attributes.ATTACK_DAMAGE,
                DEVIL_ATTACK_ID,
                "tails.devil_attack",
                profile.attackDamageBonus,
                AttributeModifier.Operation.ADDITION);
        syncModifier(
                player,
                ForgeMod.SWIM_SPEED.get(),
                SHARK_SWIM_SPEED_ID,
                "tails.shark_swim_speed",
                profile.swimSpeedBonus,
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (profile.refreshAirInWater && player.isInWaterOrBubble()) {
            player.setAirSupply(player.getMaxAirSupply());
        }

        if (profile.slowFalling
                && !player.onGround()
                && !player.getAbilities().flying
                && !player.isFallFlying()
                && !player.isInWaterOrBubble()) {
            double downwardSpeed = player.getDeltaMovement().y;
            if (downwardSpeed < -0.12D) {
                player.setDeltaMovement(
                        player.getDeltaMovement().x,
                        Math.max(downwardSpeed * 0.6D, -0.12D),
                        player.getDeltaMovement().z);
                player.hurtMarked = true;
            }
            player.fallDistance = Math.min(player.fallDistance, 1.0F);
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }

        TailProfile profile = TailProfile.fromPlayer(player);
        if (profile.jumpBonus > 0.0D) {
            player.setDeltaMovement(player.getDeltaMovement().add(0.0D, profile.jumpBonus, 0.0D));
            player.hurtMarked = true;
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }

        TailProfile profile = TailProfile.fromPlayer(player);
        if (profile.cancelFallDamage) {
            event.setCanceled(true);
        } else if (profile.slowFalling) {
            event.setDistance(event.getDistance() * 0.35F);
        }
    }

    private static void syncModifier(
            Player player,
            Attribute attribute,
            UUID id,
            String name,
            double amount,
            AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        AttributeModifier existing = instance.getModifier(id);
        if (amount == 0.0D) {
            if (existing != null) {
                instance.removeModifier(id);
            }
            return;
        }

        if (existing != null) {
            if (existing.getAmount() == amount && existing.getOperation() == operation) {
                return;
            }
            instance.removeModifier(id);
        }

        instance.addPermanentModifier(new AttributeModifier(id, name, amount, operation));
    }

    private static final class TailProfile {
        private static final TailProfile NONE = new TailProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, false, false, false);

        private final double movementSpeedBonus;
        private final double attackDamageBonus;
        private final double jumpBonus;
        private final double armorBonus;
        private final double knockbackResistanceBonus;
        private final double swimSpeedBonus;
        private final boolean cancelFallDamage;
        private final boolean slowFalling;
        private final boolean refreshAirInWater;

        private TailProfile(
                double movementSpeedBonus,
                double attackDamageBonus,
                double jumpBonus,
                double armorBonus,
                double knockbackResistanceBonus,
                double swimSpeedBonus,
                boolean cancelFallDamage,
                boolean slowFalling,
                boolean refreshAirInWater) {
            this.movementSpeedBonus = movementSpeedBonus;
            this.attackDamageBonus = attackDamageBonus;
            this.jumpBonus = jumpBonus;
            this.armorBonus = armorBonus;
            this.knockbackResistanceBonus = knockbackResistanceBonus;
            this.swimSpeedBonus = swimSpeedBonus;
            this.cancelFallDamage = cancelFallDamage;
            this.slowFalling = slowFalling;
            this.refreshAirInWater = refreshAirInWater;
        }

        private static TailProfile fromPlayer(Player player) {
            PartsData data = Tails.PROXY.getPartsData(player.getUUID());
            if (data == null || !data.hasPart(PartsData.PartType.TAIL)) {
                return NONE;
            }

            PartInfo info = data.getPartInfo(PartsData.PartType.TAIL);
            if (info == null || !info.hasPart) {
                return NONE;
            }

            return fromTail(info.typeid, info.subid);
        }

        private static TailProfile fromTail(int typeId, int subId) {
            return switch (typeId) {
                case 0 -> fluffyProfile(subId);
                case 1 -> new TailProfile(0.0D, 0.0D, 0.0D, 4.0D, 0.35D, 0.0D, false, false, false);
                case 2 -> NONE;
                case 3 -> new TailProfile(0.0D, DEVIL_ATTACK_BONUS, 0.0D, 0.0D, 0.0D, 0.0D, false, false, false);
                case 4 -> new TailProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, true, false, false);
                case 5 -> new TailProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, false, true, false);
                case 6 -> new TailProfile(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.35D, false, false, true);
                case 7 -> new TailProfile(0.0D, 0.0D, BUNNY_JUMP_BONUS, 0.0D, 0.0D, 0.0D, false, false, false);
                default -> NONE;
            };
        }

        private static TailProfile fluffyProfile(int subId) {
            if (subId == 2) {
                return new TailProfile(
                        NINE_TAILS_SPEED_BONUS,
                        DEVIL_ATTACK_BONUS,
                        NINE_TAILS_JUMP_BONUS,
                        0.0D,
                        0.0D,
                        0.0D,
                        false,
                        false,
                        false);
            }

            return new TailProfile(FOX_SPEED_BONUS, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, false, false, false);
        }
    }
}
