package kihira.tails.client.render;

import kihira.tails.client.model.ModelBirdTail;

public class RenderBirdTail extends RenderTail {

    private String[] skinNames = { "birdTail" };

    public RenderBirdTail() {
        super("bird", new ModelBirdTail());
    }

    @Override
    public String[] getTextureNames(int subid) {
        return skinNames;
    }

    @Override
    public int getAvailableSubTypes() {
        return 0;
    }
}
