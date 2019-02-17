package uk.kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerDataMapMessage {

    private Map<UUID, Outfit> outfitMap;

    public PlayerDataMapMessage(PacketBuffer buf) {
        String tailInfoJson = buf.readString(Integer.MAX_VALUE);
        try {
            this.outfitMap = Tails.gson.fromJson(tailInfoJson, new TypeToken<Map<UUID, Outfit>>() {}.getType());
        } catch (JsonSyntaxException e) {
            Tails.logger.catching(e);
        }
    }

    public PlayerDataMapMessage(Map<UUID, Outfit> outfitMap) {
        this.outfitMap = outfitMap;
    }

    public void toBytes(PacketBuffer buf) {
        String tailInfoJson = Tails.gson.toJson(this.outfitMap);
        buf.writeString(tailInfoJson);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            for (Map.Entry<UUID, Outfit> entry : outfitMap.entrySet()) {
                // TODO Tails.proxy.setActiveOutfit(entry.getKey(), entry.getValue());
            }
        });
    }
}
