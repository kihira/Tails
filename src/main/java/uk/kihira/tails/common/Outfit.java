package uk.kihira.tails.common;

import uk.kihira.tails.client.OutfitPart;

import java.util.ArrayList;

public class Outfit {
    public String name;
    public String description;
    public ArrayList<OutfitPart> parts;

    // todo make a client only multimap of mountpoint <-> outfitpart?
}
