/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model;

import com.google.common.collect.Sets;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.PartRegistry;
import kihira.tails.client.animation.*;
import kihira.tails.client.animation.interpolation.CosInterpolation;
import kihira.tails.client.model.tail.ModelFluffyTail;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.List;
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
                if (info.animation == null || Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                    final HashMap<ModelRenderer, SortedSet<AnimationSegment>> map = new HashMap<>();
                    // Bezier
                    //SortedSet<AnimationSegment> animationSegments = Sets.newTreeSet();
                    //animationSegments.add(new AnimationSegment(Variable.ROTY, new BezierCurve(new Vector2f(0, 0), new Vector2f(1, (float) Math.toRadians(-180)), new Vector2f(2, (float) Math.toRadians(180)), new Vector2f(3, (float) Math.toRadians(0))), 0, 80));
                    //map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tailBase, animationSegments);

                    // Cos
                    SortedSet<AnimationSegment> animationSegments = Sets.newTreeSet();
                    animationSegments.add(LoopingSegment.createLoopingSegment(200, new AnimationSegment(Variable.ROTY, 0, 80, new CosInterpolation(1, -1)), new AnimationSegment(Variable.ROTY, 80, 80, new CosInterpolation(-1, 1))));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tailBase, animationSegments);

                    SortedSet<AnimationSegment> animationSegments1 = Sets.newTreeSet();
                    animationSegments1.add(LoopingSegment.createLoopingSegment(200,
                            new AnimationSegment(Variable.ROTY, 10, 80, new CosInterpolation((float) Math.toRadians(10), (float) Math.toRadians(-10))),
                            new AnimationSegment(Variable.ROTY, 90, 80, new CosInterpolation((float) Math.toRadians(-10), (float) Math.toRadians(10)))));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tail1, animationSegments1);

                    SortedSet<AnimationSegment> animationSegments2 = Sets.newTreeSet();
                    animationSegments2.add(LoopingSegment.createLoopingSegment(200,
                            new AnimationSegment(Variable.ROTY, 15, 80, new CosInterpolation((float) Math.toRadians(10), (float) Math.toRadians(-10))),
                            new AnimationSegment(Variable.ROTY, 95, 80, new CosInterpolation((float) Math.toRadians(-10), (float) Math.toRadians(10)))));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tail2, animationSegments2);

                    SortedSet<AnimationSegment> animationSegments3 = Sets.newTreeSet();
                    animationSegments3.add(LoopingSegment.createLoopingSegment(200,
                            new AnimationSegment(Variable.ROTY, 20, 80, new CosInterpolation((float) Math.toRadians(5), (float) Math.toRadians(-5))),
                            new AnimationSegment(Variable.ROTY, 100, 80, new CosInterpolation((float) Math.toRadians(-5), (float) Math.toRadians(5)))));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tail3, animationSegments3);

                    SortedSet<AnimationSegment> animationSegments4 = Sets.newTreeSet();
                    animationSegments4.add(LoopingSegment.createLoopingSegment(200,
                            new AnimationSegment(Variable.ROTY, 30, 80, new CosInterpolation((float) Math.toRadians(10), (float) Math.toRadians(-10))),
                            new AnimationSegment(Variable.ROTY, 110, 80, new CosInterpolation((float) Math.toRadians(-10), (float) Math.toRadians(10)))));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tail4, animationSegments4);

                    SortedSet<AnimationSegment> animationSegments5 = Sets.newTreeSet();
                    animationSegments5.add(LoopingSegment.createLoopingSegment(200,
                            new AnimationSegment(Variable.ROTY, 40, 80, new CosInterpolation((float) Math.toRadians(10), (float) Math.toRadians(-10))),
                            new AnimationSegment(Variable.ROTY, 120, 80, new CosInterpolation((float) Math.toRadians(-10), (float) Math.toRadians(10)))));
                    map.put(((ModelFluffyTail) PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0).modelPart).tail5, animationSegments5);

                    info.animation = new AnimationStateMachine(ClientEventHandler.currentEvent.entityLiving,
                            new HashMap<String, AnimationState>() {{ put("idle", new AnimationState(new AnimationClip(map, true))); }},
                            new HashMap<AnimationState, List<Transition>>(),
                            "idle");
                }
                info.animation.animate(ClientEventHandler.currentEvent.partialRenderTick);

                PartRegistry.getRenderPart(info.partType, info.typeid).render(ClientEventHandler.currentEvent.entityPlayer, info, 0, 0, 0, ClientEventHandler.currentEvent.partialRenderTick);

                Minecraft.getMinecraft().renderEngine.bindTexture(ClientEventHandler.currentPlayerTexture);
            }
        }
    }
}
