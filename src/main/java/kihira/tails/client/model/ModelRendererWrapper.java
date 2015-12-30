/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.PartRegistry;
import kihira.tails.client.animation.AnimationClip;
import kihira.tails.client.animation.AnimationSegment;
import kihira.tails.client.animation.AnimationStateMachine;
import kihira.tails.client.animation.AnimationVariable;
import kihira.tails.client.animation.interpolation.CosInterpolation;
import kihira.tails.client.animation.interpolation.IInterpolation;
import kihira.tails.client.model.tail.ModelFluffyTail;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.SortedSet;

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
                    final HashMap<ModelRenderer, SortedSet<AnimationSegment>> map = new HashMap<>();
                    // Bezier
                    //SortedSet<AnimationSegment> animationSegments = new SortedSet<>(1);
                    //animationSegments.add(new AnimationSegment(new double[]{1, 0, 0}, new BezierCurve(new Vector2f(0, 0), new Vector2f(1, (float) Math.toRadians(-180)), new Vector2f(2, (float) Math.toRadians(180)), new Vector2f(3, (float) Math.toRadians(0))), 0, 80));
                    // Cos
                    SortedSet<AnimationSegment> animationSegments = Sets.newTreeSet();
                    animationSegments.add(new AnimationSegment(AnimationVariable.ROTY, new CosInterpolation(1, -1), 0, 80));
                    animationSegments.add(new AnimationSegment(AnimationVariable.ROTY, new CosInterpolation(-1, 1), 80, 80));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tailBase, animationSegments);
                    info.animation = new AnimationStateMachine(new HashMap<String, AnimationClip>() {{ put("idle", new AnimationClip(map, true)); }}, HashBasedTable.<AnimationClip, AnimationClip, IInterpolation>create(),"idle");
                }
                info.animation.animate(ClientEventHandler.currentEvent.partialRenderTick);

                PartRegistry.getRenderPart(info.partType, info.typeid).render(ClientEventHandler.currentEvent.entityPlayer, info, 0, 0, 0, ClientEventHandler.currentEvent.partialRenderTick);

                Minecraft.getMinecraft().renderEngine.bindTexture(ClientEventHandler.currentPlayerTexture);
            }
        }
    }
}
