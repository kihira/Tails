package uk.kihira.tails.client.outfit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Outfit
{
    public final UUID id;
    public String name;
    public String description;
    private final ArrayList<OutfitPart> parts;

    public Outfit()
    {
        this.id = UUID.randomUUID();
        this.parts = new ArrayList<>();
    }

    public List<OutfitPart> getParts()
    {
        return this.parts;
    }

    public Outfit addPart(OutfitPart part)
    {
        this.parts.add(part);
        return this;
    }

    public Outfit removePart(OutfitPart part)
    {
        this.parts.remove(part);
        return this;
    }

    // todo make a client only multimap of mountpoint <-> outfitpart?
}
