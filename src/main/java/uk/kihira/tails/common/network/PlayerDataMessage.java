package uk.kihira.tails.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.Tails;

import java.util.UUID;

import static uk.kihira.tails.common.network.Codecs.UUID_CODEC;

public record PlayerDataMessage(UUID uuid, Outfit outfit, boolean shouldRemove) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PlayerDataMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Tails.MOD_ID, PlayerDataMessage.class.getSimpleName().toLowerCase()));

    public static final StreamCodec<ByteBuf, PlayerDataMessage> STREAM_CODEC = StreamCodec.composite(
            UUID_CODEC,
            PlayerDataMessage::uuid,
            ByteBufCodecs.STRING_UTF8,
            data -> data.outfit() == null ? "" : Tails.GSON.toJson(data.outfit()),
            ByteBufCodecs.BOOL,
            PlayerDataMessage::shouldRemove,
            (uuid, outfitJson, shouldRemove) -> new PlayerDataMessage(uuid, outfitJson.isEmpty() ? null : Tails.GSON.fromJson(outfitJson, Outfit.class), shouldRemove)
    );

    public PlayerDataMessage(final FriendlyByteBuf buffer)
    {
        this(buffer.readUUID(), Tails.GSON.fromJson(buffer.readUtf(), Outfit.class), buffer.readBoolean());
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handleDataCommon(final PlayerDataMessage data, final IPayloadContext context)
    {
        if (context.connection().getDirection().isServerbound())
        {
            // Only allow players to set their own data
            if (!context.player().getUUID().equals(data.uuid()))
            {
                context.disconnect(Component.translatable("tails.networking.failed"));
                return;
            }
        }

        processMessage(data);
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
        PacketDistributor.sendToAllPlayers(data);
    }
}
