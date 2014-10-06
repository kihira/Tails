/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.model;

import kihira.tails.client.ClientEventHandler;
import kihira.tails.common.TailInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelRenderer2 extends ModelRenderer {

    public ModelRenderer2(ModelBase p_i1173_1_) {
        super(p_i1173_1_);
    }

    @Override
    public void render(float p_78785_1_) {
        if (ClientEventHandler.currentTailInfo != null && ClientEventHandler.currentEvent != null && ClientEventHandler.currentPlayerTexture != null) {
            TailInfo info = ClientEventHandler.currentTailInfo;

            int type = info.typeid;
            type = type > ClientEventHandler.tailTypes.length ? 0 : type;

            ClientEventHandler.tailTypes[type].render(ClientEventHandler.currentEvent.entityPlayer, info, 0, 0, 0, ClientEventHandler.currentEvent.partialRenderTick);

            Minecraft.getMinecraft().renderEngine.bindTexture(ClientEventHandler.currentPlayerTexture);
        }
    }
}
