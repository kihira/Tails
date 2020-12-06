package uk.kihira.tails.common.network;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.UUID;
import java.util.function.Supplier;

public final class PlayerDataMessage 
{
    private UUID uuid;
    private Outfit outfit;
    private boolean shouldRemove;

    public PlayerDataMessage() {}
    public PlayerDataMessage(UUID uuid, Outfit outfit, boolean shouldRemove)
    {
        this.uuid = uuid;
        this.outfit = outfit;
        this.shouldRemove = shouldRemove;
    }

    public PlayerDataMessage(PacketBuffer buf) 
    {
        this.uuid = buf.readUniqueId();
        String tailInfoJson = buf.readString();
        if (!Strings.isNullOrEmpty(tailInfoJson)) 
        {
            try 
            {
                this.outfit = Tails.GSON.fromJson(tailInfoJson, Outfit.class);
            } catch (JsonSyntaxException e) 
            {
                Tails.LOGGER.catching(e);
            }
        }
        else 
        {
            this.outfit = null;
        }
    }

    public void encode(PacketBuffer buf) 
    {
        buf.writeUniqueId(this.uuid);
        buf.writeString(outfit == null ? "" : Tails.GSON.toJson(this.outfit));
    }

    public void handle(Supplier<Context> ctx) 
    {
        ctx.get().enqueueWork(() ->
        {
            if (this.shouldRemove)
            {
                Tails.proxy.removeActiveOutfit(this.uuid);
            } 
            else if (this.outfit != null) 
            {
                Tails.proxy.setActiveOutfit(this.uuid, this.outfit);
                //Tell other clients about the change
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) 
                {
                    TailsPacketHandler.networkWrapper.send(PacketDistributor.ALL.noArg(), new PlayerDataMessage(this.uuid, this.outfit, false));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
