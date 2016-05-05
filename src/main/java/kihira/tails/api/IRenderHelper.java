/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.api;

import kihira.tails.client.render.RenderPart;
import kihira.tails.common.PartInfo;
import net.minecraft.entity.EntityLivingBase;

public interface IRenderHelper {

    void onPreRenderTail(EntityLivingBase entity, RenderPart tail, PartInfo info, double x, double y, double z);

}
