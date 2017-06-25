package uk.kihira.tails.common;

import uk.kihira.tails.client.OutfitPart;

import java.util.ArrayList;
import java.util.UUID;

public class Outfit {
    public final UUID id;
    public String name;
    public String description;
    public ArrayList<OutfitPart> parts;

    public Outfit() {
        id = UUID.randomUUID();
    }

    // todo make a client only multimap of mountpoint <-> outfitpart?
}
