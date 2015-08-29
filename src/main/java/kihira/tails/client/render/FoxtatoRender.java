package kihira.tails.client.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kihira.tails.client.FakeEntity;
import kihira.tails.client.PartRegistry;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.item.TinyPotatoRenderEvent;

public class FoxtatoRender {

    private FoxtatoFakeEntity fakeEntity;
    private PartInfo tailPartInfo = new PartInfo(true, 0, 0, 0, new int[]{-5480951, -6594259, -5197647}, PartsData.PartType.TAIL, null);
    private PartInfo earPartInfo = new PartInfo(true, 0, 0, 0, new int[]{-5480951, 0xFF000000, -5197647}, PartsData.PartType.EARS, null);

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        if (fakeEntity != null) {
            fakeEntity.setDead();
            fakeEntity = null;
        }
    }

    @SubscribeEvent
    public void onPotatoRender(TinyPotatoRenderEvent e) {
        if (e.name.equalsIgnoreCase("foxtato")) {
            if (fakeEntity == null) {
                fakeEntity = new FoxtatoFakeEntity(Minecraft.getMinecraft().theWorld);
            }
            RenderPart foxTailRender = PartRegistry.getRenderPart(PartsData.PartType.TAIL, 0);
            RenderPart foxEarRender = PartRegistry.getRenderPart(PartsData.PartType.EARS, 0);

            foxTailRender.render(fakeEntity, tailPartInfo, e.x, e.y, e.z, e.partTicks);
            foxEarRender.render(fakeEntity, earPartInfo, e.x, e.y, e.z, e.partTicks);
            GL11.glColor3f(1f, 0f, 1f);
        }
    }

    public static class FoxtatoFakeEntity extends FakeEntity {
        public FoxtatoFakeEntity(World world) {
            super(world);
        }
    }
}
