/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.api;

import uk.kihira.tails.client.render.RenderPart;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.entity.EntityLivingBase;

public interface IRenderHelper {

    void onPreRenderTail(EntityLivingBase entity, RenderPart tail, PartInfo info, double x, double y, double z);

}
