// 경로: src/main/java/com/akashiro/tails/client/render/IRenderHelper.java
package com.akashiro.tails.client.render;

import com.akashiro.tails.common.data.PartInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;

/**
 * 파츠 렌더 직전 호출되는 콜백 인터페이스.
 * 1.12.2 api/IRenderHelper 이식.
 */
public interface IRenderHelper {
    void onPreRenderPart(PoseStack poseStack, LivingEntity entity, RenderPart renderPart,
                         PartInfo info, double x, double y, double z);
}
