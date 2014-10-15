/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.client.ClientEventHandler;
import kihira.tails.client.PartRegistry;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelRenderer2 extends ModelRenderer {

    private PartsData.PartType partType;

    public ModelRenderer2(ModelBase p_i1173_1_, PartsData.PartType partType) {
        super(p_i1173_1_);
        this.partType = partType;
    }

    @Override
    public void render(float p_78785_1_) {
        if (ClientEventHandler.currentEvent != null && ClientEventHandler.currentPartsData != null && ClientEventHandler.currentPlayerTexture != null) {
            PartInfo info = ClientEventHandler.currentPartsData.getPartInfo(partType);
            if (info != null && info.hasPart) {
                PartRegistry.getRenderPart(info.partType, info.typeid).render(ClientEventHandler.currentEvent.entityPlayer, info, 0, 0, 0, ClientEventHandler.currentEvent.partialRenderTick);

                Minecraft.getMinecraft().renderEngine.bindTexture(ClientEventHandler.currentPlayerTexture);
            }
        }
    }
}
