package uk.kihira.tails.client.outfit;

import java.util.ArrayList;
import java.util.UUID;

public final class Outfit
{
    public final UUID id;
    public String name;
    public String description;
    public ArrayList<OutfitPart> parts;

    public Outfit()
    {
        id = UUID.randomUUID();
        parts = new ArrayList<>();
    }

    // todo make a client only multimap of mountpoint <-> outfitpart?
}
