package uk.kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.Tails;
import uk.kihira.tails.common.OutfitManager;

import java.util.Map;
import java.util.UUID;

public record PlayerDataMapMessage(String outfitMap) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PlayerDataMapMessage> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Tails.MOD_ID, PlayerDataMapMessage.class.getSimpleName().toLowerCase()));

    public static final StreamCodec<ByteBuf, PlayerDataMapMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PlayerDataMapMessage::outfitMap,
            PlayerDataMapMessage::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handleDataClient(final PlayerDataMapMessage data, final IPayloadContext context)
    {
        var outfitMap = (Map<UUID, Outfit>) Tails.GSON.fromJson(data.outfitMap, new TypeToken<Map<UUID, Outfit>>() {}.getType());
        for (Map.Entry<UUID, Outfit> entry : outfitMap.entrySet())
        {
            OutfitManager.setActiveOutfit(entry.getKey(), entry.getValue());
        }
    }
}
