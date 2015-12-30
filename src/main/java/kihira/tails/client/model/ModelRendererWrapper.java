/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model;

import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.PartRegistry;
import kihira.tails.client.animation.AnimationClip;
import kihira.tails.client.animation.AnimationSegment;
import kihira.tails.client.animation.interpolation.CosInterpolation;
import kihira.tails.client.model.tail.ModelFluffyTail;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayDeque;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class ModelRendererWrapper extends ModelRenderer {

    private final PartsData.PartType partType;

    public ModelRendererWrapper(ModelBase model, PartsData.PartType partType) {
        super(model);
        this.partType = partType;
        addBox(0, 0, 0, 0, 0, 0); //Adds in a blank box as it's required in certain cases such as rendering arrows in entities
    }

    @Override
    public void render(float p_78785_1_) {
        if (ClientEventHandler.currentEvent != null && ClientEventHandler.currentPartsData != null && ClientEventHandler.currentPlayerTexture != null) {
            PartInfo info = ClientEventHandler.currentPartsData.getPartInfo(partType);
            if (info != null && info.hasPart) {
                // TODO test code
                if (info.animation == null) {
                    HashMap<ModelRenderer, ArrayDeque<AnimationSegment>> map = new HashMap<>();
                    // Bezier
                    //ArrayBlockingQueue<AnimationSegment> animationSegments = new ArrayBlockingQueue<>(1);
                    //animationSegments.add(new AnimationSegment(new double[]{1, 0, 0}, new BezierCurve(new Vector2f(0, 0), new Vector2f(1, (float) Math.toRadians(-180)), new Vector2f(2, (float) Math.toRadians(180)), new Vector2f(3, (float) Math.toRadians(0))), 0, 80));
                    // Cos
                    ArrayDeque<AnimationSegment> animationSegments = new ArrayDeque<>(1);
                    animationSegments.add(new AnimationSegment(new double[]{1, 0, 0}, new CosInterpolation(1, -1), 0, 80));
                    animationSegments.add(new AnimationSegment(new double[]{1, 0, 0}, new CosInterpolation(-1, 1), 80, 80));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tailBase, animationSegments);
                    info.animation = new AnimationClip(map, true);
                }
                info.animation.update(Minecraft.getMinecraft().theWorld.getTotalWorldTime(), ClientEventHandler.currentEvent.partialRenderTick);

                PartRegistry.getRenderPart(info.partType, info.typeid).render(ClientEventHandler.currentEvent.entityPlayer, info, 0, 0, 0, ClientEventHandler.currentEvent.partialRenderTick);

                Minecraft.getMinecraft().renderEngine.bindTexture(ClientEventHandler.currentPlayerTexture);
            }
        }
    }
}
