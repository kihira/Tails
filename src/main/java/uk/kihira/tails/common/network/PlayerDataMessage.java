package uk.kihira.tails.common.network;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.UUID;
import java.util.function.Supplier;

public class PlayerDataMessage {

    private UUID uuid;
    private Outfit outfit;
    private boolean shouldRemove;

    public PlayerDataMessage(PacketBuffer buf) {
        uuid = buf.readUniqueId();
        String tailInfoJson = buf.readString(Integer.MAX_VALUE);
        if (!Strings.isNullOrEmpty(tailInfoJson)) {
            try {
                outfit = Tails.gson.fromJson(tailInfoJson, Outfit.class);
            } catch (JsonSyntaxException e) {
                Tails.logger.warn(e);
            }
        }
        else outfit = null;
    }

    public PlayerDataMessage(UUID uuid, Outfit outfit, boolean shouldRemove) {
        this.uuid = uuid;
        this.outfit = outfit;
        this.shouldRemove = shouldRemove;
    }

    public void toBytes(PacketBuffer buf) {
        String tailInfoJson = outfit == null ? "" : Tails.gson.toJson(this.outfit);

        buf.writeUniqueId(uuid);
        buf.writeString(tailInfoJson);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (shouldRemove) {
                // todo Tails.proxy.removeActiveOutfit(uuid);
            }
            else if (outfit != null) {
                // todo Tails.proxy.setActiveOutfit(uuid, outfit);
                //Tell other clients about the change
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                    Tails.networkWrapper.sendTo(new PlayerDataMessage(uuid, outfit, false), ctx.get().getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        });
    }
}
