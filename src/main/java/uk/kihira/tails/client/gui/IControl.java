package uk.kihira.tails.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IControl<V> {

    void setValue(V newValue);

    V getValue();
}
