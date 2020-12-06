package uk.kihira.tails.common.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerDataMapMessage 
{
    private Map<UUID, Outfit> outfitMap;

    public PlayerDataMapMessage() {}
    public PlayerDataMapMessage(Map<UUID, Outfit> outfitMap) 
    {
        this.outfitMap = outfitMap;
    }

    public PlayerDataMapMessage(PacketBuffer buf) 
    {
        String tailInfoJson = buf.readString();
        try 
        {
            this.outfitMap = Tails.GSON.fromJson(tailInfoJson, new TypeToken<Map<UUID, Outfit>>() {}.getType());
        } catch (JsonSyntaxException e) 
        {
            Tails.LOGGER.catching(e);
        }
    }

    public void encode(PacketBuffer buf)
    {
        String tailInfoJson = Tails.GSON.toJson(this.outfitMap);
        buf.writeString(tailInfoJson);
    }

    public void handle(Supplier<Context> ctx) 
    {
        ctx.get().enqueueWork(() -> 
        {
            for (Map.Entry<UUID, Outfit> entry : outfitMap.entrySet()) 
            {
                Tails.proxy.setActiveOutfit(entry.getKey(), entry.getValue());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
