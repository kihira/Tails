package uk.kihira.tails.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nonnull;
import java.util.UUID;

public record PlayerDataMessage(UUID uuid, Outfit outfit, boolean shouldRemove) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(Tails.MOD_ID, PlayerDataMessage.class.getName());

    public PlayerDataMessage(final FriendlyByteBuf buffer)
    {
        this(buffer.readUUID(), Tails.GSON.fromJson(buffer.readUtf(), Outfit.class), buffer.readBoolean());
    }

    @Override
    public void write(final FriendlyByteBuf buffer)
    {
        buffer.writeUUID(uuid());
        buffer.writeUtf(Tails.GSON.toJson(outfit()));
        buffer.writeBoolean(shouldRemove());
    }

    @Nonnull
    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handleDataClient(final PlayerDataMessage data, final PlayPayloadContext context)
    {
        context.workHandler().submitAsync(() ->
            {
                processMessage(data);
            })
            .exceptionally(e ->
            {
                context.packetHandler().disconnect(Component.translatable("tails.networking.failed", e.getMessage()));
                return null;
            });
    }

    public static void handleDataServer(final PlayerDataMessage data, final PlayPayloadContext context)
    {
        // Only allow players to set their own data
        if (context.player().isEmpty() || context.player().get().getUUID() != data.uuid())
        {
            context.packetHandler().disconnect(Component.translatable("tails.networking.failed"));
            return;
        }

        context.workHandler().submitAsync(() ->
                {
                    processMessage(data);
                })
                .exceptionally(e ->
                {
                    context.packetHandler().disconnect(Component.translatable("tails.networking.failed", e.getMessage()));
                    return null;
                });
    }

    private static void processMessage(final PlayerDataMessage data)
    {
        if (data.shouldRemove())
        {
            Tails.proxy.removeActiveOutfit(data.uuid());
        }
        else if (data.outfit() != null)
        {
            Tails.proxy.setActiveOutfit(data.uuid(), data.outfit());
        }
        // Forward packet onto all clients
        PacketDistributor.ALL.noArg().send(data);
    }
}
