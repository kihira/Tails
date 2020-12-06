package uk.kihira.tails.common.network;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.List;
import java.util.function.Supplier;

public class LibraryEntriesMessage {

    private List<LibraryEntryData> entries;
    private boolean delete; //Only used when sending to server

    public LibraryEntriesMessage() {}
    public LibraryEntriesMessage(List<LibraryEntryData> entries, boolean delete) {
        this.entries = entries;
        this.delete = delete;
    }

    public LibraryEntriesMessage(PacketBuffer buf) 
    {
        try 
        {
            this.entries = Tails.GSON.fromJson(buf.readString(), new TypeToken<List<LibraryEntryData>>() {}.getType());
        } catch (JsonParseException e) 
        {
            Tails.LOGGER.catching(e);
        }
        this.delete = buf.readBoolean();
    }

    public void encode(PacketBuffer buf) 
    {
        buf.writeString(Tails.GSON.toJson(this.entries, new TypeToken<List<LibraryEntryData>>() {}.getType()));
        buf.writeBoolean(this.delete);
    }

    public void handle(Supplier<Context> ctx) 
    {
        ctx.get().enqueueWork(() -> 
        {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) 
            {
                //Yeah this isn't exactly a nice way of doing this.
                for (LibraryEntryData entry : this.entries) 
                {
                    entry.remoteEntry = true;
                }

                //We add server entries to the uk.kihira.tails.client
                Tails.proxy.getLibraryManager().removeRemoteEntries();
                Tails.proxy.getLibraryManager().addEntries(this.entries);
            }
            //Server
            else 
            {
                if (this.delete) 
                {
                    Tails.LOGGER.debug("Removing Library Entries: %d", this.entries.size());
                    Tails.proxy.getLibraryManager().libraryEntries.removeAll(this.entries);
                }
                else 
                {
                    Tails.LOGGER.debug("Adding Library Entries: %d", this.entries.size());
                    Tails.proxy.getLibraryManager().addEntries(this.entries);
                }
                Tails.proxy.getLibraryManager().saveLibrary();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
