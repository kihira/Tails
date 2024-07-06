package uk.kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public record PlayerDataMapMessage(Map<UUID, Outfit> outfitMap) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(Tails.MOD_ID, PlayerDataMapMessage.class.getName());

    public PlayerDataMapMessage(FriendlyByteBuf buffer)
    {
        //noinspection unchecked
        this((Map<UUID, Outfit>) Tails.GSON.fromJson(buffer.readUtf(), new TypeToken<Map<UUID, Outfit>>() {}.getType()));
    }

    @Override
    public void write(final FriendlyByteBuf buffer)
    {
        buffer.writeUtf(Tails.GSON.toJson(outfitMap));
    }

    @Nonnull
    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handleDataClient(final PlayerDataMapMessage data, final PlayPayloadContext context)
    {
        context.workHandler().submitAsync(() ->
            {
                for (Map.Entry<UUID, Outfit> entry : data.outfitMap().entrySet())
                {
                    Tails.proxy.setActiveOutfit(entry.getKey(), entry.getValue());
                }
            })
            .exceptionally(e ->
            {
                context.packetHandler().disconnect(Component.translatable("tails.networking.failed", e.getMessage()));
                return null;
            });
    }
}
